package org.objectagon.core.storage.search;

import org.objectagon.core.Server;
import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.exception.SevereError;
import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.msg.field.StandardField;
import org.objectagon.core.msg.message.MessageValue;
import org.objectagon.core.msg.receiver.Reactor;
import org.objectagon.core.msg.receiver.StandardAction;
import org.objectagon.core.service.*;
import org.objectagon.core.service.event.BroadcastEventServiceProtocol;
import org.objectagon.core.service.event.EventServiceImpl;
import org.objectagon.core.service.event.EventServiceProtocol;
import org.objectagon.core.service.name.NameServiceProtocol;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.SearchServiceProtocol;
import org.objectagon.core.storage.StorageServices;
import org.objectagon.core.task.Task;
import org.objectagon.core.task.TaskBuilder;
import org.objectagon.core.utils.FindNamedConfiguration;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by christian on 2016-04-09.
 */
public class SearchService extends AbstractService<SearchService.SearchServiceWorker, Service.ServiceName> {

    public static ServiceName NAME = StandardServiceName.name("SEARCH_SERVICE");
    private enum InternalTask implements Task.TaskName {
        RegisterName
    }

    public static void registerAt(Server server) {
        server.registerFactory(NAME, SearchService::new);
    }

    private Optional<Address> eventService = Optional.empty();
    private Map<Name, List<Identity>> identitiesByName = new HashMap<>();

    private Optional<List<Identity>> getIdentitiesByName(Name name) {
        return Optional.ofNullable(identitiesByName.get(name));
    }

    private List<Identity> createList(Name name) {
        List<Identity> list = new ArrayList<>();
        identitiesByName.put(name, list);
        return list;
    }

    private void updateNamesList(Name name, Identity identity) {
        getIdentitiesByName(name)
                .orElse(createList(name))
                .add(identity);
    }

    public SearchService(ReceiverCtrl receiverCtrl) {
        super(receiverCtrl);
        setServiceName(NAME);
    }

    @Override
    public void configure(Configurations... configurations) {
        super.configure(configurations);
    }

    @Override
    protected Optional<TaskBuilder> internalCreateStartServiceTask(SearchServiceWorker serviceWorker) {
        eventService = Optional.of(getReceiverCtrl().lookupAddressByAlias(EventServiceImpl.EVENT_SERVICE_NAME).orElseThrow(() -> new SevereError(ErrorClass.SEARCH_SERVICE, ErrorKind.MISSING_CONFIGURATION)));
        TaskBuilder taskBuilder = serviceWorker.getTaskBuilder();
        taskBuilder.<EventServiceProtocol.Send>message(
                InternalTask.RegisterName,
                NameServiceProtocol.NAME_SERVICE_PROTOCOL,
                eventService.get(),
                session -> session.startListenTo(getAddress(), StorageServices.SEARCH_NAME_EVENT));
        return Optional.of(taskBuilder);
    }

    @Override
    protected SearchServiceWorker createWorker(WorkerContext workerContext) {
        return new SearchServiceWorker(workerContext);
    }

    @Override
    protected ServiceName createAddress(Configurations... configurations) {
        return FindNamedConfiguration.finder(configurations).createConfiguredAddress((serverId, timestamp, addressId) -> StandardServiceNameAddress.name(NAME, serverId, timestamp, addressId));
    }

    public class SearchServiceWorker extends ServiceWorkerImpl {
        public SearchServiceWorker(WorkerContext workerContext) {
            super(workerContext);
        }
    }

    @Override
    protected void buildReactor(Reactor.ReactorBuilder reactorBuilder) {
        super.buildReactor(reactorBuilder);
        reactorBuilder.add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(SearchServiceProtocol.MessageName.NAME_SEARCH),
                (initializer, context) -> new NameSearchAction(this, (SearchServiceWorker) context)
        );
        reactorBuilder.add(
                patternBuilder -> patternBuilder.setMessageNameTrigger(BroadcastEventServiceProtocol.MessageName.BROADCAST),
                (initializer, context) -> new NameChangeEventAction(this, (SearchServiceWorker) context)
        );
    }

    private static class NameSearchAction extends StandardAction<SearchService, SearchServiceWorker> {

        private Name name;

        public NameSearchAction(SearchService initializer, SearchServiceWorker context) {
            super(initializer, context);
        }

        public boolean initialize() {
            name = context.getValue(StandardField.NAME).asName();
            return true;
        }

        @Override
        public Optional<Message.Value> internalRun() throws UserException {
            Optional<List<Identity>> searchResult = initializer.getIdentitiesByName(name);
            return searchResult.map(identities -> {
                List<Message.Value> values = identities.stream().map(identity -> MessageValue.address(Identity.IDENTITY, identity)).collect(Collectors.toList());
                return Optional.of(MessageValue.values(values));

            }).orElse(Optional.empty());
        }
    }

    private static class NameChangeEventAction extends StandardAction<SearchService, SearchServiceWorker> {

        private Name name;
        private Identity identity;

        public NameChangeEventAction(SearchService initializer, SearchServiceWorker context) {
            super(initializer, context);
        }

        public boolean initialize() {
            name = context.getValue(StandardField.NAME).asName();
            identity = context.getValue(Identity.IDENTITY).asAddress();
            return true;
        }

        @Override
        public Optional<Message.Value> internalRun() throws UserException {
            initializer.updateNamesList(name, identity);
            return Optional.empty();
        }
    }


}
