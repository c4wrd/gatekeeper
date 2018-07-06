package com.c4wrd.gatekeeper.condition.impl;

import com.c4wrd.gatekeeper.Gatekeeper;
import com.c4wrd.gatekeeper.api.AccessRequest;
import com.c4wrd.gatekeeper.api.GatekeeperContext;
import com.c4wrd.gatekeeper.defaults.StringSubject;
import com.c4wrd.gatekeeper.exception.InvalidContextValueException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StringEqualsConditionTest {

  GatekeeperContext getContext() {
    return Gatekeeper.builder().build();
  }

  @Test
  void testSuccessSubjectId() {
    StringEqualsCondition condition = new StringEqualsCondition();

    AccessRequest request = AccessRequest.builder()
            .subject(new StringSubject("expected"))
            .build();

    Map<String, Object> args = new HashMap<>();
    args.put("subject:id", "expected");

    assertTrue(condition.fulfills(args, request, getContext()));
  }

  @Test
  void testSuccessContextValue() {
    StringEqualsCondition condition = new StringEqualsCondition();

    AccessRequest request = AccessRequest.builder()
            .contextVal("resource_id", "123")
            .build();

    Map<String, Object> args = new HashMap<>();
    args.put("resource_id", "123");

    assertTrue(condition.fulfills(args, request, getContext()));
  }

  @Test
  void testNotEqualsFulfill() {
    StringEqualsCondition condition = new StringEqualsCondition();

    AccessRequest request = AccessRequest.builder()
            .contextVal("contextVal", "not equal")
            .build();

    Map<String, Object> args = new HashMap<>();
    args.put("contextVal", "value");

    assertFalse(condition.fulfills(args, request, getContext()));
  }

}