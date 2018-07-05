package com.c4wrd.gatekeeper.api;

import java.util.List;
import java.util.Map;

public interface Policy {
    String getDescription();
    List<String> getActions();
    List<String> getResources();
    Map<String, PolicyCondition> getConditions();
}
