package com.c4wrd.gatekeeper.testutils;

import com.c4wrd.gatekeeper.api.Policy;
import com.c4wrd.gatekeeper.api.PolicyProvider;
import com.c4wrd.gatekeeper.api.Subject;
import lombok.Builder;
import lombok.Singular;

import java.util.*;

@Builder
public class TestPolicyProvider implements PolicyProvider {

    @Singular
    private Map<String, List<Policy>> policies;

    @Override
    public List<Policy> provideSubjectPolicies(Subject subject) {
        if ( policies.containsKey(subject.getId()) ) {
            return policies.get(subject.getId());
        }

        return Collections.emptyList();
    }
}
