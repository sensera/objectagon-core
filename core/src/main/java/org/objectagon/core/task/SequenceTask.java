package org.objectagon.core.task;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Protocol;
import org.objectagon.core.msg.Receiver;
import org.objectagon.core.msg.address.StandardAddress;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.message.NamedField;
import org.objectagon.core.utils.FindNamedConfiguration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by christian on 2015-11-08.
 */
public class SequenceTask extends AbstractTask {

    private List<Task> sequence = new LinkedList<>();
    private int atSequence = 0;
    private List<Message.Value> values = new ArrayList<>();

    public SequenceTask(Receiver.ReceiverCtrl taskCtrl, TaskName name) {
        super(taskCtrl, name);
    }

    @Override
    protected void internalStart() {
        startNextTask(Task.MessageName.COMPLETED, Message.NO_VALUES);
    }

    private Task activeTask() {
        try {
            return sequence.get(atSequence);
        } catch (IndexOutOfBoundsException e) {
            throw new SevereError(ErrorClass.TASK, ErrorKind.INCONSISTENCY, MessageValue.name(getName()), MessageValue.number(NamedField.number("AT_SEQUENCE"), (long) atSequence));
        }
    }

    public SequenceTask add(Task task) {
        sequence.add(task);
        task.addSuccessAction(this::startNextTask);
        task.addFailedAction(this::failed);
        return this;
    }

    public <S extends Protocol.Send>SequenceTask addSend(TaskName taskName, Protocol.ProtocolName protocolName, Address target, ProtocolTask.SendMessageAction<S> sendMessageAction) {
        add(new ProtocolTask(
                getReceiverCtrl(),
                taskName,
                protocolName,
                target,
                sendMessageAction
        ));
        return this;
    }

    private void startNextTask(Message.MessageName messageName, Iterable<Message.Value> values) {
        if (sequence.size()==atSequence) {
            success(Task.MessageName.COMPLETED, values);
            return;
        }
        Task task = activeTask();
        atSequence++;
        task.start();
    }
    @Override
    protected void handle(TaskWorker worker) {

    }



    @Override
    protected Address createAddress(Configurations... configurations) {
        return FindNamedConfiguration.finder(configurations).createConfiguredAddress(StandardAddress::standard);
    }

    public void add(Message.Value value) { values.add(value); }
}
