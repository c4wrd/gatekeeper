package com.c4wrd.gatekeeper;

import com.c4wrd.gatekeeper.api.*;
import com.c4wrd.gatekeeper.defaults.DefaultAuditLogger;
import com.c4wrd.gatekeeper.defaults.DefaultGatekeeperTemplateEngine;
import com.c4wrd.gatekeeper.defaults.DefaultPolicyDecider;
import com.c4wrd.gatekeeper.util.ResourceMatcher;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.Builder;
import lombok.Singular;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Builder
public class Gatekeeper implements GatekeeperContext {

  @Singular List<ConditionProvider> conditionProviders;

  @Builder.Default GatekeeperTemplateEngine templateEngine = new DefaultGatekeeperTemplateEngine();

  @Builder.Default private AuditLogger auditLogger = new DefaultAuditLogger();

  private List<PolicyProvider> policyProviders;

  @Builder.Default private PolicyDecider policyDecider = new DefaultPolicyDecider();

  /**
   * @param request The access request
   * @return Returns a list of policies that match the resource and action * of the given access
   *     request
   */
  private List<Policy> getCandidatePolicies(AccessRequest request) {
    List<Policy> candidatePolicies = new LinkedList<>();

    /*
     1. Create a list of candidate policies by iterating over each policy provider and obtain a
     list of all policies that contain the action in the action request and contain a resource
     that matches the resource in the access request
    */
    for (PolicyProvider provider : policyProviders) {
      for (Policy policy : provider.provideSubjectPolicies(request.getSubject())) {
        if (policyIsCandidate(request, policy)) {
          candidatePolicies.add(policy);
        }
      }
    }
    return candidatePolicies;
  }

  /**
   * Evalute whether or not a policy is a candidate for an AccessRequest
   *
   * @param request The request being checked
   * @param policy The policy to check for candidacy
   * @return Whether or not policy has the actions and resources that can fulfill this request,
   *     without checking it's conditions
   */
  private boolean policyIsCandidate(AccessRequest request, Policy policy) {
    // check if the policy permits the specified action
    if (!policy.getActions().contains(Constants.WILDCARD_ACTION)
        && !policy.getActions().contains(request.getAction())) {
      return false;
    }

    // check if the resource in the access request matches a resource pattern
    // in the policy
    for (String resource : policy.getResources()) {
      // template out the policy resource based on the context
      try {
        String policyResource = templateEngine.render(resource, request);
        if (ResourceMatcher.matches(policyResource, request.getResource())) {
          return true;
        }
      } catch (Exception ex) {
        // catch any templating exceptions and log it
        if (auditLogger != null) {
          auditLogger.logExceptionThrown(request, policy, ex);
        }
      }
    }

    return false;
  }

  private List<Policy> evaluateCandidatePolicies(
      AccessRequest request, List<Policy> candidatePolicies) {
    List<Policy> permittingPolicies = new LinkedList<>();

    for (Policy policy : candidatePolicies) {
      try {
        // evaluate the policy
        Optional<Effect> policyEffect = evaluatePolicy(request, policy);
        if (policyEffect.isPresent()) {
          permittingPolicies.add(policy);
        }
      } catch (Exception ex) {
        auditLogger.logExceptionThrown(request, policy, ex);
      }
    }

    return permittingPolicies;
  }

  /**
   * Evaluates a single policy to determine if all of it's conditions were met. If the conditions
   * were not met, a null Effect is returned, denoting the access request was not subject to this
   * policy and no action should be taken.
   *
   * @param request
   * @param policy
   * @return
   */
  private Optional<Effect> evaluatePolicy(AccessRequest request, Policy policy) {
    Map<String, Map<String, Object>> conditionsAndArgs = policy.getConditionsAndArgs();

    if (conditionsAndArgs == null || conditionsAndArgs.isEmpty()) {
      return Optional.of(policy.getEffect());
    }

    Multimap<Condition, Map<String, Object>> conditions = HashMultimap.create();

    // iterate over each condition id and arguments
    for (Map.Entry<String, Map<String, Object>> entry : conditionsAndArgs.entrySet()) {
      String conditionId = entry.getKey();
      Map<String, Object> conditionArgs = templateArguments(request, entry.getValue());
      boolean conditionProvidedFor = false;

      for (ConditionProvider conditionProvider : conditionProviders) {
        if (conditionProvider.canProvideCondition(conditionId)) {
          Condition condition = conditionProvider.provideCondition(conditionId);
          conditions.put(condition, conditionArgs);
          conditionProvidedFor = true;
          // sanity check that only one condition provider can provide for a condition
          break;
        }
      }

      // if the condition was not valid, log it, then return null as this policy does not match the
      // request
      if (!conditionProvidedFor) {
        if (auditLogger != null) {
          auditLogger.logConditionNotFound(request, policy, conditionId);
        }
        return Optional.empty();
      }
    }

    if (!allConditionsMet(request, conditions)) {
      return Optional.empty();
    }

    // at this point, all conditions were met, return the effect allowed by the policy
    return Optional.of(policy.getEffect());
  }

  /**
   * Perform templating on argument values in the event the argument to a condition contains
   * template variables
   *
   * @param value
   * @return
   */
  private Map<String, Object> templateArguments(AccessRequest request, Map<String, Object> value) {
    return value
        .entrySet()
        .stream()
        .collect(
            Collectors.toMap(
                it -> templateEngine.render(it.getKey(), request), Map.Entry::getValue));
  }

  private boolean allConditionsMet(
          AccessRequest request, Multimap<Condition, Map<String, Object>> conditions) {
    for ( Condition condition : conditions.keys() ) {
      for ( Map<String, Object> args : conditions.get(condition)) {
        // if the conditions aren't met, this policy is no longer a candidate
        if (!condition.fulfills(args, request, this)) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public List<Policy> getPermittingPolicies(AccessRequest request) {
    List<Policy> candidatePolicies = getCandidatePolicies(request);
    return evaluateCandidatePolicies(request, candidatePolicies);
  }

  @Override
  public Effect enforce(AccessRequest request) {
    List<Policy> permittingPolicies = getPermittingPolicies(request);

    Effect decision = policyDecider.decide(request, permittingPolicies);

    if (decision == Effect.ALLOW) {
      auditLogger.logAccessGranted(request, permittingPolicies);
    } else {
      auditLogger.logAccessDenied(request, permittingPolicies);
    }

    return decision;
  }

  @Override
  public GatekeeperTemplateEngine getTemplateEngine() {
    return templateEngine;
  }
}
