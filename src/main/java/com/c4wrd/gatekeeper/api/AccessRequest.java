package com.c4wrd.gatekeeper.api;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import org.apache.commons.text.lookup.StringLookup;

import java.util.Map;

@Data
@Builder
public class AccessRequest {
  private Subject subject;
  @Singular("contextVal")
  private Map<String, Object> context;
  private String resource;
  private String action;
}
