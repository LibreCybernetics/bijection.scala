package dev.librecybernetics.data

import org.scalacheck.{Arbitrary, Prop}
import org.scalacheck.Prop.{forAll, propBoolean}

extension [A, B](mapBijection: MapBijection[A, B])
  def checkForward(using Arbitrary[A]): Prop =
    forAll[A, Prop] { case a: A =>
      mapBijection.isDefined(a) ==>
        // Self Inverse
        mapBijection(a).flatMap(mapBijection.reverse(_)).contains(a)
    }

  def checkReverse(using Arbitrary[B]): Prop =
    mapBijection.flip.checkForward

  def check(using Arbitrary[A], Arbitrary[B]): Prop =
    checkReverse && checkForward
end extension

extension [A, B](pfnBijection: PFnBijection[A, B])
  def checkForward(using Arbitrary[A]): Prop =
    forAll[A, Prop] { case a: A =>
      pfnBijection.isDefined(a) ==>
        // Self Inverse
        pfnBijection(a).flatMap(pfnBijection.reverse(_)).contains(a)
    }

  def checkReverse(using Arbitrary[B]): Prop =
    pfnBijection.flip.checkForward

  def check(using Arbitrary[A], Arbitrary[B]): Prop =
    checkReverse && checkForward
end extension

extension [A, B](fnBijection: FnBijection[A, B])
  def checkForward(using Arbitrary[A]): Prop =
    forAll[A, Prop] { case a: A =>
      fnBijection.isDefined(a) ==>
        // Self Inverse
        (fnBijection.reverse(fnBijection(a)) == a)
    }

  def checkReverse(using Arbitrary[B]): Prop =
    fnBijection.flip.checkForward

  def check(using Arbitrary[A], Arbitrary[B]): Prop =
    checkReverse && checkForward
end extension
