package dev.librecybernetics.data

import cats.MonadError

case class PFnBijection[A, B](
    forward: PartialFunction[A, B],
    reverse: PartialFunction[B, A]
)

given Bijection[PFnBijection] with
  extension [A, B](pb: PFnBijection[A, B])
    // Properties
    def isDefined(a: A): Boolean = pb.forward.isDefinedAt(a)

    // Access
    def apply(a: A): Option[B]   = pb.forward.unapply(a)
    def reverse(b: B): Option[A] = pb.reverse.unapply(b)

    def unsafeApply(a: A): B   = pb.forward(a)
    def unsafeReverse(b: B): A = pb.reverse(b)

    // Transform
    def flip: PFnBijection[B, A] = PFnBijection(pb.reverse, pb.forward)

    // Combine
    @SuppressWarnings(Array("org.wartremover.warts.TripleQuestionMark"))
    def ++[
        F[_]: [F[_]] =>> MonadError[F, Bijection.Error]
    ](other: PFnBijection[A, B]): F[PFnBijection[A, B]] = ???
  end extension
end given
