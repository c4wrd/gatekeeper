package com.c4wrd.gatekeeper;

import com.c4wrd.gatekeeper.api.*;
import com.c4wrd.gatekeeper.defaults.DefaultAuditLogger;
import com.c4wrd.gatekeeper.defaults.DefaultGatekeeperTemplateEngine;
import com.c4wrd.gatekeeper.defaults.DefaultPolicyDecider;
import com.c4wrd.gatekeeper.namespaces.StringsNamespace;
import com.c4wrd.gatekeeper.util.ResourceMatcher;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;

import java.util.*;
import java.util.stream.Collectors;

public class Gatekeeper implements GatekeeperContext {

  GatekeeperTemplateEngine templateEngine = new DefaultGatekeeperTemplateEngine();

  private AuditLogger auditLogger = new DefaultAuditLogger();

  private List<PolicyProvider> policyProviders;

  private PolicyDecider policyDecider = new DefaultPolicyDecider();

  private JexlEngine engine;

  private Gatekeeper() {}

  public static Builder defaultBuilder() {
    return new Builder()
        .addJexlNamespace("strings", new StringsNamespace())
        .setPolicyDecider(new DefaultPolicyDecider())
        .auditLogger(new DefaultAuditLogger())
        .templateEngine(new DefaultGatekeeperTemplateEngine());
  }

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
    List<String> conditions = policy.getConditions();

    if (conditions == null || conditions.isEmpty()) {
      return Optional.of(policy.getEffect());
    }

    try {
      if (!allConditionsMet(request, conditions)) {
        return Optional.empty();
      }
    } catch (Exception ex) {
      if (auditLogger != null) {
        auditLogger.logExceptionThrown(request, policy, ex);
      }
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

  private boolean allConditionsMet(AccessRequest request, List<String> conditions) {
    for (String condition : conditions) {
      JexlExpression expression = engine.createExpression(condition);
      MapContext context = new MapContext(new HashMap<>(request.getContext()));
      context.set("subject", request.getSubject());
      context.set("action", request.getAction());
      context.set("resource", request.getResource());
      boolean result = (Boolean) expression.evaluate(context);
      if (!result) {
        return false;
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

  public static class Builder {

    private Gatekeeper instance;

    private JexlBuilder builder;

    private Map<String, Object> namespaces;

    public Builder() {
      this.instance = new Gatekeeper();
      this.builder = new JexlBuilder();
      this.namespaces = new HashMap<>();
    }

    public Builder setPolicyDecider(PolicyDecider decider) {
      instance.policyDecider = decider;
      return this;
    }

    public Builder policyProviders(PolicyProvider... providers) {
      if (this.instance.policyProviders == null) {
        this.instance.policyProviders = new LinkedList<>();
      }
      this.instance.policyProviders.addAll(Arrays.asList(providers));
      return this;
    }

    public Builder addJexlNamespace(String namespace, Object instance) {
      this.namespaces.put(namespace, instance);
      return this;
    }

    public Builder templateEngine(GatekeeperTemplateEngine templateEngine) {
      this.instance.templateEngine = templateEngine;
      return this;
    }

    public Builder auditLogger(AuditLogger logger) {
      this.instance.auditLogger = logger;
      return this;
    }

    public Gatekeeper build() {
      JexlEngine engine = this.builder.namespaces(namespaces).create();
      this.instance.engine = engine;
      return this.instance;
    }
  }
}
