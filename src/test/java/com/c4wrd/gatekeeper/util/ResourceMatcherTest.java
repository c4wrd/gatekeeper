package com.c4wrd.gatekeeper.util;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

class ResourceMatcherTest {

  private void shouldPass(String pattern, String resource) {
    System.out.println(String.format("checking if %s -> %s", pattern, resource));
    assertTrue(ResourceMatcher.matches(pattern, resource));
  }

  private void shouldFail(String pattern, String resource) {
    System.out.println(String.format("checking if %s -> %s", pattern, resource));
    assertFalse(ResourceMatcher.matches(pattern, resource));
  }

  @Test
  void matches() {
    shouldPass("users:cory", "users:cory");
    shouldPass("users:*", "users:cory");
    shouldPass("users*", "users:cory");
    shouldPass("static:resource:name", "static:resource:name");
    shouldPass("users:*:name", "users:cory:name");
    shouldPass("arn:aws:s3:::*/home/*", "arn:aws:s3:::bucket/home/anything");
    shouldPass("arn:aws:s3:::*", "arn:aws:s3:::bucket/home/");
    shouldPass("arn:aws:s3:::*", "arn:aws:s3:::bucket");
    shouldPass("arn:aws:s3:::*/*", "arn:aws:s3:::bucket/*");
    shouldPass("arn:aws:s3:::*", "arn:aws:s3:::*");

    shouldFail("users:cory", "users:corys");
    shouldFail("users:", "users:cory");
    shouldFail("abc", "def");
    shouldFail("users:*:cory", "users:cory");
    shouldFail("users:cory:name", "users:test:name");
    shouldFail("arn:aws:s3:::*/required/*", "arn:aws:s3:::bucket/");
    shouldFail("arn:aws:s3:::*/", "arn:aws:s3:::*");
    shouldFail("arn:aws:s3:::*/*", "arn:aws:s3:::*");
  }
}
