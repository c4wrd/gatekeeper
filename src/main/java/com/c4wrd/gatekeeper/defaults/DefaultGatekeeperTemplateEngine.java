package com.c4wrd.gatekeeper.defaults;

import com.c4wrd.gatekeeper.api.AccessRequest;
import com.c4wrd.gatekeeper.api.GatekeeperTemplateEngine;
import com.google.common.collect.Maps;
import org.apache.commons.text.StringSubstitutor;

import java.util.Map;

/**
 * A default implementation of the GatekeeperTemplateEngine that will perform the following things:
 *
 * <p>1. For each context value in the access request, it will be made available as ${ctx:ID} in the
 * template, where `ID` is the identifier of the context value
 *
 * <p>2. The subjects id will be made available as ${subject:id}
 */
public class DefaultGatekeeperTemplateEngine implements GatekeeperTemplateEngine {

  @Override
  public String render(String template, AccessRequest request) {
    StringSubstitutor substitutor = new StringSubstitutor(makeValueMap(request));
    return substitutor.replace(template);
  }

  private Map<String, Object> makeValueMap(AccessRequest request) {
    Map<String, Object> values = Maps.newHashMap();
    values.putAll(request.getContext());
    if (request.getSubject() != null) {
      values.put("subject:id", request.getSubject().getId());
    }
    return values;
  }
}
