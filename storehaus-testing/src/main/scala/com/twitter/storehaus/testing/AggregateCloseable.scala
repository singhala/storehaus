/*
 * Copyright 2013 Twitter Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.twitter.storehaus.testing

import scala.collection.mutable.{ HashSet, SynchronizedSet }
import com.twitter.util.{Closable, Future, Time}

/** Represents an Closeable `view` of an aggregation of Closeables */
trait AggregateCloseable[C <: Closable] extends Closable {
  private val closables =
    new HashSet[C] with SynchronizedSet[C]

  def aggregateCloseable(c: C) = {
    closables += c
    c
  }

  final def close(t: Time) = Future.collect(closables.map(_.close).toSeq).unit
}

/** An AggregatedCloseable that cleans up after itself */
trait SelfAggregatingCloseableCleanup[C <: Closable]
  extends AggregateCloseable[C]
  with CloseableCleanup[AggregateCloseable[C]] {
  final def closeable = this
}
