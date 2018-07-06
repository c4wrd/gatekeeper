package com.c4wrd.gatekeeper.testutils;

import com.c4wrd.gatekeeper.api.Condition;
import lombok.Builder;

import java.util.HashMap;
import java.util.Map;

public class ConditionBuilder {

    private Map<String, Object> args = new HashMap<>();

    public ConditionBuilder arg(String name, String value) {
        this.args.put(name, value);
        return this;
    }

    public Map<String, Object> build() {
        return this.args;
    }
}
