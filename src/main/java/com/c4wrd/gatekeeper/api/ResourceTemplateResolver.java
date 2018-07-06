package com.c4wrd.gatekeeper.api;

public interface ResourceTemplateResolver {
    /**
     * Resolves all of the resource template variables (should they exist)
     * in a given resource string for a given access request. For example, by using
     * the default StringSubstitutorTemplateResolver implementation, the string
     * "account:${ctx:id}", given context contains the variable "id" with value "cory",
     * would resolve to "account:cory"
     * @param resourceTemplate The template to provide templating for
     * @param accessRequest The access request
     * @return
     */
    String resolveResourceTemplate(String resourceTemplate, AccessRequest accessRequest);
}
