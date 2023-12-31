package dev.librecybernetics.data

import scala.annotation.{nowarn, targetName}
import scala.collection.immutable.{Map, Set}

import alleycats.std.iterable.*
import cats.syntax.monadError.*
import cats.{ApplicativeError, MonadError, Traverse}

sealed case class MapBijection[A, B] private[data] (
    forwardMap: Map[A, B],
    reverseMap: Map[B, A]
) extends Bijection[A, B] { self =>
  override type Concat = MapBijection[A, B]
  override type Flip = MapBijection[B, A]
  override type Result[T] = Option[T]

  // Properties
  override inline def isDefined(inline a: A): Boolean = forwardMap.contains(a)

  def isEmpty: Boolean =
    assert(forwardMap.isEmpty == reverseMap.isEmpty)
    forwardMap.isEmpty
  end isEmpty

  def nonEmpty: Boolean =
    assert(forwardMap.nonEmpty == reverseMap.nonEmpty)
    forwardMap.nonEmpty
  end nonEmpty

  def size: Int =
    assert(forwardMap.size == reverseMap.size)
    forwardMap.size
  end size

  // Access
  override inline def apply(inline a: A): Option[B] = forwardMap.get(a)

  override inline def reverse(inline b: B): Option[A] = reverseMap.get(b)

  def iterable: Iterable[(A, B)] = forwardMap.iterator.to(Iterable)

  def keySet: Set[A] = forwardMap.keySet

  // Transform
  override lazy val flip: MapBijection[B, A] =
    new MapBijection[B, A](reverseMap, forwardMap) {
      override lazy val flip: MapBijection[A, B] = self
    }

  // Modifiers

  @targetName("append")
  infix def +[
      F[_]: [F[_]] =>> ApplicativeError[F, Bijection.Error]
  ](
      ab: (A, B)
  ): F[MapBijection[A, B]] =
    val (a, b) = ab
    val aerr     = ApplicativeError[F, Bijection.Error]
    this.apply(a) -> this.reverse(b) match
      case (Some(bi), None) if b != bi                =>
        aerr.raiseError(Bijection.Error.Rebinding[A, B](a -> b, a -> bi))
      case (None, Some(ai)) if a != ai                =>
        aerr.raiseError(Bijection.Error.Rebinding[A, B](a -> b, ai -> b))
      case (Some(bi), Some(ai)) if a != ai && b != bi =>
        aerr.raiseError(Bijection.Error.Rebinding[A, B](a -> b, ai -> b, a -> bi))
      case (_, _)                                     =>
        aerr.pure(new MapBijection[A, B](forwardMap + ab, reverseMap + ab.swap))
    end match

  @targetName("appendAll")
  def ++[
      F[_]: [F[_]] =>> MonadError[F, Bijection.Error],
      T[_]: Traverse
  ](
      iterable: T[(A, B)]
  ): F[MapBijection[A, B]] =
    Traverse[T].foldM(iterable, this)(_ + _)

  @targetName("concat")
  override def ++(other: MapBijection[A, B]): MapBijection[A, B] =
    MapBijection(
      forwardMap ++ other.forwardMap,
      reverseMap ++ other.reverseMap
    )
  end ++

  @targetName("remove")
  def -(a: A): MapBijection[A, B] = this(a) match
    case None    => this
    case Some(b) => MapBijection(forwardMap - a, reverseMap - b)
  end -

  @targetName("removeAll")
  infix def --(i: IterableOnce[A]): MapBijection[A, B] =
    i.iterator.foldLeft(this)(_ - _)
}

object MapBijection:
  def empty[A, B]: MapBijection[A, B] = MapBijection[A, B](Map.empty, Map.empty)

  def apply[A, B, F[_]: [F[_]] =>> MonadError[F, Bijection.Error]](
      kvs: (A, B)*
  ): F[MapBijection[A, B]] = empty[A, B] ++ kvs
end MapBijection
