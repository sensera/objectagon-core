package org.objectagon.core.rest2.commander;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by christian on 2017-03-21.
 */
public interface CommandInterpreter {

    String OBJECTAGON_REST_TOKEN = "objectagon_rest_token";
    String OBJECTAGON_REST_URL = "objectagon_rest_url";

    InterpretedCommand interpret(Params params);

    @FunctionalInterface
    interface InterpretedCommand {
        InterpreterResponse exec(RestCommander restCommander);
    }

    interface Params {
        ParamsInterpreter.CommandName getCommandName();
        Optional<String> token();
        Optional<String> alias();
        Optional<String> url();
        Optional<String> getParam(ParamsInterpreter.ParamName paramName);
        VerifyParams require();
    }

    @FunctionalInterface
    interface VerifyParams {
        void verify(Consumer<Params> params, Consumer<List<ParamsInterpreter.ParamName>> missingParams);
    }

    @FunctionalInterface
    interface InterpreterResponse {
        void response(RestCommander.ResponsePrinter responsePrinter);
    }



}
