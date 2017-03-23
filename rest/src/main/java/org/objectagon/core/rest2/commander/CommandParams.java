package org.objectagon.core.rest2.commander;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by christian on 2017-03-22.
 */
class CommandParams implements CommandInterpreter.Params {
    private Map<ParamsInterpreter.ParamName, String> params = new HashMap<>();

    private ParamsInterpreter.CommandName commandName = ParamsInterpreter.CommandName.none;

    @Override
    public Optional<String> getParam(ParamsInterpreter.ParamName paramName) {
        return Optional.ofNullable(params.get(paramName));
    }

    @Override
    public CommandInterpreter.VerifyParams require() {
        final List<ParamsInterpreter.ParamName> missingParamsList = commandName.requiredParams()
                .filter(paramName -> !params.containsKey(paramName))
                .collect(Collectors.toList());
        if (missingParamsList.isEmpty()) {
            return (params1, missingParams) -> params1.accept(this);
        }
        return (params1, missingParams) -> missingParams.accept(missingParamsList);
    }

    void setNameValue(ParamsInterpreter.NameValue nameValue) {
        params.put(nameValue.getParamName(), nameValue.getValue());
    }

    void updateMissingParamsFromEnvironment() {
        if (!token().isPresent())
            params.put(ParamsInterpreter.ParamName.token, System.getenv(CommandInterpreter.OBJECTAGON_REST_TOKEN));
        if (!url().isPresent())
            params.put(ParamsInterpreter.ParamName.url, System.getenv(CommandInterpreter.OBJECTAGON_REST_URL));
    }

    void setCommandName(ParamsInterpreter.CommandName commandName) {
        this.commandName = commandName;
    }

    @Override
    public ParamsInterpreter.CommandName getCommandName() {
        return commandName;
    }

    @Override
    public Optional<String> token() {
        return getParam(ParamsInterpreter.ParamName.token);
    }

    @Override
    public Optional<String> url() {
        return getParam(ParamsInterpreter.ParamName.url);
    }

    @Override
    public Optional<String> alias() {
        return getParam(ParamsInterpreter.ParamName.alias);
    }
}
