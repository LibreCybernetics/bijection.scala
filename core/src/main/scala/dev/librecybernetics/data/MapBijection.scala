package dev.librecybernetics.data

import scala.annotation.{nowarn, targetName}
import scala.collection.immutable.{Map, Set}
import scala.util.NotGiven

// NOTE: This mimics a middle point between Scala and Cats

object MapBijection:
  def empty[A, B]: MapBijection[A, B] =
    empty[A, B](Map.empty, Map.empty)

  def empty[A, B](
      emptyf: Map[A, B],
      emptyr: Map[B, A]
  ): MapBijection[A, B] =
    require(emptyf.isEmpty)
    require(emptyr.isEmpty)
    new MapBijection[A, B](emptyf, emptyr)

  def apply[A, B](
      kvs: (A, B)*
  ): MapBijection[A, B] = empty[A, B] ++ kvs
end MapBijection

case class MapBijection[A, B] private(
    forwardMap: Map[A, B],
    reverseMap: Map[B, A]
):
  // Consistency check

  require(forwardMap.keys.forall { key => reverseMap(forwardMap(key)) == key })
  require(reverseMap.keys.forall { key => forwardMap(reverseMap(key)) == key })

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

  def iterator: Iterator[(A, B)] = forwardMap.iterator

  def keySet: Set[A] = forwardMap.keySet

  def size: Int =
    assert(forwardMap.size == reverseMap.size)
    forwardMap.size
  end size

  // Modifiers

  def updated(a: A, b: B): MapBijection[A, B] =
    MapBijection(forwardMap.updated(a, b), reverseMap.updated(b, a))

  @targetName("append")
  infix def +(kv: (A, B)): MapBijection[A, B] = updated(kv._1, kv._2)

  @targetName("appendAll")
  infix def ++(i: IterableOnce[(A, B)]): MapBijection[A, B] =
    i.iterator.foldLeft(this)(_ + _)

  infix def ++(o: MapBijection[A, B]): MapBijection[A, B] =
    this ++ o.iterator

  def remove(a: A): MapBijection[A, B] = this(a) match
    case None    => this
    case Some(b) => MapBijection(forwardMap - a, reverseMap - b)

  infix def -(a: A): MapBijection[A, B] = remove(a)

  @targetName("removeAll")
  infix def --(i: IterableOnce[A]): MapBijection[A, B] =
    i.iterator.foldLeft(this)(_ - _)

  def flip: MapBijection[B, A] = MapBijection(reverseMap, forwardMap)
end MapBijection

given Bijection[MapBijection] with
  extension [A, B](mb: MapBijection[A, B])

    // Properties
    override def isDefined(a: A): Boolean = mb.forwardMap.contains(a)

    // Access
    def apply(a: A): Option[B] = mb.forwardMap.get(a)
    def reverse(b: B): Option[A] = mb.reverseMap.get(b)

    @throws[NoSuchElementException]
    def unsafeApply(a: A): B = mb.forwardMap(a)

    @throws[NoSuchElementException]
    def unsafeReverse(b: B): A = mb.reverseMap(b)

    // Combine
    def ++(other: MapBijection[A, B]): MapBijection[A, B] = mb ++ other.iterator
  end extension
end given
