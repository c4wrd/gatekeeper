package com.c4wrd.gatekeeper.api;

import java.util.Map;

public interface NamespaceProvider {
    Map<String, Object> provide();
}
