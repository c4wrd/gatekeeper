package com.c4wrd.gatekeeper.api;

import java.util.List;

public interface PolicyDecider {
  /**
   * Decides whether or not to permit or deny the access request given matching policies.
   *
   * @param matchingPolicies A list of policies that have had their conditions met based on the
   *     access request. This may be empty, and is up to implementer as to whether or not in this
   *     case the request will be permitted or not.
   * @return Whether or not the access request is permitted based on the list of matching policies
   */
  Effect decide(AccessRequest request, List<Policy> matchingPolicies);
}
