# Motivation

Small utility to organize bijections (group both arrows together in a single object)

A bit inspired by [twitter/bijection](https://github.com/twitter/bijection/) (2013-2023)

# Getting Started

## Adding as a dependency via JitPack

Add jitpack.io as a repository

### SBT

```scala
libraryDependencies += "dev.librecybernetics.bijection~scala" %%% "bijection-core" % bijectionVersion,
```

### Mill

```scala
ivy"dev.librecybernetics.bijection~scala::bijection-core::$bijectionVersion"
```

## Adding as a dependency via GitHub Packages

Add github packages as a repository (needs a personal access token)

### SBT

```scala
libraryDependencies += "dev.librecybernetics" %%% "bijection-core" % bijectionVersion,
```

### Mill

```scala
ivy"dev.librecybernetics::bijection-core::$bijectionVersion"
```

## Adding as a dependency via Maven Central

Not currently available

## Simple Examples

### Map-based Bijections

```scala
import dev.librecybernetics.data.Bijection

val Right(bijection): Either[Bijection.Error, MapBijection[Char, Int]] =
  Bijection(
    'a' -> 1,
    'b' -> 2,
    'c' -> 3
  ): @unchecked

bijection("a") // Some(1)
bijection("d") // None

bijection.reverse(1) // Some("a")
bijection.reverse(4) // None

bijection.flip // MapBijection[Int, Char] 
```

### Function-based Bijections

There are two variants: FnBijection (Function Bijection) and PFnBijection (Partial Function Bijection)

```scala
val bijection: PFnBijection[Char, Int] = Bijection(
  { case c if ('a' to 'c') contains c => (c - 87).toInt },
  { case i if (1 to 3) contains i     => (i + 87).toChar }
)
```

## Versioning

CalVer with Patch: `YYYY.MM.MICRO` (Year, Month, Patch)

## Supported Platforms

- CPU: x86_64 (ARM and RISC-V might work but LibreCybernetics currently has no hardware to support development)
- OS: Linux (Neither Windows nor MacOS are supported)
- JDK: Latest 2 supported LTS releases and latest version. (Currently: 17, 21)
- Scala: 3.y LTS releases and latest version /w latest scala-native and scala.js (Currently: 3.3.1)
