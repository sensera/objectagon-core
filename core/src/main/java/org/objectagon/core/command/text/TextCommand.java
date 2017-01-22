package org.objectagon.core.command.text;

import org.objectagon.core.command.Command;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by christian on 2017-01-15.
 */
public class TextCommand {

    Command command;
    List<Cmd> commands = new ArrayList<>();

    public TextCommand(Command command) {
        this.command = command;
        registerCommands();
    }

    private void registerCommands() {
        commands.add(new LoadPackageCommand());
    }

    interface Cmd {
        EvaluationResult evaluate(CommandContext commandContext);
        void exec(CommandContext commandContext);
    }

    interface CommandContext {
        boolean commandEquals(String textCmd);
    }

    class LoadPackageCommand implements Cmd {

        @Override
        public EvaluationResult evaluate(CommandContext commandContext) {
            if (commandContext.commandEquals("Load"))
                return EvaluationResult.EXACT;
            return EvaluationResult.NOT;
        }

        @Override
        public void exec(CommandContext commandContext) {

        }

    }

    class StartServiceCommand implements Cmd {

        @Override
        public EvaluationResult evaluate(CommandContext commandContext) {
            if (commandContext.commandEquals("Load"))
                return EvaluationResult.EXACT;
            return EvaluationResult.NOT;
        }

        @Override
        public void exec(CommandContext commandContext) {

        }

    }
}
