package com.c4wrd.gatekeeper.api;

/**
 * Intended to be implemented when using this framework. Subject is an abstract concept that will
 * identify some entity, such as a user or service.
 */
public interface Subject {
  String getId();
}
