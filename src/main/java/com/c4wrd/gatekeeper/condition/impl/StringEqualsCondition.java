package com.c4wrd.gatekeeper.condition.impl;

import com.c4wrd.gatekeeper.api.AccessRequest;
import com.c4wrd.gatekeeper.api.Condition;
import com.c4wrd.gatekeeper.api.GatekeeperContext;
import com.c4wrd.gatekeeper.api.GatekeeperTemplateEngine;
import com.c4wrd.gatekeeper.defaults.DefaultGatekeeperTemplateEngine;
import org.apache.commons.text.lookup.StringLookup;

import java.util.Map;

public class StringEqualsCondition extends Condition {

  /**
   * Verifies that each provided argument key equals the value in the request context
   *
   * @param request The access request being checked
   * @return
   */
  @Override
  public boolean fulfills(Map<String, Object> args, AccessRequest request, GatekeeperContext context) {
    StringLookup lookup = context.getTemplateEngine().getVariableResolver(request);

    for (String expectedValueKey : args.keySet()) {
      String expectedValue = super.resolveArgument(args, expectedValueKey, String.class);
      String actualValue = lookup.lookup(expectedValueKey);
      if (!expectedValue.equals(actualValue)) {
        return false;
      }
    }

    return true;
  }
}
