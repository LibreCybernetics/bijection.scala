package dev.librecybernetics.data

import scala.util.Try

import cats.MonadError

case class FnBijection[A, B](
    forward: A => B,
    reverse: B => A
)

given Bijection[FnBijection] with
  extension [A, B](fb: FnBijection[A, B])

    // Properties
    def isDefined(a: A): Boolean = Try(unsafeApply(a)).isSuccess

    // Access
    def apply(a: A): Option[B]   = Try(unsafeApply(a)).toOption
    def reverse(b: B): Option[A] = Try(fb.unsafeReverse(b)).toOption

    inline def unsafeApply(a: A): B   = fb.forward(a)
    inline def unsafeReverse(b: B): A = fb.reverse(b)

    // Transform
    def flip: FnBijection[B, A] = FnBijection(fb.reverse, fb.forward)

    // Combin
    @SuppressWarnings(Array("org.wartremover.warts.TripleQuestionMark"))
    def ++[
        F[_]: [F[_]] =>> MonadError[F, Bijection.Error]
    ](other: FnBijection[A, B]): F[FnBijection[A, B]] = ???
  end extension
end given
