package dev.librecybernetics.data

import scala.util.Try

import cats.MonadError

case class FnBijection[A, B](
    forwardFn: A => B,
    reverseFn: B => A
) extends Bijection[FnBijection, A, B] { self =>
  // Properties
  override inline def isDefined(a: A): Boolean = Try(forwardFn(a)).isSuccess

  // Access
  override inline def apply(a: A): Option[B] = Try(forwardFn(a)).toOption

  override inline def reverse(b: B): Option[A] = Try(reverseFn(b)).toOption

  // Transform
  override lazy val flip: FnBijection[B, A] = new FnBijection(self.reverseFn, self.forwardFn) {
    override lazy val flip: FnBijection[A, B] = self
  }

  // Combine
  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  override inline def ++(other: FnBijection[A, B]): FnBijection[A, B] =
    FnBijection(
      forwardFn = a => other.apply(a).orElse(this.apply(a)).get,
      reverseFn = b => other.reverse(b).orElse(this.reverse(b)).get
    )
}
