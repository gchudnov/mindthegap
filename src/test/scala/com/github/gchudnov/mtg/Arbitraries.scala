package com.github.gchudnov.mtg

import org.scalacheck.Gen

object Arbitraries:

  val numPair = 
    for {
      x <- Gen.choose(-100, 100)
      y <- Gen.choose(x, 100)
    } yield (x, y)
