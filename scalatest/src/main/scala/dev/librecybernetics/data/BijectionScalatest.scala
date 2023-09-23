package dev.librecybernetics.data

import scala.util.Try

import org.scalacheck.Arbitrary
import org.scalatest
import org.scalatest.{Assertion, Succeeded}
import org.scalatest.matchers.should.Matchers.*
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

extension [A, B](mapBijection: MapBijection[A, B])
  def checkForward(using Arbitrary[A]): Assertion =
    forAll[A, Assertion] { case a: A =>
      whenever(mapBijection.isDefined(a)) {
        // Self Inverse
        mapBijection(a).flatMap(mapBijection.reverse(_)) should contain(a)
      }
    }

  def checkReverse(using Arbitrary[B]): Assertion =
    mapBijection.flip.checkForward

  @SuppressWarnings(Array("org.wartremover.warts.TryPartial"))
  def check(using Arbitrary[A], Arbitrary[B]): Assertion =
    (for
      Succeeded <- Try(checkForward)
      Succeeded <- Try(checkReverse)
    yield Succeeded).get
end extension

extension [A, B](pfnBijection: PFnBijection[A, B])
  def checkForward(using Arbitrary[A]): Assertion =
    forAll[A, Assertion] { case a: A =>
      whenever(pfnBijection.isDefined(a)) {
        // Self Inverse
        pfnBijection(a).flatMap(pfnBijection.reverse(_)) should contain(a)
      }
    }

  def checkReverse(using Arbitrary[B]): Assertion =
    pfnBijection.flip.checkForward

  @SuppressWarnings(Array("org.wartremover.warts.TryPartial"))
  def check(using Arbitrary[A], Arbitrary[B]): Assertion =
    (for
      Succeeded <- Try(checkForward)
      Succeeded <- Try(checkReverse)
    yield Succeeded).get
end extension

extension [A, B](fnBijection: FnBijection[A, B])
  def checkForward(using Arbitrary[A]): Assertion =
    forAll[A, Assertion] { case a: A =>
      whenever(fnBijection.isDefined(a)) {
        // Self Inverse
        fnBijection.reverse(fnBijection(a)) shouldBe a
      }
    }

  def checkReverse(using Arbitrary[B]): Assertion =
    fnBijection.flip.checkForward

  @SuppressWarnings(Array("org.wartremover.warts.TryPartial"))
  def check(using Arbitrary[A], Arbitrary[B]): Assertion =
    (for
      Succeeded <- Try(checkForward)
      Succeeded <- Try(checkReverse)
    yield Succeeded).get
end extension
