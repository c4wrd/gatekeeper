package com.c4wrd.gatekeeper.api;

import com.c4wrd.gatekeeper.exception.InvalidConditionArgumentException;
import com.c4wrd.gatekeeper.exception.InvalidContextValueException;
import com.google.common.reflect.TypeToken;

import java.util.Map;

public abstract class Condition {

  protected <T> T resolveArgument(
          Map<String, Object> arguments, String name, Class<T> argumentClass) {
    return resolveArgument(arguments, name, TypeToken.of(argumentClass));
  }

  protected <T> T resolveArgument(
          Map<String, Object> arguments, String name, TypeToken<T> typeToken) {
    if (!arguments.containsKey(name)) {
      throw InvalidConditionArgumentException.missing(name, getClass());
    }
    Object value = arguments.get(name);
    if (!typeToken.isSupertypeOf(value.getClass())) {
      throw InvalidConditionArgumentException.invalidType(
              name, typeToken.getRawType(), value.getClass(), getClass());
    }

    return (T) value;
  }

  protected <T> T resolveContext(
          Map<String, Object> context, String name, Class<T> contextValueClass) {
    return resolveContext(context, name, TypeToken.of(contextValueClass));
  }

  protected <T> T resolveContext(
          Map<String, Object> context, String name, TypeToken<T> contextType) {
    if (!context.containsKey(name)) {
      throw InvalidContextValueException.missing(name, getClass());
    }
    Object value = context.get(name);
    if (!contextType.isSupertypeOf(value.getClass())) {
      throw InvalidContextValueException.invalidType(
              name, contextType.getRawType(), value.getClass(), getClass());
    }

    return (T) value;
  }

  /**
   * Given the context and arguments, check if the condition is fulfilled by subject.
   *
   * @param request The access request being checked
   * @return
   */
  public abstract boolean fulfills(Map<String, Object> arguments, AccessRequest request, GatekeeperContext context);
}
