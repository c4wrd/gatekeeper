package com.c4wrd.gatekeeper;

import com.c4wrd.gatekeeper.api.*;
import com.c4wrd.gatekeeper.condition.GatekeeperBaseConditionProvider;
import com.c4wrd.gatekeeper.defaults.BasicPolicy;
import com.c4wrd.gatekeeper.defaults.StringSubject;
import com.c4wrd.gatekeeper.testutils.ConditionBuilder;
import com.c4wrd.gatekeeper.testutils.TestPolicyProvider;
import com.google.common.collect.Lists;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GatekeeperTestsBenchmark {

  private Gatekeeper getGatekeeper(PolicyProvider... providers) {
    return Gatekeeper.builder()
            .policyProviders(Arrays.asList(providers))
            .conditionProvider(new GatekeeperBaseConditionProvider())
            .build();
  }

  public static class StringNamespace {
    public boolean equal(String value1, String value2) {
      return value1 != null && value2 != null && value1.equalsIgnoreCase(value2);
    }
  }

  @Test
  void testJexl() {
    Map<String, Object> ns = new HashMap<>();
    ns.put("string", new StringNamespace());

    JexlEngine jexl = new JexlBuilder()
            .cache(512)
            .namespaces(ns)
            .create();

    JexlExpression exp = jexl.createExpression("strings:equal(var1, var2)");
    MapContext context = new MapContext();
    context.set("var1", "value 1");
    context.set("var2", "value 2");
    context.set("subject", new StringSubject("Cory"));
    boolean output = (Boolean) jexl.createExpression("string:equal(subject.id, 'Cory')").evaluate(context);
    System.out.println(output);
  }

  /**
   * Test to verify that a policy without any conditions applied to a subject will permit
   * successfully
   */
  @Test
  void testPolicyNoConditions() {
    Subject testSubject = new StringSubject("Cory");
    Policy simplePolicy =
        BasicPolicy.builder().action("view").resource("resource").effect(Effect.ALLOW).build();

    PolicyProvider provider =
        TestPolicyProvider.builder()
            .policy(testSubject.getId(), Collections.singletonList(simplePolicy))
            .build();

    Gatekeeper keeper = getGatekeeper(provider);

    AccessRequest request =
        AccessRequest.builder().subject(testSubject).action("view").resource("resource").build();

    assertEquals(Effect.ALLOW, keeper.enforce(request));
  }

  @Test
  void testPolicyConditionWithContextValue() {
    Subject testSubject = new StringSubject("Cory");
    Policy simplePolicy =
        BasicPolicy.builder()
            .action("view")
            .resource("resource")
            .condition("strings:equal(resource_name, 'permitted_resource_name'")
            .effect(Effect.ALLOW)
            .build();

    PolicyProvider provider =
        TestPolicyProvider.builder()
            .policy(testSubject.getId(), Collections.singletonList(simplePolicy))
            .build();

    Gatekeeper keeper = getGatekeeper(provider);

    AccessRequest request =
        AccessRequest.builder()
            .subject(testSubject)
            .action("view")
            .resource("resource")
            .contextVal("resource_name", "permitted_resource_name")
            .build();

    assertEquals(Effect.ALLOW, keeper.enforce(request));
  }

  /**
   * Test that the subject id can be used in a policy as a means
   * of permitting an action.
   */
  @Test
  void testPolicyWithSubjectValue() {
    Subject testSubject = new StringSubject("Cory");
    Policy simplePolicy =
            BasicPolicy.builder()
                    .action("view")
                    .resource("resource")
                    .condition("strings:equal(subject.id, 'Cory')")
                    .effect(Effect.ALLOW)
                    .build();

    PolicyProvider provider =
            TestPolicyProvider.builder()
                    .policy(testSubject.getId(), Collections.singletonList(simplePolicy))
                    .build();

    Gatekeeper keeper = getGatekeeper(provider);

    AccessRequest request =
            AccessRequest.builder()
                    .subject(testSubject)
                    .action("view")
                    .resource("resource")
                    .build();

    assertEquals(Effect.ALLOW, keeper.enforce(request));
  }

  @Test
  void testPolicyWithNonMatchingConditions() {
    Subject testSubject = new StringSubject("Cory");
    Policy simplePolicy =
            BasicPolicy.builder()
                    .action("view")
                    .resource("resource")
                    .condition("strings:equal(subject.id, 'Not Cory')")
                    .effect(Effect.ALLOW)
                    .build();

    PolicyProvider provider =
            TestPolicyProvider.builder()
                    .policy(testSubject.getId(), Collections.singletonList(simplePolicy))
                    .build();

    Gatekeeper keeper = getGatekeeper(provider);

    AccessRequest request =
            AccessRequest.builder()
                    .subject(testSubject)
                    .action("view")
                    .resource("resource")
                    .build();

    assertEquals(Effect.DENY, keeper.enforce(request));
  }

  @Test
  void testPolicyMultipleConditions() {
    Subject testSubject = new StringSubject("Cory");
    Policy simplePolicy =
            BasicPolicy.builder()
                    .action("view")
                    .resource("resource")
                    .condition("string:equals(subject.id, 'Cory')")
                    .condition("string:equals(example_context_value, '123')")
                    .effect(Effect.ALLOW)
                    .build();

    PolicyProvider provider =
            TestPolicyProvider.builder()
                    .policy(testSubject.getId(), Collections.singletonList(simplePolicy))
                    .build();

    Gatekeeper keeper = getGatekeeper(provider);

    AccessRequest request =
            AccessRequest.builder()
                    .subject(testSubject)
                    .action("view")
                    .resource("resource")
                    .contextVal("example_context_value", 123)
                    .build();

    assertEquals(Effect.ALLOW, keeper.enforce(request));
  }

  @Test
  void testMultipleConditionsWithFailingCondition() {
    Subject testSubject = new StringSubject("Cory");
    Subject denySubject = new StringSubject("Not Cory");
    Policy simplePolicy =
            BasicPolicy.builder()
                    .action("view")
                    .resource("resource")
                    .condition("strings:equal(subject.id, 'Cory'")
                    .condition("strings:equal(example_context_value, '123'")
                    .effect(Effect.ALLOW)
                    .build();

    PolicyProvider provider =
            TestPolicyProvider.builder()
                    .policy(testSubject.getId(), Collections.singletonList(simplePolicy))
                    .policy(denySubject.getId(), Collections.singletonList(simplePolicy))
                    .build();

    Gatekeeper keeper = getGatekeeper(provider);

    AccessRequest successRequest =
            AccessRequest.builder()
                    .subject(testSubject)
                    .action("view")
                    .resource("resource")
                    .contextVal("example_context_value", 123)
                    .build();

    AccessRequest denyRequest =
            AccessRequest.builder()
                    .subject(denySubject)
                    .action("view")
                    .resource("resource")
                    .contextVal("example_context_value", 123)
                    .build();

    assertEquals(Effect.ALLOW, keeper.enforce(successRequest));
    assertEquals(Effect.DENY, keeper.enforce(denyRequest));
  }

  @Test
  void testMultipleMatchingPoliciesWithDenyEffect() {
      Subject testSubject = new StringSubject("Cory");
      Subject denySubject = new StringSubject("Tyler");
      Policy simplePolicy =
              BasicPolicy.builder()
                      .action("view")
                      .resource("resource")
                      .condition("strings:equal(example_context_value, '123')")
                      .effect(Effect.ALLOW)
                      .build();

      // deny access to resource if the subject is "Michael"
      Policy denyPolicy =
              BasicPolicy.builder()
                      .action("view")
                      .resource("resource")
                      .condition("strings:equal(subject.id, 'Tyler')")
                      .effect(Effect.DENY)
                      .build();

    PolicyProvider provider =
        TestPolicyProvider.builder()
            .policy(testSubject.getId(), Lists.newArrayList(simplePolicy, denyPolicy))
            .policy(denySubject.getId(), Lists.newArrayList(simplePolicy, denyPolicy))
            .build();

      Gatekeeper keeper = getGatekeeper(provider);

      AccessRequest successRequest =
              AccessRequest.builder()
                      .subject(testSubject)
                      .action("view")
                      .resource("resource")
                      .contextVal("example_context_value", 123)
                      .build();

      AccessRequest denyRequest =
              AccessRequest.builder()
                      .subject(denySubject)
                      .action("view")
                      .resource("resource")
                      .contextVal("example_context_value", 123)
                      .build();

      assertEquals(Effect.ALLOW, keeper.enforce(successRequest));
      assertEquals(Effect.DENY, keeper.enforce(denyRequest));
  }

  @Test
  void testNoPoliciesAppliedToSubject() {

  }
}
