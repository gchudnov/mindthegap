package com.github.gchudnov.mtg.internal.domain

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.internal.Value
import com.github.gchudnov.mtg.internal.Endpoint
import com.github.gchudnov.mtg.internal.ordering.ValueOrdering
import com.github.gchudnov.mtg.internal.ordering.EndpointOrdering

/**
  * Any domain. A common functionality for all domains.
  */
abstract class AnyDomain[T] extends Domain[T] {
  self: Ordering[T] =>

  /**
    * Ordering of the value.
    */
  override val ordValue: Ordering[Value[T]] =
    new ValueOrdering[T]()(using self)

  /**
    * Ordering of the endpoint.
    */
  override val ordEndpoint: Ordering[Endpoint[T]] =
    new EndpointOrdering[T]()(using this)

}