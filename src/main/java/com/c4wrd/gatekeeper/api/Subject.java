package com.c4wrd.gatekeeper.api;

import java.util.Collections;
import java.util.List;

/**
 * Intended to be implemented when using this framework. Subject is an abstract concept that will
 * identify some entity, such as a user or service.
 */
public interface Subject {
  /**
   * Returns an identifier to be used by the implementation.
   */
  String getId();

  /**
   * Optionally return a list of tags that can be used in expressions
   * to determine whether or not a user can satisfy some condition.
   */
  default List<String> getTags() {
    return Collections.emptyList();
  }
}
