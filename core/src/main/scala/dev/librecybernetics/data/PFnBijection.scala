package dev.librecybernetics.data

import cats.MonadError

case class PFnBijection[A, B](
    forwardPFn: PartialFunction[A, B],
    reversePFn: PartialFunction[B, A]
) extends Bijection[PFnBijection, A, B]:
  // Properties
  override def isDefined(a: A): Boolean = forwardPFn.isDefinedAt(a)

  // Access
  override def apply(a: A): Option[B]   = forwardPFn.unapply(a)
  override def reverse(b: B): Option[A] = reversePFn.unapply(b)

  // Transform
  override def flip: PFnBijection[B, A] = PFnBijection(reversePFn, forwardPFn)

  // Combine
  override def ++[F[_]: [F[_]] =>> MonadError[F, Bijection.Error]](
      other: PFnBijection[A, B]
  ): F[PFnBijection[A, B]] = MonadError[F, Bijection.Error].pure {
    PFnBijection[A, B](
      forwardPFn = {
        case a if apply(a).isDefined && other(a).isEmpty =>
          forwardPFn(a)
        case a if apply(a).isEmpty && other(a).isDefined =>
          other.forwardPFn(a)

        case a
            if apply(a).isDefined &&
              other(a).isDefined &&
              apply(a) == other(a) =>
          forwardPFn(a)
      }: PartialFunction[A, B],
      reversePFn = {
        case b if reverse(b).isDefined && other.reverse(b).isEmpty =>
          reversePFn(b)
        case b if reverse(b).isEmpty && other.reverse(b).isDefined =>
          other.reversePFn(b)

        case b
            if reverse(b).isDefined &&
              other.reverse(b).isDefined &&
              reverse(b) == other.reverse(b) =>
          reversePFn(b)
      }: PartialFunction[B, A]
    )
  }
end PFnBijection
