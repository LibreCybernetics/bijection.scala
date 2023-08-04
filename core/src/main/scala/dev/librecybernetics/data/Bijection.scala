package dev.librecybernetics.data

import scala.annotation.{nowarn, targetName}
import scala.collection.immutable.{Map, Set}
import scala.util.NotGiven

// NOTE: This mimics a middle point between Scala and Cats

object Bijection:
  def empty[A, B]: Bijection[A, B] =
    empty[A, B](Map.empty, Map.empty)

  def empty[A, B](
      emptyf: Map[A, B],
      emptyr: Map[B, A]
  ): Bijection[A, B] =
    require(emptyf.isEmpty)
    require(emptyr.isEmpty)
    new Bijection[A, B](emptyf, emptyr)

  def apply[A, B](
      kvs: (A, B)*
  ): Bijection[A, B] = empty[A, B] ++ kvs
end Bijection

case class Bijection[A, B] private (
    forwardMap: Map[A, B],
    reverseMap: Map[B, A]
):
  // Consistency check

  require(forwardMap.keys.forall { key => reverseMap(forwardMap(key)) == key })
  require(reverseMap.keys.forall { key => forwardMap(reverseMap(key)) == key })

  // Properties

  def contains(a: A): Boolean = forwardMap.contains(a)

  def isEmpty: Boolean =
    assert(forwardMap.isEmpty == reverseMap.isEmpty)
    forwardMap.isEmpty
  end isEmpty

  def nonEmpty: Boolean =
    assert(forwardMap.nonEmpty == reverseMap.nonEmpty)
    forwardMap.nonEmpty
  end nonEmpty

  // Access

  @throws[NoSuchElementException]
  def unsafeApply(a: A): B = forwardMap(a)

  def apply(a: A): Option[B] = forwardMap.get(a)

  @throws[NoSuchElementException]
  def unsafeReverse(b: B): A   = reverseMap(b)
  def reverse(b: B): Option[A] = reverseMap.get(b)

  def iterator: Iterator[(A, B)] = forwardMap.iterator

  def keySet: Set[A] = forwardMap.keySet

  def size: Int =
    assert(forwardMap.size == reverseMap.size)
    forwardMap.size
  end size

  // Modifiers

  def updated(a: A, b: B): Bijection[A, B] =
    Bijection(forwardMap.updated(a, b), reverseMap.updated(b, a))

  @targetName("append")
  infix def +(kv: (A, B)): Bijection[A, B] = updated(kv._1, kv._2)

  @targetName("appendAll")
  infix def ++(i: IterableOnce[(A, B)]): Bijection[A, B] =
    i.iterator.foldLeft(this)(_ + _)

  infix def ++(o: Bijection[A, B]): Bijection[A, B] =
    this ++ o.iterator

  def remove(a: A): Bijection[A, B] = this(a) match
    case None    => this
    case Some(b) => Bijection(forwardMap - a, reverseMap - b)

  infix def -(a: A): Bijection[A, B] = remove(a)

  @targetName("removeAll")
  infix def --(i: IterableOnce[A]): Bijection[A, B] =
    i.iterator.foldLeft(this)(_ - _)

  def flip: Bijection[B, A] = Bijection(reverseMap, forwardMap)
end Bijection
