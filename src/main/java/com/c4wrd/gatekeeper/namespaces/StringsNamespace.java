package com.c4wrd.gatekeeper.namespaces;

public class StringsNamespace {
    public boolean equal(String var1, String var2) {
        return var1 != null && var1.equals(var2);
    }

    public boolean equalIgnoreCase(String var1, String var2) {
        return var1 != null && var1.equalsIgnoreCase(var2);
    }

    public boolean oneOf(String var1, String... options) {
        if ( var1 == null || options == null || options.length < 1 ) {
            return false;
        }

        for ( String option : options ) {
            if ( option.equals(var1) ) {
                return true;
            }
        }

        return false;
    }
}
