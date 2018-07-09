package com.c4wrd.gatekeeper.namespaces;

public class StringsNamespace {
    public boolean equal(Object var1, Object var2 ){
        return var1 != null && var1.toString().equals(var2.toString());
    }

    public boolean equalIgnoreCase(Object var1, Object var2) {
        return var1 != null && var1.toString().equalsIgnoreCase(var2.toString());
    }

    public boolean oneOf(Object var1, Object... options) {
        if ( var1 == null || options == null || options.length < 1 ) {
            return false;
        }

        for ( Object option : options ) {
            if ( option.toString().equals(var1.toString()) ) {
                return true;
            }
        }

        return false;
    }
}
