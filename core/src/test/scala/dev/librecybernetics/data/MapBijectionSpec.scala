package dev.librecybernetics.data

import org.scalatest.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

class MapBijectionSpec extends AnyWordSpec:
  "Bijection" when {
    val Right(bijection): Either[Bijection.Error, MapBijection[Char, Int]] =
      MapBijection('a' -> 1, 'b' -> 2, 'c' -> 3): @unchecked

    "Simple Example" in {
      bijection.isDefined('a') shouldBe true
      bijection.isDefined('b') shouldBe true
      bijection.isDefined('c') shouldBe true

      bijection('a') should contain(1)
      bijection('b') should contain(2)
      bijection('c') should contain(3)

      bijection.reverse(1) should contain('a')
      bijection.reverse(2) should contain('b')
      bijection.reverse(3) should contain('c')

      bijection.isEmpty shouldBe false
      bijection.nonEmpty shouldBe true
      bijection.size shouldBe 3

      bijection.keySet.foreach { char =>
        bijection(char).flatMap(bijection.reverse) should contain(char)
      }

      (bijection -- Seq('a', 'b', 'c', 'd')).isEmpty shouldBe true
      (bijection.flip -- Seq(1, 2, 3, 4)).isEmpty shouldBe true

      val Right(secondBijection): Either[Bijection.Error, MapBijection[Char, Int]] =
        MapBijection('d' -> 4, 'e' -> 5, 'f' -> 6): @unchecked
      val Right(concat): Either[Bijection.Error, MapBijection[Char, Int]]          =
        bijection ++ secondBijection: @unchecked
      concat.size shouldBe 6
    }

    "Rebinding should error" in {
      val Left(bijectionError1) = bijection + ('a' -> 2): @unchecked

      bijectionError1 match
        case Bijection.Error.Rebinding(rebinding, current*) =>
          rebinding shouldBe 'a' -> 2
          current should contain theSameElementsAs
            Seq('a' -> 1, 'b' -> 2)

        case _ =>
          fail("Expected Rebinding Error")
      end match

      val Left(bijectionError2) = bijection + ('a' -> 4): @unchecked

      bijectionError2 match
        case Bijection.Error.Rebinding(rebinding, current*) =>
          rebinding shouldBe 'a' -> 4
          current should contain theSameElementsAs
            Seq('a' -> 1)
        case _                                              => fail("Expected Rebinding Error")
      end match

      val Left(bijectionError3) = bijection + ('d' -> 2): @unchecked

      bijectionError3 match
        case Bijection.Error.Rebinding(rebinding, current*) =>
          rebinding shouldBe 'd' -> 2
          current should contain theSameElementsAs
            Seq('b' -> 2)
        case _                                              => fail("Expected Rebinding Error")
      end match
    }
  }
