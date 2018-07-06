package com.c4wrd.gatekeeper;

import com.c4wrd.gatekeeper.api.*;
import com.c4wrd.gatekeeper.util.ResourceMatcher;

import java.util.LinkedList;
import java.util.List;

public class Gatekeeper {

  private List<PolicyProvider> policyProviders;

  private ConditionProvider conditionProvider;

  private ResourceTemplateResolver resourceTemplateResolver;

  public Gatekeeper(
      List<PolicyProvider> policyProviderList,
      ConditionProvider conditionProvider,
      ResourceTemplateResolver resourceTemplateResolver) {
    this.policyProviders = policyProviderList;
    this.conditionProvider = conditionProvider;
    this.resourceTemplateResolver = resourceTemplateResolver;
  }

  public boolean isPermitted(AccessRequest request) {
    List<Policy> candidatePolicies = new LinkedList<>();

    // template out the access request resource based on the context
    String accessRequestResource = resourceTemplateResolver.resolveResourceTemplate(request.getResource(), request);

    /**
     * 1. Create a list of candidate policies by iterating over each policy provider and obtain a
     * list of all policies that contain the action in the action request and contain a resource
     * that matches the resource in the access request
     */

    for (PolicyProvider provider : policyProviders) {
      for (Policy policy : provider.provideSubjectPolicies(request.getSubject())) {
        // check if the policy permits the specified action
        if (!policy.getActions().contains("*")
            && !policy.getActions().contains(request.getAction())) {
          continue;
        }

        for (String resource : policy.getResources()) {
          // template out the policy resource based on the context
          String policyResource = resourceTemplateResolver.resolveResourceTemplate(resource, request);
          if (ResourceMatcher.matches(policyResource, accessRequestResource)) {
            candidatePolicies.add(policy);
            break;
          }
        }
      }
    }

    if ( candidatePolicies.isEmpty() ) {
      return false;
    }

  }
}
