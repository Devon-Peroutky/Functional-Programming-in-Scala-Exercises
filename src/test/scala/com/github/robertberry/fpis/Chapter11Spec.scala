package com.github.robertberry.fpis

import org.specs2.Specification
import org.specs2.specification.Fragments
import Chapter11._

import scala.util.Try

class Chapter11Spec extends Specification {
  override def is: Fragments = "optionMonad unit" ! {
    optionMonad.unit(1) mustEqual Some(1)
  } ^ "optionMonad flatMap" ! {
    optionMonad.flatMap(Some(1)) { a =>
      Some(a + 5)
    } mustEqual Some(6)
  }
}

class MonadExtensions1Spec extends Specification {
  override def is: Fragments = "sequence over Option (no None)" ! {
    optionMonad.sequence(List(Some(1), Some(2), Some(3))) mustEqual Some(List(1, 2, 3))
  } ^ "sequence over Option (with None)" ! {
    optionMonad.sequence(List(Some(1), None, Some(3))) mustEqual None
  } ^ "sequence over List" ! {
    listMonad.sequence(List(List(1, 2, 3), List(4, 5, 6))) mustEqual List(
      List(1, 4),
      List(1, 5),
      List(1, 6),
      List(2, 4),
      List(2, 5),
      List(2, 6),
      List(3, 4),
      List(3, 5),
      List(3, 6)
    )
  } ^ "traverse over Option (no None)" ! {
    optionMonad.traverse(List("1", "2", "3")) { s =>
      Try {
        s.toInt
      }.toOption
    } mustEqual Some(List(1, 2, 3))
  } ^ "traverse over Option (with None)" ! {
    optionMonad.traverse(List("1", "a2", "3")) { s =>
      Try {
        s.toInt
      }.toOption
    } mustEqual None
  }
}

class MonadExtensions2Spec extends Specification {
  override def is: Fragments = "replicateM over Option" ! {
    (optionMonad.replicateM(5, Some(1)) mustEqual Some(List(1, 1, 1, 1, 1))) and
      (optionMonad.replicateM(5, None) mustEqual None)
  } ^ "replicateM over List" ! {
    listMonad.replicateM(2, List(1, 2, 3)) mustEqual List(
      List(1, 1),
      List(1, 2),
      List(1, 3),
      List(2, 1),
      List(2, 2),
      List(2, 3),
      List(3, 1),
      List(3, 2),
      List(3, 3)
    )
  }
}

class MonadExtensions3Spec extends Specification {
  override def is: Fragments = "filterM over Option (no None)" ! {
    optionMonad.filterM(List("1", "2", "3")) { s =>
      Try {
        s.toInt
      }.toOption.map(_ > 1)
    } mustEqual Some(List("2", "3"))
  } ^ "filterM over Option (with None)" ! {
    optionMonad.filterM(List("1", "b", "3")) { s =>
      Try {
        s.toInt
      }.toOption.map(_ > 1)
    } mustEqual None
  } ^ "filterM over List" ! {
    listMonad.filterM(List(2, 1, 0, -1, -2)) { n =>
      List(n > 0)
    } mustEqual List(List(2, 1))
  }
}

class MonadExtensions4Spec extends Specification {
  override def is: Fragments = "compose over Option" ! {
    val f = { s: String => Try { s.toInt }.toOption }
    val g = { n: Int => if (n == 0) None else Some(1 / n) }
    val h = optionMonad.compose(f, g)

    (h("3") mustEqual Some(1 / 3)) and
      (h("0") mustEqual None) and
      (h("a") mustEqual None)
  } ^ "compose over List" ! {
    val f = { s: String => s.toList }
    val g = { c: Char => List(c.toInt, -c.toInt) }
    val h = listMonad.compose(f, g)

    h("hello") mustEqual List(104, -104, 101, -101, 108, -108, 108, -108, 111, -111)
  }
}

class MonadExtensions5Spec extends Specification {
  override def is: Fragments = "flatMap over Option" ! {
    (Some(1) flatMap { n =>
      Some(n + 5)
    } mustEqual Some(6)) and (Option.empty[Int] flatMap { n =>
      Some(n + 5)
    } mustEqual None) and (Some(1) flatMap { n =>
      None
    } mustEqual None)
  }
}

class MonadExtensions6Spec extends Specification {
  override def is: Fragments = "join over Option" ! {
    (optionMonad.join(Some(Some(1))) mustEqual Some(1)) and
      (optionMonad.join(None) mustEqual None) and
      (optionMonad.join(Some(None)) mustEqual None)
  } ^ "join over List" ! {
    listMonad.join(List(List(1, 2), List(3, 4), List(5))) mustEqual List(1, 2, 3, 4, 5)
  }
}