package org.objectagon.core.rest2.commander;

/**
 * Created by christian on 2017-03-21.
 */
public interface RestCommander {

    CommandExec<LoginResult> login(String userName, String password);
    CommandExec createTransaction();
    CommandExec createInstanceClass(String alias);

    InstanceClass buildInstanceClassCommand(String alias);

    interface Configuration {
        String url();
        String token();
    }

    @FunctionalInterface
    interface Success<R extends Result> {
        void success(R result);
    }

    @FunctionalInterface
    interface Fail {
        void fail(String cause, boolean displayUsageMessage);
    }

    @FunctionalInterface
    interface CommandExec<R extends Result> {
        void exec(CommandInterpreter.Params params, RestHttpCmd restHttpCmd, Success<R> success, Fail fail);
    }

    interface ResponsePrinter {
        void printToken(String token);
        void printResponse(String response);
        void printError(String errorMessage);
        void printStackTrace(Exception exception);
        void printHelp();
        void completed();
        void printCommandName(ParamsInterpreter.CommandName commandName);
        void printSuccess();
        void printFailed();
        void printParam(ParamsInterpreter.ParamName param);
    }

    interface InstanceClass {
        CommandExec setName(String name);
        CommandExec addField(String name);
        CommandExec addRelation(String name, String target);
    }

    interface Result {
        String token();
    }

    interface LoginResult extends Result {
        boolean success();
    }

    interface RestHttpCmd {
        void call(String method, String url, CommandInterpreter.Params params, RestHttpCmdSuccess success, RestHttpCmdFail fail);
    }

    interface RestHttpCmdSuccess {
        void responseSuccess(String json);
    }

    interface RestHttpCmdFail {
        void responseFail(int errorCode, String errorText);
    }

}
