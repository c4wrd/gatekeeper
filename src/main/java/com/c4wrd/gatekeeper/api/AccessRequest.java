package com.c4wrd.gatekeeper.api;

import lombok.Data;

import java.util.Map;

@Data
public class AccessRequest {
  private Subject subject;
  private Map<String, Object> context;
  private String resource;
  private String action;
}
