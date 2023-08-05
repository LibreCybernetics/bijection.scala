package dev.librecybernetics.data

import cats.MonadError

case class PFnBijection[A, B](
    forwardPFn: PartialFunction[A, B],
    reversePFn: PartialFunction[B, A]
)

object PFnBijection:
  given Bijection[PFnBijection] with
    extension [A, B](pb: PFnBijection[A, B])
      // Properties
      def isDefined(a: A): Boolean = pb.forwardPFn.isDefinedAt(a)

      // Access
      def apply(a: A): Option[B]   = pb.forwardPFn.unapply(a)
      def reverse(b: B): Option[A] = pb.reversePFn.unapply(b)

      // Transform
      def flip: PFnBijection[B, A] = PFnBijection(pb.reversePFn, pb.forwardPFn)

      // Combine
      def ++[F[_]: [F[_]] =>> MonadError[F, Bijection.Error]](
          other: PFnBijection[A, B]
      ): F[PFnBijection[A, B]] = MonadError[F, Bijection.Error].pure {
        PFnBijection[A, B](
          forwardPFn = {
            case a if pb(a).isDefined && other(a).isEmpty =>
              pb.forwardPFn(a)
            case a if pb(a).isEmpty && other(a).isDefined =>
              other.forwardPFn(a)

            case a
                if pb(a).isDefined &&
                  other(a).isDefined &&
                  pb(a) == other(a) =>
              pb.forwardPFn(a)
          }: PartialFunction[A, B],
          reversePFn = {
            case b if pb.reverse(b).isDefined && other.reverse(b).isEmpty =>
              pb.reversePFn(b)
            case b if pb.reverse(b).isEmpty && other.reverse(b).isDefined =>
              other.reversePFn(b)

            case b
                if pb.reverse(b).isDefined &&
                  other.reverse(b).isDefined &&
                  pb.reverse(b) == other.reverse(b) =>
              pb.reversePFn(b)
          }: PartialFunction[B, A]
        )
      }
    end extension
  end given
end PFnBijection
