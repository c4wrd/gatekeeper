package com.c4wrd.gatekeeper.api;

import java.util.Map;

public interface PolicyCondition {
  String getType();

  Map<String, Object> getArguments();
}
