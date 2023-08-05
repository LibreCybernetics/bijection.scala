package dev.librecybernetics.data

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

trait Bijection[F[_, _]]:
  extension [A, B](c: F[A, B])
    // Properties
    def isDefined(a: A): Boolean

    // Access
    def apply(a: A): Option[B]
    def reverse(b: B): Option[A]

    // Transform
    def flip: F[B, A]

    // Combine
    def ++[
        M[_]: [M[_]] =>> MonadError[M, Bijection.Error]
    ](other: F[A, B]): M[F[A, B]]
  end extension
end Bijection
