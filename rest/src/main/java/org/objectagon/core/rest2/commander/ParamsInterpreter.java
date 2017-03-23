package org.objectagon.core.rest2.commander;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Created by christian on 2017-03-21.
 */
public interface ParamsInterpreter {
    enum ParamName {
        token("t"),
        alias("a"),
        url(),
        user("u"),
        password("p");

        private String[] shortParam;

        ParamName(String... shortParam) {
            this.shortParam = shortParam;
        }
    }

    enum CommandName {
        none(),
        login(ParamName.user, ParamName.password),
        create;

        private ParamName[] requiredParams;

        CommandName(ParamName... requiredParams) {
            this.requiredParams = requiredParams;
        }

        public Stream<ParamName> requiredParams() { return Arrays.asList(requiredParams).stream();}
    }

    void interpret(String[] params, Consumer<NameValue> nameValue, Consumer<CommandName> command);

    interface NameValue {
        ParamName getParamName();
        String getValue();
    }

    default Stream<ParamName> paramNamesAsStream() { return Arrays.asList(ParamsInterpreter.ParamName.values()).stream(); }

    default Stream<CommandName> commandNamesAsStream() { return Arrays.asList(ParamsInterpreter.CommandName.values()).stream(); }
}
