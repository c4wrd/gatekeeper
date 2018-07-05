package com.c4wrd.gatekeeper.condition;

import com.c4wrd.gatekeeper.api.Condition;
import com.c4wrd.gatekeeper.api.Subject;

import java.util.Map;

public class StringEqualsCondition implements Condition {
    public boolean check(Subject subject, Map<String, Object> context, Map<String, Object> arguments) {
        return false;
    }
}
