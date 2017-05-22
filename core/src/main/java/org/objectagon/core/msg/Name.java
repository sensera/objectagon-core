package org.objectagon.core.msg;

import java.util.function.Function;

/**
 * Created by christian on 2015-10-08.
 */
public interface Name {

    interface Named<N extends Name> {
        N getName();
    }

    interface GetName<T, N extends Name> {
        Named<N> getNameFrom(T target);
    }

    interface Interpret {
        void setText(String interpretedName);
        void setDomain(String domainName);
    }

    default void interpret(Interpret interpret) {
        interpret.setText(toString());
    }

    static String getNameAsString(Name name) {
        StringBuilder buildResponse = new StringBuilder();
        name.interpret(new Interpret() {
            @Override public void setText(String interpretedName) {buildResponse.append(interpretedName);}
            @Override public void setDomain(String domainName) { /*Ignored*/ }
        });
        return buildResponse.toString();
    }

    default <N extends Name> N to(Function<String,N> trans) {
        return trans.apply(toString());
    }

}
