package org.objectagon.core.rest.processor;

import org.objectagon.core.rest.ProcessorLocator;
import org.objectagon.core.rest.ServerCore;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.storage.TransactionManagerProtocol;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2016-05-03.
 */
public class TransactionRestProcessor extends AbstractRestProcessor {

    public static void attachToLocator(ProcessorLocator.LocatorBuilder locatorBuilder) {
        locatorBuilder.patternBuilder(
                new TransactionRestProcessor(true, (send, testUser, request, response) -> send.commit())
        ).add("transaction").add("commit").setOperation(Operation.UpdateExecute);
        locatorBuilder.patternBuilder(
                new TransactionRestProcessor(true,  (send, testUser, request, response) -> send.rollback())
        ).add("transaction").add("rollback").setOperation(Operation.UpdateExecute);
        locatorBuilder.patternBuilder(
                new TransactionRestProcessor(true,  (send, testUser, request, response) -> send.commit())
        ).add("transaction").addIdentity("transactionId").add("commit").setOperation(Operation.UpdateExecute);
        locatorBuilder.patternBuilder(
                new TransactionRestProcessor(true,  (send, testUser, request, response) -> send.rollback())
        ).add("transaction").addIdentity("transactionId").add("rollback").setOperation(Operation.UpdateExecute);
    }

    boolean transaction;
    TransactionRestProcesssorTaskCreator transactionRestProcesssorTaskCreator;

    public TransactionRestProcessor(boolean transaction, TransactionRestProcesssorTaskCreator transactionRestProcesssorTaskCreator) {
        this.transaction = transaction;
        this.transactionRestProcesssorTaskCreator = transactionRestProcesssorTaskCreator;
    }

    private TransactionManagerProtocol.Send getTransactionManagerProtocol(ServerCore.TestUser testUser, Request request) {
        if (!transaction)
            return testUser.createTransactionManagerProtocol();
        Transaction transaction = request.getValue(Transaction.TRANSACTION).address();
        return testUser.createTransactionManagerProtocol(transaction);
    }

    @Override
    Task createActionTask(ServerCore.TestUser testUser, Request request, Response response) {
        return transactionRestProcesssorTaskCreator.consume(getTransactionManagerProtocol(testUser, request), testUser, request, response)
                .addSuccessAction(response::reply);
    }

    interface TransactionRestProcesssorTaskCreator {
        Task consume(TransactionManagerProtocol.Send send, ServerCore.TestUser testUser, Request request, Response response);
    }

}



