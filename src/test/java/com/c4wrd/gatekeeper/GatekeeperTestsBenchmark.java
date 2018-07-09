package com.c4wrd.gatekeeper;

import com.c4wrd.gatekeeper.api.*;
import com.c4wrd.gatekeeper.condition.GatekeeperBaseConditionProvider;
import com.c4wrd.gatekeeper.defaults.BasicPolicy;
import com.c4wrd.gatekeeper.defaults.StringSubject;
import com.c4wrd.gatekeeper.testutils.ConditionBuilder;
import com.c4wrd.gatekeeper.testutils.TestPolicyProvider;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GatekeeperTestsBenchmark {

  private Gatekeeper getGatekeeper(PolicyProvider... providers) {
    return Gatekeeper.builder()
            .policyProviders(Arrays.asList(providers))
            .conditionProvider(new GatekeeperBaseConditionProvider())
            .build();
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
            .condition(
                "string:equals",
                new ConditionBuilder().arg("resource_name", "permitted_resource_name").build())
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
                    .condition(
                            "string:equals",
                            new ConditionBuilder().arg("subject:id", "Cory").build())
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
                    .condition(
                            "string:equals",
                            new ConditionBuilder().arg("subject:id", "Not Cory").build())
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
                    .condition(
                            "string:equals",
                            new ConditionBuilder().arg("subject:id", "Cory").build())
                    .condition(
                            "string:equals",
                            new ConditionBuilder().arg("example_context_value", "123").build()
                    )
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
                    .condition(
                            "string:equals",
                            new ConditionBuilder()
                                    .arg("subject:id", "Cory")
                                    .arg("example_context_value", "123").build())
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
                      .condition(
                              "string:equals",
                              new ConditionBuilder().arg("example_context_value", "123").build()
                      )
                      .effect(Effect.ALLOW)
                      .build();

      // deny access to resource if the subject is "Michael"
      Policy denyPolicy =
              BasicPolicy.builder()
                      .action("view")
                      .resource("resource")
                      .condition(
                              "string:equals",
                              new ConditionBuilder().arg("subject:id", "Tyler").build())
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
