package com.c4wrd.gatekeeper.condition;

import com.c4wrd.gatekeeper.api.Condition;
import com.c4wrd.gatekeeper.api.ConditionProvider;
import com.c4wrd.gatekeeper.condition.impl.StringEqualsCondition;
import com.c4wrd.gatekeeper.condition.impl.StringEqualsIgnoreCaseCondition;

import java.util.HashMap;
import java.util.Map;

public class GatekeeperBaseConditionProvider implements ConditionProvider {

  private static Map<String, Condition> DEFAULT_CONDITIONS = new HashMap<String, Condition>();

  static {
    DEFAULT_CONDITIONS.put("string:equals", new StringEqualsCondition());
    DEFAULT_CONDITIONS.put("string:equals_ignore_case", new StringEqualsIgnoreCaseCondition());
  }

  @Override
  public boolean canProvideCondition(String type) {
    return DEFAULT_CONDITIONS.containsKey(type);
  }

  @Override
  public Condition provideCondition(String type) {
    return DEFAULT_CONDITIONS.get(type);
  }
}
