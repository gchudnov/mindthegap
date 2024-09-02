+++
draft = false
insert_anchor_links = "right"
+++

A library of [intervals](/intervals/), [relations](/relations/) and [algorithms](/algorithms/).

Add the following dependency to your `build.sbt`:

```scala
libraryDependencies += "com.github.gchudnov" %% "mtg" % "{{app_version}}"

// optional dependencies to draw diagrams
libraryDependencies += "com.github.gchudnov" %% "mtg-diagram-ascii" % "{{app_version}}"   // ascii
libraryDependencies += "com.github.gchudnov" %% "mtg-diagram-mermaid" % "{{app_version}}" // mermaid
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
  val a = Interval.closed(0, 5) // [0,5]
  val b = Interval.closed(1, 6) // [1,6]

  val c = a.intersection(b) // [1,5]

  println(c)
  // [1,5]
}
```

Check the [examples](https://github.com/gchudnov/mindthegap/tree/main/examples/src/main/scala/com/github/gchudnov/mtg/examples) directory for the list of use-cases.
