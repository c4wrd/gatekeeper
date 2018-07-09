package com.c4wrd.gatekeeper.api;

import java.util.List;
import java.util.Map;

public interface Policy {
  Effect getEffect();

  /**
   * Identifiable name for the policy for audit logging purposes
   */
  String getId();

  String getDescription();
  /**
   * List of actions that can be taken on the resources in this policy
   */
  List<String> getActions();

  /**
   * List of resources this policy permits
   */
  List<String> getResources();

  /**
   * Map of condition name -> map of condition arguments
   */
  List<String> getConditions();
}
