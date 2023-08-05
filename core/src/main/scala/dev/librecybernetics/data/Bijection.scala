package dev.librecybernetics.data

trait Bijection[F[_, _]]:
  extension [A, B](c: F[A, B])
    // Properties
    def isDefined(a: A): Boolean

    // Access
    def apply(a: A): Option[B]
    def reverse(b: B): Option[A]

    def unsafeApply(a: A): B
    def unsafeReverse(b: B): A

    // Combine
    def ++(other: F[A, B]): F[A, B]
  end extension
end Bijection
