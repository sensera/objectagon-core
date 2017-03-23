package org.objectagon.core.rest2.service;

import org.objectagon.core.exception.UserException;
import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.rest2.service.locator.RestPathPatternImpl;
import org.objectagon.core.service.Service;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.storage.Transaction;
import org.objectagon.core.task.Task;
import org.objectagon.core.task.TaskBuilder;
import org.objectagon.core.utils.KeyValue;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Created by christian on 2017-01-25.
 */
public interface RestServiceActionLocator {
    Predicate<RestActionMatchResult> ONLY_VALID_RESULTS = restActionMatchResult -> !restActionMatchResult.getRestMatchRating().equals(RestMatchRating.None);

    enum TaskName implements Task.TaskName { Login }

    RestAction locate(Address protocolSessionId, RestServiceProtocol.Method method, RestPath path) throws UserException;

    void configure(ServiceLocator serviceLocator);

    @FunctionalInterface
    interface RestAction {
        Task createTask(TaskBuilder taskBuilder, IdentityStore identityStore, RestPath restPath, List<KeyValue<ParamName, Message.Value>> params, String data);
    }

    interface RestActionKey {
        RestActionMatchResult check(RestServiceProtocol.Method method, RestPath path);
    }

    interface RestActionMatchResult {
        RestMatchRating getRestMatchRating();
        RestAction getAction();
    }

    interface RestPath {
        Stream<RestPathValue> values();
        Message.Value asValue();
        Message.Value valueAtIndex(int index);
        int size();
    }

    interface IdentityStore {
        void updateIdentity(Identity identity, String identityAlias);
        void updateSessionTransaction(Transaction transaction);
        Optional<Transaction> getActiveTransaction();
    }

    interface RestPathValue {
        int getIndex();
        RestServiceActionLocator.RestMatchRating isText(RestPathPatternImpl.MatchPatternDetails matchPatternDetails);
        RestServiceActionLocator.RestMatchRating isId(RestPathPatternImpl.MatchPatternDetails matchPatternDetails);
        <E extends Identity> Optional<E> getIdentity();
    }

    interface RestKeyCheck {
        RestMatchRating check(RestServiceProtocol.Method method, RestPath restPath);

    }

    interface RestPathPattern {
        RestMatchRating check(RestPath restPath);

        default RestMatchRating max(RestMatchRating... list) {
            return Arrays.stream(list).max(Comparator.naturalOrder()).orElse(RestMatchRating.Perfect);
        }

        default RestMatchRating min(RestMatchRating... list) {
            return Arrays.stream(list).min(Comparator.naturalOrder()).orElse(RestMatchRating.None);
        }
    }

    interface RestPathPatternBuilder  {
        RestPathPatternBuilder text(String text);
        RestPathPatternBuilder id();
        RestPathPatternBuilder name();
        RestPathPattern build();
    }

    interface ServiceLocator {
        Service.ServiceName getService(Name name);
    }

    interface CreateServiceLocator {
        CreateServiceLocator add(Name key, Service.ServiceName service);
        ServiceLocator create();
    }

    interface RestSession {
        RestSessionToken geRestSessionToken();
        void updateTransaction(Transaction transaction);
        void updateAlias(Identity identity, String alias);
        Optional<Identity> getIdentityByAlias(String alias);
        Optional<Transaction> getActiveTransaction();
        void useActiveTransaction(Consumer<Transaction> consumer);

    }

    enum RestMatchRating {
        Perfect,
        Ok,
        Vague,
        None;

        public RestMatchRating max(RestMatchRating... list) {
            return Arrays.stream(list).max(Comparator.naturalOrder()).orElse(this);
        }

        public RestMatchRating min(RestMatchRating... list) {
            return Arrays.stream(list).min(Comparator.naturalOrder()).orElse(this);
        }

    }

    @FunctionalInterface
    interface Create {
        RestServiceActionLocator create();
    }

}

