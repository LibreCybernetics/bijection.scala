package dev.librecybernetics.data

import scala.util.Try

case class FnBijection[A, B](
    forward: A => B,
    reverse: B => A
)

given Bijection[FnBijection] with
  extension [A, B](fb: FnBijection[A, B])

    // Properties
    def isDefined(a: A): Boolean = Try(unsafeApply(a)).isSuccess

    // Access
    def apply(a: A): Option[B] = Try(unsafeApply(a)).toOption
    def reverse(b: B): Option[A] = Try(fb.unsafeReverse(b)).toOption

    inline def unsafeApply(a: A): B = fb.forward(a)
    inline def unsafeReverse(b: B): A = fb.reverse(b)

    // Combine
    def ++(other: FnBijection[A, B]): FnBijection[A, B] = ???
  end extension
end given