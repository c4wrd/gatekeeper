package com.c4wrd.gatekeeper.condition.impl;

import com.c4wrd.gatekeeper.api.AccessRequest;
import com.c4wrd.gatekeeper.api.Condition;

import java.util.Map;

public class StringEqualsIgnoreCaseCondition extends Condition {

    /**
     * Verifies that each provided argument key equals the value
     * in the request context
     * @param request The access request being checked
     * @return
     */

    @Override
    public boolean fulfills(Map<String, Object> args, AccessRequest request) {
        for ( String key : args.keySet() ) {
            String expectedValue = super.resolveArgument(args, key, String.class);
            String providedValue = super.resolveContext(request.getContext(), key, String.class);

            if (!expectedValue.equalsIgnoreCase(providedValue)) {
                return false;
            }
        }

        return true;
    }
}
