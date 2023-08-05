package dev.librecybernetics.data

import scala.annotation.{nowarn, targetName}
import scala.collection.immutable.{Map, Set}

import alleycats.std.iterable.*
import cats.syntax.monadError.*
import cats.{MonadError, Traverse}

// NOTE: This mimics a middle point between Scala and Cats

object MapBijection:
  def empty[A, B]: MapBijection[A, B] =
    new MapBijection[A, B](Map.empty, Map.empty)

  def apply[
      A,
      B,
      F[_]: [F[_]] =>> MonadError[F, Bijection.Error]
  ](
      kvs: (A, B)*
  ): F[MapBijection[A, B]] = empty[A, B] ++ kvs

  given Bijection[MapBijection] with
    extension [A, B](mb: MapBijection[A, B])

      // Properties
      override def isDefined(a: A): Boolean = mb.forwardMap.contains(a)

      // Access
      def apply(a: A): Option[B]   = mb.forwardMap.get(a)
      def reverse(b: B): Option[A] = mb.reverseMap.get(b)

      @throws[NoSuchElementException]
      def unsafeApply(a: A): B = mb.forwardMap(a)

      @throws[NoSuchElementException]
      def unsafeReverse(b: B): A = mb.reverseMap(b)

      // Transform
      def flip: MapBijection[B, A] = new MapBijection[B, A](mb.reverseMap, mb.forwardMap)

      // Combine
      def ++[
          F[_]: [F[_]] =>> MonadError[F, Bijection.Error]
      ](
          other: MapBijection[A, B]
      ): F[MapBijection[A, B]] =
        mb ++ other.iterable
      end ++
    end extension
  end given
end MapBijection

case class MapBijection[A, B] private[data] (
    forwardMap: Map[A, B],
    reverseMap: Map[B, A]
):
  // Properties
  def isEmpty: Boolean =
    assert(forwardMap.isEmpty == reverseMap.isEmpty)
    forwardMap.isEmpty
  end isEmpty

  def nonEmpty: Boolean =
    assert(forwardMap.nonEmpty == reverseMap.nonEmpty)
    forwardMap.nonEmpty
  end nonEmpty

  // Access

  def iterable: Iterable[(A, B)] = forwardMap.iterator.to(Iterable)

  def keySet: Set[A] = forwardMap.keySet

  def size: Int =
    assert(forwardMap.size == reverseMap.size)
    forwardMap.size
  end size

  // Modifiers

  @targetName("append")
  infix def +[
      F[_]: [F[_]] =>> MonadError[F, Bijection.Error]
  ](
      ab: (A, B)
  ): F[MapBijection[A, B]] =
    val (a, b) = ab
    val me     = MonadError[F, Bijection.Error]
    this.apply(a) -> this.reverse(b) match
      case (Some(bi), None) if b != bi                =>
        me.raiseError(Bijection.Error.Rebinding(a -> bi, a -> b))
      case (None, Some(ai)) if a != ai                =>
        me.raiseError(Bijection.Error.Rebinding(b -> ai, b -> a))
      case (Some(bi), Some(ai)) if a != ai && b != bi =>
        me.raiseError(Bijection.Error.Rebinding(a -> bi, a -> b, b -> ai, b -> a))
      case (_, _)                                     =>
        me.pure(new MapBijection[A, B](forwardMap + ab, reverseMap + ab.swap))
    end match

  @targetName("appendAll")
  infix def ++[
      F[_]: [F[_]] =>> MonadError[F, Bijection.Error],
      T[_]: Traverse
  ](
      iterable: T[(A, B)]
  ): F[MapBijection[A, B]] =
    Traverse[T].foldM(iterable, this)(_ + _)

  def remove(a: A): MapBijection[A, B] = this(a) match
    case None    => this
    case Some(b) => MapBijection(forwardMap - a, reverseMap - b)

  infix def -(a: A): MapBijection[A, B] = remove(a)

  @targetName("removeAll")
  infix def --(i: IterableOnce[A]): MapBijection[A, B] =
    i.iterator.foldLeft(this)(_ - _)
end MapBijection
