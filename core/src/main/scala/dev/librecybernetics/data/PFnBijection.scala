package dev.librecybernetics.data

import cats.MonadError

case class PFnBijection[A, B](
    forwardPFn: PartialFunction[A, B],
    reversePFn: PartialFunction[B, A]
) extends Bijection[PFnBijection, A, B] { self =>
  // Properties
  override inline def isDefined(a: A): Boolean = forwardPFn.isDefinedAt(a)

  // Access
  override inline def apply(a: A): Option[B] = forwardPFn.unapply(a)

  override inline def reverse(b: B): Option[A] = reversePFn.unapply(b)

  // Transform
  override lazy val flip: PFnBijection[B, A] =
    new PFnBijection(self.reversePFn, self.forwardPFn) {
      override lazy val flip: PFnBijection[A, B] = self
    }

  // Combine
  override inline def ++(other: PFnBijection[A, B]): PFnBijection[A, B] =
    PFnBijection[A, B](
      forwardPFn = other.forwardPFn orElse this.forwardPFn,
      reversePFn = other.reversePFn orElse this.reversePFn
    )
}
