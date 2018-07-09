package com.c4wrd.gatekeeper.api;

public interface GatekeeperTemplateEngine {
  /**
   * Abstraction over any arbitrary templating engine that will resolve all of the resource
   * template variables (should they exist) in a given resource string for a given access request.
   * For example, by using the default StringSubstitutorTemplateResolver implementation, the string
   * "account:${ctx:id}", given context contains the variable "id" with value "cory", would resolve
   * to "account:cory"
   *
   * @param template The template to provide templating for
   * @param request The access request
   * @return The templated string, or the original string if no templating is needed.
   */
  String render(String template, AccessRequest request);
}
