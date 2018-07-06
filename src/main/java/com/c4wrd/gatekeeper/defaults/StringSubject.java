package com.c4wrd.gatekeeper.defaults;

import com.c4wrd.gatekeeper.api.Subject;
import lombok.AllArgsConstructor;

/**
 * Simple subject that is only identified by a string name
 */
@AllArgsConstructor
public class StringSubject implements Subject {
    private String id;
    @Override
    public String getId() {
        return id;
    }
}
