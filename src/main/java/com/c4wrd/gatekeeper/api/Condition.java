package com.c4wrd.gatekeeper.api;

import java.util.Map;

public interface Condition {
    /**
     * Given the context and arguments,
     * check if the condition is fulfilled by subject.
     * @param subject The subject being authorized
     * @param context The context of the access request
     * @return
     */
    boolean fulfills(Subject subject, Object value, Map<String, Object> context);
}
