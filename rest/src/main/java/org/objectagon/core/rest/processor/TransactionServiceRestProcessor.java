package org.objectagon.core.rest.processor;

import org.objectagon.core.rest.ProcessorLocator;
import org.objectagon.core.rest.ServerCore;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2016-05-03.
 */
public class TransactionServiceRestProcessor extends AbstractRestProcessor {

    public static void attachToLocator(ProcessorLocator.LocatorBuilder locatorBuilder) {
        locatorBuilder.patternBuilder(
                new TransactionServiceRestProcessor( ((testUser, request, response) -> testUser.createTransactionTask()))
        ).add("transaction").setOperation(Operation.SaveNew);
    }

    TransactionRestProcesssorTaskCreator transactionRestProcesssorTaskCreator;

    public TransactionServiceRestProcessor(TransactionRestProcesssorTaskCreator transactionRestProcesssorTaskCreator) {
        this.transactionRestProcesssorTaskCreator = transactionRestProcesssorTaskCreator;
    }

    @Override
    Task createActionTask(ServerCore.TestUser testUser, Request request, Response response) {
        return transactionRestProcesssorTaskCreator.consume(testUser, request, response)
                .addSuccessAction(response::reply);
    }

    interface TransactionRestProcesssorTaskCreator {
        Task consume(ServerCore.TestUser testUser, Request request, Response response);
    }

}



