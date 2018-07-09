package com.c4wrd.gatekeeper.api;

import java.util.List;

public interface GatekeeperContext {
  Effect enforce(AccessRequest request);

  List<Policy> getPermittingPolicies(AccessRequest request);
}
