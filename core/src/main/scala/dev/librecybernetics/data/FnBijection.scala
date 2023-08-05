package dev.librecybernetics.data

import scala.util.Try

import cats.MonadError

case class FnBijection[A, B](
    forwardFn: A => B,
    reverseFn: B => A
)

object FnBijection:
  given Bijection[FnBijection] with
    extension [A, B](fb: FnBijection[A, B])
      // Properties
      def isDefined(a: A): Boolean = Try(fb.forwardFn(a)).isSuccess

      // Access
      def apply(a: A): Option[B]   = Try(fb.forwardFn(a)).toOption
      def reverse(b: B): Option[A] = Try(fb.reverseFn(b)).toOption

      // Transform
      def flip: FnBijection[B, A] = FnBijection(fb.reverseFn, fb.forwardFn)

      // Combine
      @SuppressWarnings(Array("org.wartremover.warts.Throw"))
      def ++[F[_]: [F[_]] =>> MonadError[F, Bijection.Error]](
          other: FnBijection[A, B]
      ): F[FnBijection[A, B]] =
        MonadError[F, Bijection.Error].pure {
          FnBijection(
            forwardFn = a =>
              (apply(a), other(a)) match
                case (Some(b), None) => b
                case (None, Some(b)) => b

                case (Some(b1), Some(b2)) if b1 == b2 => b1

                case (_, _) =>
                  throw new NoSuchElementException()
              end match
            ,
            reverseFn = b =>
              (reverse(b), other.reverse(b)) match
                case (Some(a), None) => a
                case (None, Some(a)) => a

                case (Some(a1), Some(a2)) if a1 == a2 => a1

                case (_, _) =>
                  throw new NoSuchElementException()
              end match
          )
        }
    end extension
  end given
end FnBijection
