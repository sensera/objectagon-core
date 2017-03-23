package org.objectagon.core.rest2.commander;

import org.objectagon.core.utils.WaitLock;

import java.util.concurrent.TimeoutException;

/**
 * Created by christian on 2017-03-22.
 */
class ResponsePrinterImpl implements RestCommander.ResponsePrinter {
    WaitLock waitLock = WaitLock.create();

    @Override
    public void printToken(String token) {
        System.out.println("export TOKEN=\"" + token + "\"");
    }

    @Override
    public void printResponse(String response) {
        System.out.println("------------------ InterpreterResponse ----------------");
        System.out.println(response);
        System.out.println("--------------------------------------------");
    }

    @Override
    public void printError(String errorMessage) {
        System.out.println("¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤ ERROR ¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤");
        System.out.println(errorMessage);
    }

    @Override
    public void printStackTrace(Exception exception) {
        exception.printStackTrace();
    }

    @Override
    public void printHelp() {
        System.out.println("????????????????????? USAGE ??????????????????");
        System.out.println("Usage: ");
        System.out.println("  cmd login");
    }

    public void completed() {
        waitLock.completed();
    }

    @Override
    public void printCommandName(ParamsInterpreter.CommandName commandName) {
        System.out.println("Command: "+commandName);
    }

    @Override
    public void printSuccess() {
        System.out.println("Success");
    }

    @Override
    public void printFailed() {
        System.out.println("Failed");
    }

    @Override
    public void printParam(ParamsInterpreter.ParamName param) {
        System.out.println(param.name());
    }

    public void waitForResponse() {
        try {
            waitLock.waitUntilCompleted();
        } catch (TimeoutException e) {
            System.out.println("Timeout waiting for exec!");
        }
    }
}
