package com.c4wrd.gatekeeper.exception;

import com.c4wrd.gatekeeper.api.Condition;

public class InvalidContextValueException extends IllegalArgumentException {

  public InvalidContextValueException(String message) {
    super(message);
  }

  public static InvalidContextValueException missing(
      String argumentName, Class<? extends Condition> conditionCls) {
    throw new InvalidContextValueException(
        String.format(
            "Missing required context value '%s' for condition '%s'",
            argumentName, conditionCls.getCanonicalName()));
  }

  public static InvalidContextValueException invalidType(
      String contextKeyName,
      Class expectedContextValueCls,
      Class suppliedContextValueCls,
      Class<? extends Condition> conditionCls) {
    throw new InvalidContextValueException(
        String.format(
            "Invalid type '%s' provided for context key '%s' of condition '%s'. Expected type '%s'.",
            suppliedContextValueCls.getCanonicalName(),
            contextKeyName,
            conditionCls.getCanonicalName(),
            expectedContextValueCls.getCanonicalName()));
  }
}
