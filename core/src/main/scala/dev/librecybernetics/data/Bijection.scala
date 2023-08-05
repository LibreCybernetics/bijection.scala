package dev.librecybernetics.data

import cats.{MonadError, Traverse}

object Bijection:
  enum Error:
    case Rebinding[A, B](rebinding: (A, B), current: (A, B)*)
  end Error

  def apply[
      A,
      B,
      F[_]: [F[_]] =>> MonadError[F, Bijection.Error],
      T[_]: Traverse
  ](
      kvs: (A, B)*
  ): F[MapBijection[A, B]] = MapBijection(kvs*)
end Bijection

trait Bijection[F[_, _]]:
  extension [A, B](c: F[A, B])
    // Properties
    def isDefined(a: A): Boolean

    // Access
    def apply(a: A): Option[B]
    def reverse(b: B): Option[A]

    // Transform
    def flip: F[B, A]

    // Combine
    def ++[
        M[_]: [M[_]] =>> MonadError[M, Bijection.Error]
    ](other: F[A, B]): M[F[A, B]]
  end extension
end Bijection
