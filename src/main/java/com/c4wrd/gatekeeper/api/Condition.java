package com.c4wrd.gatekeeper.api;

import com.c4wrd.gatekeeper.exception.InvalidConditionArgumentException;
import com.c4wrd.gatekeeper.exception.InvalidContextValueException;

import java.util.Map;
import java.util.Set;

public abstract class Condition {

  protected <T> T resolveArgument(Map<String, Object> arguments, String name, Class<T> argumentClass) {
    if (!arguments.containsKey(name)) {
      throw InvalidConditionArgumentException.missing(name, getClass());
    }
    Object value = arguments.get(name);
    if (!argumentClass.isAssignableFrom(value.getClass())) {
      throw InvalidConditionArgumentException.invalidType(name, argumentClass, value.getClass(), getClass());
    }

    return (T) value;
  }

  protected <T> T resolveContext(Map<String, Object> context, String name, Class<T> contextValueClass) {
    if (!context.containsKey(name)) {
      throw InvalidContextValueException.missing(name, getClass());
    }
    Object value = context.get(name);
    if (!contextValueClass.isAssignableFrom(value.getClass())) {
      throw InvalidContextValueException.invalidType(name, contextValueClass, value.getClass(), getClass());
    }

    return (T) value;
  }

  /**
   * Given the context and arguments, check if the condition is fulfilled by subject.
   *
   * @param request The access request being checked
   * @return
   */
  abstract public boolean fulfills(Map<String, Object> arguments, AccessRequest request);
}
