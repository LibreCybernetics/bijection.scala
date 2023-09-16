package dev.librecybernetics.data

import scala.annotation.targetName

sealed case class PFnBijection[A, B](
    forwardPFn: PartialFunction[A, B],
    reversePFn: PartialFunction[B, A]
) extends Bijection[A, B] { self =>
  override type Concat = PFnBijection[A, B]
  override type Flip = PFnBijection[B, A]
  override type Result[T] = Option[T]

  // Properties
  override inline def isDefined(inline a: A): Boolean = forwardPFn.isDefinedAt(a)

  // Access
  override inline def apply(inline a: A): Option[B] = forwardPFn.unapply(a)

  override inline def reverse(inline b: B): Option[A] = reversePFn.unapply(b)

  // Transform
  override lazy val flip: PFnBijection[B, A] =
    new PFnBijection(self.reversePFn, self.forwardPFn) {
      override lazy val flip: PFnBijection[A, B] = self
    }

  // Combine
  @targetName("concat")
  override def ++(other: PFnBijection[A, B]): PFnBijection[A, B] =
    PFnBijection[A, B](
      forwardPFn = other.forwardPFn orElse this.forwardPFn,
      reversePFn = other.reversePFn orElse this.reversePFn
    )
}
