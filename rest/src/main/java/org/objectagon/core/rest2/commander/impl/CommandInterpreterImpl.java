package org.objectagon.core.rest2.commander.impl;

import org.objectagon.core.rest2.commander.CommandInterpreter;
import org.objectagon.core.rest2.commander.ParamsInterpreter;
import org.objectagon.core.rest2.commander.RestCommander;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by christian on 2017-03-21.
 */
public class CommandInterpreterImpl implements CommandInterpreter {

    private static InterpretedCommand NONE = (restCommander -> responsePrinter -> {
        responsePrinter.printError("Missing command");
        responsePrinter.printHelp();
        responsePrinter.completed();
    });

    private static InterpretedCommand unimplementedCommand(ParamsInterpreter.CommandName commandName) {
        return (restCommander -> responsePrinter -> {
            responsePrinter.printCommandName(commandName);
            responsePrinter.printError("Unimplemented command");
            responsePrinter.printHelp();
            responsePrinter.completed();
        });
    }

    @Override
    public InterpretedCommand interpret(Params params) {
        switch (params.getCommandName()) {
            case none: return NONE;
            case login: return new LoginInterpretedCommand(params);
            default: return unimplementedCommand(params.getCommandName());
        }
    }

    private static abstract class ParamsInterpretedCommand implements InterpretedCommand {
        Params params;

        public ParamsInterpretedCommand(Params params) {
            this.params = params;
        }

        @Override
        public final InterpreterResponse exec(RestCommander restCommander) {
            return responsePrinter -> {
                params.require()
                        .verify(paramNames -> {
                            internalCommand(restCommander, responsePrinter);
                        },missingParams -> {
                            responsePrinter.printError("Missing required params:");
                            missingParams.stream().forEach(responsePrinter::printParam);
                            responsePrinter.completed();
                        });
            };
        }

        protected abstract void internalCommand(RestCommander restCommander, RestCommander.ResponsePrinter responsePrinter);
    }

    private static class LoginInterpretedCommand extends ParamsInterpretedCommand{
        public LoginInterpretedCommand(Params params) {
            super(params);
        }

        @Override
        protected void internalCommand(RestCommander restCommander, RestCommander.ResponsePrinter responsePrinter) {
            final String user = params.getParam(ParamsInterpreter.ParamName.user).get();
            final String pwd = params.getParam(ParamsInterpreter.ParamName.password).get();
            restCommander.login(user, pwd).exec(
                    params,
                    createRestHttpRequest(),
                    result -> {
                        responsePrinter.printCommandName(ParamsInterpreter.CommandName.login);
                        if (result.success()) {
                            responsePrinter.printSuccess();
                        } else {
                            responsePrinter.printFailed();
                        }
                    },
                    standardFail(ParamsInterpreter.CommandName.login, responsePrinter));
        }
    }

    private static RestCommander.RestHttpCmd createRestHttpRequest() {
        return (method, urlText, params, success, fail) -> {
            try {
                final HttpURLConnection urlConnection = (HttpURLConnection) new URL(params.url().get() + urlText).openConnection();
                urlConnection.setRequestMethod(method);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                //urlConnection.setRequestProperty("Content-Length", "" + postData.getBytes().length);
                //urlConnection.setRequestProperty("Content-Language", "en-US");
                params.token().ifPresent(token -> urlConnection.setRequestProperty(CommandInterpreter.OBJECTAGON_REST_TOKEN, token));


            } catch (IOException e) {
                e.printStackTrace();
                fail.responseFail(0, e.getLocalizedMessage());
            }

            success.responseSuccess("ok");
        };
    }

    private static RestCommander.Fail standardFail(ParamsInterpreter.CommandName commandName, RestCommander.ResponsePrinter responsePrinter) {
        return (cause, displayUsageMessage) -> {
            responsePrinter.printCommandName(commandName);
            responsePrinter.printError(cause);
            if (displayUsageMessage) {
                responsePrinter.printHelp();
            }
            responsePrinter.completed();
        };
    }
}
