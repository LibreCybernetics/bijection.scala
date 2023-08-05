package dev.librecybernetics.data

import org.scalatest.*
import org.scalatest.wordspec.AnyWordSpec

class MapBijectionSpec extends AnyWordSpec:
  "Bijection" when {
    "Simple Example" in {
      val bijection: MapBijection[Char, Int] =
        MapBijection('a' -> 1, 'b' -> 2, 'c' -> 3)

      assert(bijection.isDefined('a'))
      assert(bijection.isDefined('b'))
      assert(bijection.isDefined('c'))

      assert(bijection('a') contains 1)
      assert(bijection('b') contains 2)
      assert(bijection('c') contains 3)

      assert(bijection.reverse(1) contains 'a')
      assert(bijection.reverse(2) contains 'b')
      assert(bijection.reverse(3) contains 'c')

      assert(!bijection.isEmpty)
      assert(bijection.nonEmpty)
      assert(bijection.size == 3)

      assert(bijection.keySet.forall { char =>
        bijection(char).flatMap(bijection.reverse) contains char
      })

      assert((bijection -- Seq('a', 'b', 'c', 'd')).isEmpty)
      assert((bijection.flip -- Seq(1, 2, 3, 4)).isEmpty)
    }
  }
