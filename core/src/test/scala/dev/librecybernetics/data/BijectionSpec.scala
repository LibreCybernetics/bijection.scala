package dev.librecybernetics.data

import org.scalatest.*
import org.scalatest.wordspec.AnyWordSpec

class BijectionSpec extends AnyWordSpec:
  "Bijection" when {
    "Growable" in {
      val bijection: Bijection[Char, Int] =
        Bijection.empty[Char, Int](Map.empty, Map.empty) ++
          (Bijection.empty[Char, Int] ++ Set('a' -> 1, 'b' -> 2, 'c' -> 3))

      assert(bijection.contains('a'))
      assert(bijection.contains('b'))
      assert(bijection.contains('c'))

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
