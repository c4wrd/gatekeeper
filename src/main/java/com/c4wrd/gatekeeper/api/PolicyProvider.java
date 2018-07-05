package com.c4wrd.gatekeeper.api;

import java.util.List;

public interface PolicyProvider {
    List<Policy> provideSubjectPolicies(Subject subject);
}
