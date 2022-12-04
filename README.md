# mindthegap

> Intervals, Relations and Algorithms

[![Build](https://github.com/gchudnov/mindthegap/actions/workflows/scala.yml/badge.svg)](https://github.com/gchudnov/mindthegap/actions/workflows/scala.yml)

<br clear="right" /><!-- Turn off the wrapping for the logo image. -->

## Documentation

You can find the documentation [on the website](https://gchudnov.github.io/mindthegap/).  

## Usage

Add the following dependency to your `build.sbt`:

```scala
libraryDependencies += "com.github.gchudnov" %% "mindthegap" % "1.0.0"
```

Import the package:

```scala
import com.github.gchudnov.mtg.*
```

An example application:

```scala
package com.example

import com.github.gchudnov.mtg.*

object Hello extends App {
  val a = Interval.closed(0, 5)
  val b = Interval.closed(1, 6)

  val c = a.intersection(b)

  println(c)
  // Interval(At(Finite(1)),At(Finite(5)))

  println(Show.asString(c))
  // [1,5]
}
```

## Links

- [Allen's Interval Algebra](https://www.ics.uci.edu/~alspaugh/cls/shr/allen.html)

## Keywords

Allen's Interval Algebra, Interval Arithmetic, Interval Relations, Infinite Temporal Intervals, Temporal Algorithms

## Contact

[Grigorii Chudnov](mailto:g.chudnov@gmail.com)

## License

Distributed under the [The MIT License (MIT)](LICENSE).
