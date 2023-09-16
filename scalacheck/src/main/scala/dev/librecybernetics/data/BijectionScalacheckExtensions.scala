package dev.librecybernetics.data

import org.scalacheck.{Arbitrary, Prop}

extension [A, B](mapBijection: MapBijection[A, B])
  def checkForward(using Arbitrary[A]): Prop =
    Prop.forAll[A, Boolean] { case a: A =>
      // Self Inverse
      mapBijection(a).flatMap(mapBijection.reverse(_)).contains(a)
    }

  def checkReverse(using Arbitrary[B]): Prop =
    Prop.forAll[B, Boolean] { case b: B =>
      // Self Inverse
      mapBijection.reverse(b).flatMap(mapBijection(_)).contains(b)
    }

  def check(using Arbitrary[A], Arbitrary[B]): Prop =
    checkReverse && checkForward
end extension

extension [A, B](pfnBijection: PFnBijection[A, B])
  def checkForward(using Arbitrary[A]): Prop =
    Prop.forAll[A, Boolean] { case a: A =>
      // Self Inverse
      pfnBijection(a).flatMap(pfnBijection.reverse(_)).contains(a)
    }

  def checkReverse(using Arbitrary[B]): Prop =
    Prop.forAll[B, Boolean] { case b: B =>
      // Self Inverse
      pfnBijection.reverse(b).flatMap(pfnBijection(_)).contains(b)
    }

  def check(using Arbitrary[A], Arbitrary[B]): Prop =
    checkReverse && checkForward
end extension

extension [A, B](fnBijection: FnBijection[A, B])
  def checkForward(using Arbitrary[A]): Prop =
    Prop.forAll[A, Boolean] { case a: A =>
      // Self Inverse
      fnBijection.reverse(fnBijection(a)) == a
    }

  def checkReverse(using Arbitrary[B]): Prop =
    Prop.forAll[B, Boolean] { case b: B =>
      // Self Inverse
      fnBijection(fnBijection.reverse(b)) == b
    }

  def check(using Arbitrary[A], Arbitrary[B]): Prop =
    checkReverse && checkForward
end extension
