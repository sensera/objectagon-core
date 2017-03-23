package org.objectagon.core.rest2.commander;

import org.objectagon.core.rest2.commander.impl.CommandInterpreterImpl;
import org.objectagon.core.rest2.commander.impl.ParamsInterpreterImpl;
import org.objectagon.core.rest2.commander.impl.RestCommanderImpl;

/**
 * Created by christian on 2017-03-21.
 */
public class Cmd {

    static CommandInterpreter commandInterpreter = new CommandInterpreterImpl();
    static RestCommander restCommander = new RestCommanderImpl();
    static ParamsInterpreter paramsInterpreter = new ParamsInterpreterImpl();

    public static void main(String[] params) {
        CommandParams commandParams = new CommandParams();

        paramsInterpreter.interpret(
                params,
                commandParams::setNameValue,
                commandParams::setCommandName);

        commandParams.updateMissingParamsFromEnvironment();

        final ResponsePrinterImpl responsePrinterImpl = new ResponsePrinterImpl();

        commandInterpreter.interpret(commandParams)
                .exec(restCommander)
                .response(responsePrinterImpl);

        responsePrinterImpl.waitForResponse();
    }

}
