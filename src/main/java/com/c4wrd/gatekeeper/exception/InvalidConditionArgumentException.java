package com.c4wrd.gatekeeper.exception;

import com.c4wrd.gatekeeper.api.Condition;

public class InvalidConditionArgumentException extends IllegalArgumentException {

  public InvalidConditionArgumentException(String message) {
    super(message);
  }

  public static InvalidConditionArgumentException missing(
      String argumentName, Class<? extends Condition> conditionCls) {
    throw new InvalidConditionArgumentException(
        String.format(
            "Missing required condition argument '%s' for condition '%s'",
            argumentName, conditionCls.getCanonicalName()));
  }

  public static InvalidConditionArgumentException invalidType(
      String arugmentName,
      Class expectedArgumentClass,
      Class suppliedArgumentClass,
      Class<? extends Condition> conditionCls) {
    throw new InvalidConditionArgumentException(
        String.format(
            "Invalid type '%s' provided for argument name '%s' of condition '%s'. Expected type '%s'.",
            suppliedArgumentClass.getCanonicalName(),
            arugmentName,
            conditionCls.getCanonicalName(),
            expectedArgumentClass.getCanonicalName()));
  }
}
