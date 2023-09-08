package dev.librecybernetics.data

import scala.annotation.targetName
import scala.util.Try

import cats.MonadError

sealed case class FnBijection[A, B](
    forwardFn: A => B,
    reverseFn: B => A
) extends Bijection[FnBijection, A, B] { self =>
  // Properties
  override inline def isDefined(inline a: A): Boolean = Try(forwardFn(a)).isSuccess

  // Access
  override inline def apply(inline a: A): Option[B] = Try(forwardFn(a)).toOption

  override inline def reverse(inline b: B): Option[A] = Try(reverseFn(b)).toOption

  // Transform
  override lazy val flip: FnBijection[B, A] = new FnBijection(self.reverseFn, self.forwardFn) {
    override lazy val flip: FnBijection[A, B] = self
  }

  // Combine
  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  @targetName("concat")
  override def ++(other: FnBijection[A, B]): FnBijection[A, B] =
    FnBijection(
      forwardFn = a => other.apply(a).orElse(this.apply(a)).get,
      reverseFn = b => other.reverse(b).orElse(this.reverse(b)).get
    )
}
