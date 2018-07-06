package com.c4wrd.gatekeeper.api;

import java.util.List;

public interface AuditLogger {
    void logAccessGranted(AccessRequest request, List<Policy> permittingPolicies);
    void logAccessDenied(AccessRequest request, List<Policy> permittingPolicies);
    void logExceptionThrown(AccessRequest request, Policy policy, Exception exception);
    void logConditionNotFound(AccessRequest request, Policy policy, String conditionId);
}
