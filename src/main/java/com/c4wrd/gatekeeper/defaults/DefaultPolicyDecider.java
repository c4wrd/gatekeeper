package com.c4wrd.gatekeeper.defaults;

import com.c4wrd.gatekeeper.api.AccessRequest;
import com.c4wrd.gatekeeper.api.Effect;
import com.c4wrd.gatekeeper.api.Policy;
import com.c4wrd.gatekeeper.api.PolicyDecider;

import java.util.List;

public class DefaultPolicyDecider implements PolicyDecider {
  /**
   * Given the request and matching policies, the default behavior of this decider is to make a
   * decision based on the following cases.
   *
   * <p>1. If the matching policies list is empty, deny. 2. If there is a policy with a 'DENY'
   * effect, deny regardless of any 'ALLOW' affects. 3. If there is a policy with an 'ALLOW' effect,
   * allow
   *
   * @param request The access request
   * @param matchingPolicies A list of policies that have had their conditions met based on the
   *     access request. This may be empty, and is up to implementer as to whether or not in this
   *     case the request will be permitted or not.
   * @return Effect.DENY if matchingPolicies is empty or contains a policy with a "DENY" effect. Otherwise
   * "ALLOW".
   */
  @Override
  public Effect decide(AccessRequest request, List<Policy> matchingPolicies) {
    if ( matchingPolicies.isEmpty() ) {
        return Effect.DENY;
    }

    for ( Policy policy : matchingPolicies ) {
        if ( policy.getEffect() == Effect.DENY ) {
            return Effect.DENY;
        }
    }

    return Effect.ALLOW;
  }
}
