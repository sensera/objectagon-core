package org.objectagon.core.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by christian on 2017-04-15.
 */
public class AsyncImpl implements Async {
    @Override
    public <E extends Target> AsyncList<E> createAsyncList() {
        return new LocalAsyncList<E>();
    }

    private class LocalAsyncList<E extends Target> implements AsyncList<E> {
        private List<E> targets = new ArrayList<>();
        private boolean done = false;
        private List<WhenDone> whenDones = new ArrayList<>();

        public LocalAsyncList() {
        }

        @Override
        public void add(E target) {
            synchronized (whenDones) {
                if (done) {
                    throw new RuntimeException("Already done!");
                }
            }
            synchronized (targets) {
                targets.add(target);
                target.<E>whenDone(() -> remove(target));
            }
        }

        @Override
        public void remove(E target) {
            boolean empty;
            synchronized (targets) {
                targets.remove(target);
                empty = targets.isEmpty();
            }
            if (empty)
                notifyWhenDones();
        }

        private void notifyWhenDones() {
            synchronized (whenDones) {
                if (!done) {
                    return;
                }
                whenDones.stream()
                        .forEach(WhenDone::done);
            }
        }

        public void done() {
            synchronized (whenDones) {
                if (done) {
                    throw new RuntimeException("Already done!");
                }
                done = true;
                notifyWhenDones();
            }
        }

        @Override
        public void whenDone(WhenDone whenDone) {
            synchronized (whenDones) {
                whenDones.add(whenDone);
            }
        }
    }
}
