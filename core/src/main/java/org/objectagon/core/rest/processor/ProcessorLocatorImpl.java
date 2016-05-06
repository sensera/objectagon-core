package org.objectagon.core.rest.processor;

import org.objectagon.core.rest.ProcessorLocator;
import org.objectagon.core.rest.RestProcessor;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by christian on 2016-05-05.
 */
public class ProcessorLocatorImpl implements ProcessorLocator {

    @Override
    public LocatorBuilder locatorBuilder() {
        return new LocalLocatorBuilder();
    }

    private class LocalLocatorBuilder implements LocatorBuilder {
        List<LocalPatternBuilder> patternBulders = new ArrayList<>();

        @Override
        public PatternBuilder patternBuilder(RestProcessor restProcessor) {
            LocalPatternBuilder localPatternBuilder = new LocalPatternBuilder(restProcessor);
            patternBulders.add(localPatternBuilder);
            return localPatternBuilder;
        }

        @Override
        public Locator build() {
            TreeItem treeItem = new TreeItem();
            patternBulders.stream().forEach(localPatternBulder -> treeItem.build(localPatternBulder.patterns.iterator(), localPatternBulder.restProcessor, localPatternBulder.operation));
            return new LocalLocator(treeItem);
        }
    }

    private class LocalLocator implements Locator {

        private TreeItem treeItem;

        public LocalLocator(TreeItem treeItem) {
            this.treeItem = treeItem;
        }

        @Override
        public Optional<RestProcessor> match(Iterator<String> path, RestProcessor.Operation operation) {
            return treeItem.match(path, operation);
        }
    }

    private class LocalPatternBuilder implements PatternBuilder {
        private RestProcessor restProcessor;
        private List<Consumer<PatternBuilder>> patterns = new ArrayList<>();
        private RestProcessor.Operation operation;

        public LocalPatternBuilder(RestProcessor restProcessor) {
            this.restProcessor = restProcessor;
        }

        @Override
        public PatternBuilder setOperation(RestProcessor.Operation operation) {
            this.operation = operation;
            return this;
        }

        @Override
        public PatternBuilder add(String staticName) {
            patterns.add( (s) -> s.add(staticName));
            return this;
        }

        @Override
        public PatternBuilder addName() {
            patterns.add(PatternBuilder::addName);
            return this;
        }

        @Override
        public PatternBuilder addIdentity() {
            patterns.add(PatternBuilder::addIdentity);
            return this;
        }
    }

    private class TreeItem {
        Map<String, TreeItem> nextTreeItem = new HashMap<>();
        Optional<TreeItem> name = Optional.empty();
        Optional<TreeItem> identity = Optional.empty();
        Map<RestProcessor.Operation, RestProcessor> restProcessorByOperation = new HashMap<>();

        void build(Iterator<Consumer<PatternBuilder>> buildTreeIterator, RestProcessor restProcessor, RestProcessor.Operation operation) {
            if (!buildTreeIterator.hasNext()) {
                if (restProcessorByOperation.containsKey(operation))
                    throw new RuntimeException("Patterm used!");
                restProcessorByOperation.put(operation, restProcessor);
                return;
            }
            Consumer<PatternBuilder> next = buildTreeIterator.next();
            next.accept(new PatternBuilder() {
                @Override
                public PatternBuilder setOperation(RestProcessor.Operation operation) {
                    return null;
                }

                @Override
                public PatternBuilder add(String staticName) {
                    TreeItem treeItem = nextTreeItem.get(staticName);
                    if (treeItem==null) {
                        treeItem = new TreeItem();
                        nextTreeItem.put(staticName, treeItem);
                    }
                    treeItem.build(buildTreeIterator, restProcessor, operation);
                    return null;
                }

                @Override
                public PatternBuilder addName() {
                    if (!name.isPresent())
                        name = Optional.of(new TreeItem());
                    name.ifPresent(treeItem -> treeItem.build(buildTreeIterator, restProcessor, operation));
                    return null;
                }

                @Override
                public PatternBuilder addIdentity() {
                    if (!identity.isPresent())
                        identity = Optional.of(new TreeItem());
                    identity.ifPresent(treeItem -> treeItem.build(buildTreeIterator, restProcessor, operation));
                    return null;
                }
            });
        }

        Optional<RestProcessor> match(Iterator<String> path, RestProcessor.Operation operation) {
            if (!path.hasNext())
                return Optional.ofNullable(restProcessorByOperation.get(operation));
            String value = path.next();
            TreeItem treeItem = nextTreeItem.get(value);
            if (treeItem==null) {
                if (value.contains("-"))
                    treeItem = identity.orElse(null);
                else
                    treeItem = name.orElse(null);
            }
            if (treeItem==null)
                throw new RuntimeException("Cannot find '"+value+"'");
            return treeItem.match(path,operation);
        }
    }

}
