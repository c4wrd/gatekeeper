package com.c4wrd.gatekeeper.condition;

import com.c4wrd.gatekeeper.api.Condition;
import com.c4wrd.gatekeeper.api.ConditionProvider;

import java.util.HashMap;
import java.util.Map;

public class GatekeeperDefaultConditionProvider implements ConditionProvider {

    private static Map<String, Condition> DEFAULT_CONDITIONS = new HashMap<String, Condition>();

    static {
        DEFAULT_CONDITIONS.put("string_equals", new StringEqualsCondition());
    }

    public Condition provideCondition(String type) {
        return null;
    }
}
