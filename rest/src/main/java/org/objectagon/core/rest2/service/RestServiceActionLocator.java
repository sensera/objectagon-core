package org.objectagon.core.rest2.service;

import org.objectagon.core.msg.Address;
import org.objectagon.core.msg.Message;
import org.objectagon.core.msg.Name;
import org.objectagon.core.rest2.service.locator.RestPathPatternImpl;
import org.objectagon.core.service.Service;
import org.objectagon.core.storage.Identity;
import org.objectagon.core.task.Task;
import org.objectagon.core.task.TaskBuilder;
import org.objectagon.core.utils.KeyValue;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Created by christian on 2017-01-25.
 */
public interface RestServiceActionLocator {
    Predicate<RestActionMatchResult> ONLY_VALID_RESULTS = restActionMatchResult -> !restActionMatchResult.getRestMatchRating().equals(RestMatchRating.None);

    RestAction locate(Address protocolSessionId, RestServiceProtocol.Method method, RestPath path) throws Exception;

    void configure(ServiceLocator serviceLocator);

    @FunctionalInterface
    interface RestAction {
        Task createTask(TaskBuilder taskBuilder, RestPath restPath, List<KeyValue<ParamName, Message.Value>> params, String data);
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
    }

    interface IdentityLookup {
        Optional<Identity> find(String identityAlias);
    }

    interface RestPathValue {
        int getIndex();
        RestServiceActionLocator.RestMatchRating isText(RestPathPatternImpl.MatchPatternDetails matchPatternDetails);
        RestServiceActionLocator.RestMatchRating isId();
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

