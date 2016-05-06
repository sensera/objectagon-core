package org.objectagon.core.rest.processor;

import org.objectagon.core.rest.ProcessorLocator;
import org.objectagon.core.rest.ServerCore;
import org.objectagon.core.storage.EntityServiceProtocol;
import org.objectagon.core.storage.TransactionManagerProtocol;
import org.objectagon.core.task.Task;

/**
 * Created by christian on 2016-05-03.
 */
public class TransactionRestProcessor extends AbstractRestProcessor {

    public static void attachToLocator(ProcessorLocator.LocatorBuilder locatorBuilder) {
/*
        locatorBuilder.patternBuilder(
                new TransactionRestProcessor( ((send, testUser, request, response) -> send.create(request.queryAsValues())))
        ).add("transaction").setOperation(Operation.SaveNew);
*/
    }

    TransactionRestProcesssorTaskCreator transactionRestProcesssorTaskCreator;

    public TransactionRestProcessor(TransactionRestProcesssorTaskCreator transactionRestProcesssorTaskCreator) {
        this.transactionRestProcesssorTaskCreator = transactionRestProcesssorTaskCreator;
    }

    private EntityServiceProtocol.Send getEntityServiceProtocol(ServerCore.TestUser testUser) {
        throw new RuntimeException("Not implemented!");
/*
        testUser.createTransaction()
        switch (entityType) {
            case Class: return testUser.createInstanceClassEntityServiceProtocol();
            default: throw new RuntimeException("Internal error");
        }
*/
    }

    @Override
    Task createActionTask(ServerCore.TestUser testUser, Request request, Response response) {
        throw new RuntimeException("Not implemented!");
/*
        return transactionRestProcesssorTaskCreator.consume(getEntityServiceProtocol(testUser), testUser, request, response)
                .addSuccessAction(response::reply);
*/
    }

    interface TransactionRestProcesssorTaskCreator {
        Task consume(TransactionManagerProtocol.Send send, ServerCore.TestUser testUser, Request request, Response response);
    }

}



