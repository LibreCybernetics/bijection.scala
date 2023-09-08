package dev.librecybernetics.data

import scala.annotation.targetName

import cats.{MonadError, Traverse}

object Bijection:
  enum Error:
    case Rebinding[A, B](rebinding: (A, B), current: (A, B)*)
  end Error

  /** Attempts to create a MapBijection from a sequence of correspondences. If any clash then it will return a Rebinding
    * error
    *
    * ```
    * Bijection('a' -> 1, 'b' -> 2, 'c' -> 3)
    * ```
    */
  def apply[A, B, F[_]: [F[_]] =>> MonadError[F, Bijection.Error]](
      correspondences: (A, B)*
  ): F[MapBijection[A, B]] = MapBijection(correspondences*)

  /** Creates a FnBijection from a forward and reverse function
    *
    * ```
    * Bijection[Int, String](_.toString, _.toInt)
    * ```
    */
  def apply[A, B](
      forward: A => B,
      reverse: B => A
  ): FnBijection[A, B] = FnBijection(forward, reverse)

  /** Creates a PFnBijection from a partial forward and reverse function
    */
  def apply[A, B](
      forward: PartialFunction[A, B],
      reverse: PartialFunction[B, A]
  ): PFnBijection[A, B] = PFnBijection(forward, reverse)
end Bijection

trait Bijection[F[_, _], A, B]:
  // Properties
  inline def isDefined(inline a: A): Boolean

  // Access
  inline def apply(inline a: A): Option[B]
  inline def reverse(inline b: B): Option[A]

  // Transform
  lazy val flip: F[B, A]

  // Combine
  @targetName("concat")
  def ++(other: F[A, B]): F[A, B]
end Bijection
