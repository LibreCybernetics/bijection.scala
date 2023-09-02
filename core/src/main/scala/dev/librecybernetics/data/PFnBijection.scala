package dev.librecybernetics.data

import cats.MonadError

case class PFnBijection[A, B](
    forwardPFn: PartialFunction[A, B],
    reversePFn: PartialFunction[B, A]
) extends Bijection[PFnBijection, A, B] { self =>
  // Properties
  override def isDefined(a: A): Boolean = forwardPFn.isDefinedAt(a)

  // Access
  inline override def apply(a: A): Option[B] = forwardPFn.unapply(a)

  inline override def reverse(b: B): Option[A] = reversePFn.unapply(b)

  // Transform
  override lazy val flip: PFnBijection[B, A] = new PFnBijection(reversePFn, forwardPFn) {
    override lazy val flip: PFnBijection[A, B] = self
  }

  // Combine
  override def ++(other: PFnBijection[A, B]): PFnBijection[A, B] =
    PFnBijection[A, B](
      forwardPFn = other.forwardPFn.orElse(this.forwardPFn),
      reversePFn = other.reversePFn.orElse(this.reversePFn)
    )
}
