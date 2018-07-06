package com.c4wrd.gatekeeper.api;

import java.util.List;

public interface PolicyProvider {
  /**
   * @param subject The subject that policies should be provided for.
   * @return A list of policies applied to the subject
   */
  List<Policy> provideSubjectPolicies(Subject subject);
}
