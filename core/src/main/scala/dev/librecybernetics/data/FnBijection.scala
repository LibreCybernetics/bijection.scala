package dev.librecybernetics.data

import scala.annotation.targetName
import scala.util.Try

import cats.MonadError

sealed case class FnBijection[A, B](
    forwardFn: A => B,
    reverseFn: B => A
) extends Bijection[A, B] { self =>
  override type Concat = FnBijection[A, B]
  override type Flip = FnBijection[B, A]
  override type Result[T] = T

  // Properties
  override inline def isDefined(inline a: A): Boolean = Try(forwardFn(a)).isSuccess

  // Access
  override inline def apply(inline a: A): B = forwardFn(a)

  override inline def reverse(inline b: B): A = reverseFn(b)

  // Transform
  override lazy val flip: FnBijection[B, A] = new FnBijection(self.reverseFn, self.forwardFn) {
    override lazy val flip: FnBijection[A, B] = self
  }

  // Combine
  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  @targetName("concat")
  override def ++(other: FnBijection[A, B]): FnBijection[A, B] =
    FnBijection(
      forwardFn = a => Try(other(a)).getOrElse(this(a)),
      reverseFn = b => Try(other.reverse(b)).getOrElse(this.reverse(b))
    )
}
