package com.c4wrd.gatekeeper.defaults;

import com.c4wrd.gatekeeper.api.AccessRequest;
import com.c4wrd.gatekeeper.api.AuditLogger;
import com.c4wrd.gatekeeper.api.Policy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DefaultAuditLogger implements AuditLogger {

  private static final Logger logger = LoggerFactory.getLogger(DefaultAuditLogger.class);

  @Override
  public void logAccessGranted(AccessRequest request, List<Policy> permittingPolicies) {
    logger.info(
        "Access was granted for subject {} to perform action {} on resource {}.",
        request.getSubject().getId(),
        request.getAction(),
        request.getResource());
  }

  @Override
  public void logAccessDenied(AccessRequest request, List<Policy> permittingPolicies) {
    if (permittingPolicies.isEmpty()) {
      logger.info(
          "No policy was found to permit subject '{}' to perform action '{}' on resource '{}'",
          request.getSubject().getId(),
          request.getAction(),
          request.getResource());
    } else {
      logger.info(
          "Access was denied for subject '{}' to perform action '{}' on resource '{}'.",
          request.getSubject().getId(),
          request.getAction(),
          request.getResource());
    }
  }

  @Override
  public void logExceptionThrown(AccessRequest request, Policy policy, Exception exception) {
    logger.error(
        "An exception was thrown checking if subject {} can perform '{}' on '{}' by policy '{}'",
        request.getSubject().getId(),
        request.getAction(),
        request.getResource(),
        policy.getId(),
        exception);
  }

  @Override
  public void logConditionNotFound(AccessRequest request, Policy policy, String conditionId) {
    logger.error("A condition '{}' in policy '{}' was not found.", conditionId, policy.getId());
  }
}
