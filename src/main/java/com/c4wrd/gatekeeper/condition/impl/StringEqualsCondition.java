package com.c4wrd.gatekeeper.condition.impl;

import com.c4wrd.gatekeeper.api.AccessRequest;
import com.c4wrd.gatekeeper.api.Condition;
import com.c4wrd.gatekeeper.api.GatekeeperContext;
import com.google.common.reflect.TypeToken;
import org.apache.commons.text.lookup.StringLookup;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StringEqualsCondition extends Condition {

  /**
   * Verifies that each provided argument key equals the value in the request context
   *
   * @param request The access request being checked
   * @return
   */
  @Override
  public boolean fulfills(
      Map<String, Object> args, AccessRequest request, GatekeeperContext context) {
    for ( String expectedValueKey : args.keySet() ) {
      // get a list of candidate values to check
      List<String> candidates = super.resolveArgument(args, expectedValueKey);

      for (String expectedValue : candidates) {
        String actualValue = (String) request.getContext().get(expectedValueKey);
        if ( expectedValue.equals(actualValue) ) {
          break;
        } else {
          return false;
        }
      }
    }

//    for (String expectedValueKey : args.keySet()) {
//      if (args.get(expectedValueKey) instanceof List) {
//        List<String> expectedValues =
//            super.resolveArgument(args, expectedValueKey, new TypeToken<List<String>>() {});
//        for (String expectedValue : expectedValues) {}
//      } else {
//        String expectedValue = super.resolveArgument(args, expectedValueKey, String.class);
//        String actualValue = lookup.lookup(expectedValueKey);
//        if (!expectedValue.equals(actualValue)) {
//          return false;
//        }
//      }
//    }

    return true;
  }
}
