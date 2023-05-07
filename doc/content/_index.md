+++
date = 2022-11-19
draft = false
insert_anchor_links = "right"
+++

A library of [intervals](/intervals/), [relations](/relations/) and [algorithms](/algorithms/).

### Usage

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

  println(c.asString)
  // [1,5]
}
```
