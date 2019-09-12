/* Annot8 (annot8.io) - Licensed under Apache-2.0. */
package io.annot8.core.filters;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Logical OR of filters
 *
 * @param <T> element type
 */
public class OrFilter<T> implements Filter<T> {

  private final Filter<T>[] filters;

  public OrFilter(Filter<T>... filters) {
    this.filters = filters;
  }

  @Override
  public boolean test(T t) {
    return Arrays.stream(filters).anyMatch(f -> f.test(t));
  }

  /**
   * Get all the sub filters of this operation.
   *
   * @return filters
   */
  public Stream<Filter> getFilters() {
    return Arrays.stream(filters);
  }
}
