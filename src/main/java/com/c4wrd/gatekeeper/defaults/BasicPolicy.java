package com.c4wrd.gatekeeper.defaults;

import com.c4wrd.gatekeeper.api.Effect;
import com.c4wrd.gatekeeper.api.Policy;
import lombok.Builder;
import lombok.Singular;

import java.util.List;
import java.util.Map;

@Builder
public class BasicPolicy implements Policy {

    private Effect effect;
    private String id;
    private String description;
    @Singular
    private List<String> actions;
    @Singular
    private List<String> resources;
    @Singular
    private Map<String, Map<String, Object>> conditions;

    @Override
    public Effect getEffect() {
        return effect;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<String> getActions() {
        return actions;
    }

    @Override
    public List<String> getResources() {
        return resources;
    }

    @Override
    public Map<String, Map<String, Object>> getConditionsAndArgs() {
        return conditions;
    }
}
