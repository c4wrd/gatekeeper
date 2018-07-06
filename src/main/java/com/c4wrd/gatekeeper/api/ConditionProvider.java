package com.c4wrd.gatekeeper.api;

import java.util.Map;

public interface ConditionProvider {

  /**
   * Whether or not this condition provider can provide
   * a specified condition identified by type
   */
  boolean canProvideCondition(String type);
  /**
   * Provides the condition that implements the given condition type.
   *
   * @param type The implementation of the condition identified by type
   */
  Condition provideCondition(String type);
}
