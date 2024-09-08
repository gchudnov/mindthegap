+++
draft = false
insert_anchor_links = "right"
+++

A library of [intervals](intervals/), [relations](relations/) and [algorithms](algorithms/).

To include this library in your project, add the following dependency to your `build.sbt`:

```scala
libraryDependencies += "com.github.gchudnov" %% "mtg" % "{{APP_VERSION}}"

// optional dependencies to draw diagrams
libraryDependencies += "com.github.gchudnov" %% "mtg-diagram-ascii" % "{{APP_VERSION}}"   // ascii
libraryDependencies += "com.github.gchudnov" %% "mtg-diagram-mermaid" % "{{APP_VERSION}}" // mermaid
```

Import the package:

```scala
import com.github.gchudnov.mtg.*
import com.github.gchudnov.mtg.diagram.*
```

Explore various algorithms for interval operations, including intersection:

```scala
val a = Interval.closed(0, 5) // [0,5]
val b = Interval.closed(1, 6) // [1,6]

val c = a.intersection(b) // [1,5]

println(c)
// [1,5]
```

For a list of use-cases, check out the [examples directory](https://github.com/gchudnov/mindthegap/tree/main/examples/src/main/scala/com/github/gchudnov/examples).
