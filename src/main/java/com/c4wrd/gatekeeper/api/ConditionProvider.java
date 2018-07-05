package com.c4wrd.gatekeeper.api;

import java.util.Map;

public interface ConditionProvider {
  /**
   * Provides the condition that implements the given condition type.
   *
   * @param type The implementation of the condition identified by type
   * @param arguments The arugments provided by the policy definition, which may or may not be valid
   *     arguments for the condition.
   */
  Condition provideCondition(String type, Map<String, Object> arguments);
}
