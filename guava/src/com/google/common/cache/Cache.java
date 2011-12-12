/*
 * Copyright (C) 2011 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.common.cache;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.UncheckedExecutionException;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nullable;

/**
 * A semi-persistent mapping from keys to values. Cache entries are manually added using
 * {@link #get(K, Callable)} or {@link #put(K, V)}, and are stored in the cache until either
 * evicted or manually invalidated.
 *
 * <p><b>Note:</b> in release 12.0, all methods moved from {@code Cache} to {@link LoadingCache}
 * will be deleted from {@code Cache}. As part of this transition {@code Cache} will no longer
 * extend {@link Function}.
 *
 * <p>Implementations of this interface are expected to be thread-safe, and can be safely accessed
 * by multiple concurrent threads.
 *
 * <p>All methods other than {@link #getIfPresent} are optional.
 *
 * @author Charles Fry
 * @since 10.0
 */
@Beta
@GwtCompatible
public interface Cache<K, V> extends Function<K, V> {

  /**
   * Returns the value associated with {@code key} in this cache, or {@code null} if there is no
   * cached value for {@code key}.
   *
   * @since 11.0
   */
  @Nullable
  V getIfPresent(K key);

  /**
   * Returns the value associated with {@code key} in this cache, obtaining that value from
   * {@code valueLoader} if necessary. No observable state associated with this cache is modified
   * until loading completes. This method provides a simple substitute for the conventional
   * "if cached, return; otherwise create, cache and return" pattern.
   *
   * <p><b>Warning:</b> as with {@link CacheLoader#load}, {@code valueLoader} <b>must not</b> return
   * {@code null}; it may either return a non-null value or throw an exception.
   *
   * @throws ExecutionException if a checked exception was thrown while loading the value
   * @throws UncheckedExecutionException if an unchecked exception was thrown while loading the
   *     value
   * @throws ExecutionError if an error was thrown while loading the value
   *
   * @since 11.0
   */
  V get(K key, Callable<? extends V> valueLoader) throws ExecutionException;

  /**
   * Returns a map of the values associated with {@code keys} in this cache. The returned map will
   * only contain entries which are already present in the cache.
   *
   * @since 11.0
   */
  ImmutableMap<K, V> getAllPresent(Iterable<? extends K> keys);

  /**
   * Associates {@code value} with {@code key} in this cache. If the cache previously contained a
   * value associated with {@code key}, the old value is replaced by {@code value}.
   *
   * <p>Prefer {@link #get(K, Callable)} when using the conventional "if cached, return; otherwise
   * create, cache and return" pattern.
   *
   * @since 11.0
   */
  void put(K key, V value);

  /**
   * Discards any cached value for key {@code key}.
   */
  void invalidate(Object key);

  /**
   * Discards any cached values for keys {@code keys}.
   *
   * @since 11.0
   */
  void invalidateAll(Iterable<?> keys);

  /**
   * Discards all entries in the cache.
   */
  void invalidateAll();

  /**
   * Returns the approximate number of entries in this cache.
   */
  long size();

  /**
   * Returns a current snapshot of this cache's cumulative statistics. All stats are initialized
   * to zero, and are monotonically increasing over the lifetime of the cache.
   */
  CacheStats stats();

  /**
   * Returns a view of the entries stored in this cache as a thread-safe map. Modifications made to
   * the map directly affect the cache.
   */
  ConcurrentMap<K, V> asMap();

  /**
   * Performs any pending maintenance operations needed by the cache. Exactly which activities are
   * performed -- if any -- is implementation-dependent.
   */
  void cleanUp();

  /**
   * Returns the value associated with {@code key} in this cache, first loading that value if
   * necessary. No observable state associated with this cache is modified until loading completes.
   *
   * @throws ExecutionException if a checked exception was thrown while loading the value
   * @throws UncheckedExecutionException if an unchecked exception was thrown while loading the
   *     value
   * @throws ExecutionError if an error was thrown while loading the value
   * @deprecated This method has been split out into the {@link LoadingCache} interface, and will be
   * removed from {@code Cache} in Guava release 12.0. Note that
   * {@link CacheBuilder#build(CacheLoader)} now returns a {@code LoadingCache}, so this deprecation
   * (migration) can be dealt with by simply changing the type of references to the results of
   * {@link CacheBuilder#build(CacheLoader)}.
   */
  @Deprecated V get(K key) throws ExecutionException;

  /**
   * Returns the value associated with {@code key} in this cache, first loading that value if
   * necessary. No observable state associated with this cache is modified until computation
   * completes. Unlike {@link #get}, this method does not throw a checked exception, and thus should
   * only be used in situations where checked exceptions are not thrown by the cache loader.
   *
   * <p><b>Warning:</b> this method silently converts checked exceptions to unchecked exceptions,
   * and should not be used with cache loaders which throw checked exceptions.
   *
   * @throws UncheckedExecutionException if an exception was thrown while loading the value,
   *     regardless of whether the exception was checked or unchecked
   * @throws ExecutionError if an error was thrown while loading the value
   * @deprecated This method has been split out into the {@link LoadingCache} interface, and will be
   * removed from {@code Cache} in Guava release 12.0. Note that
   * {@link CacheBuilder#build(CacheLoader)} now returns a {@code LoadingCache}, so this deprecation
   * (migration) can be dealt with by simply changing the type of references to the results of
   * {@link CacheBuilder#build(CacheLoader)}.
   */
  @Deprecated V getUnchecked(K key);

  /**
   * Discouraged. Provided to satisfy the {@code Function} interface; use {@link #get} or
   * {@link #getUnchecked} instead.
   *
   * @throws UncheckedExecutionException if an exception was thrown while loading the value,
   *     regardless of whether the exception was checked or unchecked
   * @throws ExecutionError if an error was thrown while loading the value
   * @deprecated This method has been split out into the {@link LoadingCache} interface, and will be
   * removed from {@code Cache} in Guava release 12.0. Note that
   * {@link CacheBuilder#build(CacheLoader)} now returns a {@code LoadingCache}, so this deprecation
   * (migration) can be dealt with by simply changing the type of references to the results of
   * {@link CacheBuilder#build(CacheLoader)}.
   */
  @Deprecated V apply(K key);
}
