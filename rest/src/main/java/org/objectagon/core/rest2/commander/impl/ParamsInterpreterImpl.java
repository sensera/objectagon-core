package org.objectagon.core.rest2.commander.impl;

import org.objectagon.core.rest2.commander.ParamsInterpreter;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by christian on 2017-03-21.
 */
public class ParamsInterpreterImpl implements ParamsInterpreter {

    @Override
    public void interpret(String[] params, Consumer<NameValue> nameValue, Consumer<CommandName> command) {
        Arrays.asList(params).stream()
                .forEach(interpretParam(nameValue, command));
    }

    private Consumer<? super String> interpretParam(Consumer<NameValue> nameValue, Consumer<CommandName> command) {
        return param -> {
            int indexOfEquals = param.indexOf("=");
            if (indexOfEquals==-1) {
                setCommand(command, param);
            } else {
                setNameValue(nameValue, param);
            }
        };
    }

    private void setCommand(Consumer<CommandName> command, String param) {
        commandNamesAsStream()
                .filter(commandName -> commandName.name().equalsIgnoreCase(param))
                .forEach(command);
    }

    private void setNameValue(Consumer<NameValue> nameValue, String param) {
        String[] splittedParam = param.split("=");
        if (splittedParam.length!=2)
            throw new RuntimeException("Too many equal signs +"+param+"\"");
        paramNamesAsStream()
                .filter(paramName -> paramName.name().equalsIgnoreCase(splittedParam[0]))
                .map(createParamNameValueFunction(splittedParam[1]))
                .forEach(nameValue);
    }

    private Function<ParamName, NameValue> createParamNameValueFunction(final String value) {
        return paramName -> new NameValue() {
            @Override public ParamName getParamName() { return paramName;}
            @Override public String getValue() {return value;}
        };
    }
}
