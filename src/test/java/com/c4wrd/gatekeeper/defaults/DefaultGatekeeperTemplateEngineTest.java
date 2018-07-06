package com.c4wrd.gatekeeper.defaults;

import com.c4wrd.gatekeeper.api.AccessRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultGatekeeperTemplateEngineTest {

  AccessRequest request = AccessRequest.builder()
          .subject(new StringSubject("subject_id"))
          .contextVal("resource_id", 123)
          .build();

  DefaultGatekeeperTemplateEngine resolver = new DefaultGatekeeperTemplateEngine();

  @Test
  void testContextValuesExist() {
    String templatedString = resolver.render("resources:${resource_id}", request);
    assertEquals("resources:123", templatedString, "the value 123 should exist in the templated string");
  }

  @Test
  void testSubjectExists() {
    String templatedString = resolver.render("${subject:id}", request);
    assertEquals(request.getSubject().getId(), templatedString, "the subject id should match the templated subject string");
  }

}