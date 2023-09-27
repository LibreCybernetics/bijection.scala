package dev.librecybernetics.data

import scala.util.Try

import org.scalacheck.Arbitrary
import org.scalatest
import org.scalatest.{Assertion, Succeeded}
import org.scalatest.matchers.should.Matchers.*
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

extension [A, B](mapBijection: MapBijection[A, B])
  def checkForward(configs: PropertyCheckConfigParam*)(using Arbitrary[A]): Assertion =
    forAll[A, Assertion](summon[Arbitrary[A]].arbitrary, configs*) { case a: A =>
      whenever(mapBijection.isDefined(a)) {
        // Self Inverse
        mapBijection(a).flatMap(mapBijection.reverse(_)) should contain(a)
      }
    }

  def checkReverse(configs: PropertyCheckConfigParam*)(using Arbitrary[B]): Assertion =
    mapBijection.flip.checkForward(configs*)

  @SuppressWarnings(Array("org.wartremover.warts.TryPartial"))
  def check(configs: PropertyCheckConfigParam*)(using Arbitrary[A], Arbitrary[B]): Assertion =
    (for
      Succeeded <- Try(checkForward(configs*))
      Succeeded <- Try(checkReverse(configs*))
    yield Succeeded).get
end extension

extension [A, B](pfnBijection: PFnBijection[A, B])
  def checkForward(configs: PropertyCheckConfigParam*)(using Arbitrary[A]): Assertion =
    forAll[A, Assertion](summon[Arbitrary[A]].arbitrary, configs*) { case a: A =>
      whenever(pfnBijection.isDefined(a)) {
        // Self Inverse
        pfnBijection(a).flatMap(pfnBijection.reverse(_)) should contain(a)
      }
    }

  def checkReverse(configs: PropertyCheckConfigParam*)(using Arbitrary[B]): Assertion =
    pfnBijection.flip.checkForward(configs*)

  @SuppressWarnings(Array("org.wartremover.warts.TryPartial"))
  def check(configs: PropertyCheckConfigParam*)(using Arbitrary[A], Arbitrary[B]): Assertion =
    (for
      Succeeded <- Try(checkForward(configs*))
      Succeeded <- Try(checkReverse(configs*))
    yield Succeeded).get
end extension

extension [A, B](fnBijection: FnBijection[A, B])
  def checkForward(configs: PropertyCheckConfigParam*)(using Arbitrary[A]): Assertion =
    forAll[A, Assertion](summon[Arbitrary[A]].arbitrary, configs*) { case a: A =>
      whenever(fnBijection.isDefined(a)) {
        // Self Inverse
        fnBijection.reverse(fnBijection(a)) shouldBe a
      }
    }

  def checkReverse(configs: PropertyCheckConfigParam*)(using Arbitrary[B]): Assertion =
    fnBijection.flip.checkForward(configs*)

  @SuppressWarnings(Array("org.wartremover.warts.TryPartial"))
  def check(configs: PropertyCheckConfigParam*)(using Arbitrary[A], Arbitrary[B]): Assertion =
    (for
      Succeeded <- Try(checkForward(configs*))
      Succeeded <- Try(checkReverse(configs*))
    yield Succeeded).get
end extension
