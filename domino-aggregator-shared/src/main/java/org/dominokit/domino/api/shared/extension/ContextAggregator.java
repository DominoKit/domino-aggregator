package org.dominokit.domino.api.shared.extension;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class ContextAggregator {

    private Set<ContextWait> contextsSet = new LinkedHashSet<>();
    private Set<ContextWait> removed = new LinkedHashSet<>();
    private ReadyHandler handler;

    private ContextAggregator(Set<ContextWait> contexts,
                              ReadyHandler handler) {
        this.handler = handler;
        contexts.forEach(this::setupContext);
    }

    /**
     * prepare a context and add it to the set of elements that needs to be completed before the aggregator onReady method is called.
     * @param contextWait
     */
    public void setupContext(ContextWait contextWait) {
        if (!this.contextsSet.contains(contextWait)) {
            this.contextsSet.add(contextWait);
            contextWait.onReady(() -> {
                this.contextsSet.remove(contextWait);
                this.removed.add(contextWait);
                contextWait.completed =true;
                if (this.contextsSet.isEmpty()) {
                    handler.onReady();
                }
            });
        }
    }

    /**
     * Will reset all context wait elements. all context wait elements will be considered no completed after this.
     */
    public void reset() {
        contextsSet.addAll(removed);
        removed.clear();
        contextsSet.forEach(contextWait -> contextWait.completed = false);
    }

    /**
     * same as reset() but for a specific context wait.
     * @param context
     */
    public void resetContext(ContextWait context) {
        if (removed.contains(context)) {
            contextsSet.add(context);
            removed.remove(context);
            context.completed = false;
        }
    }

    /**
     * a factory method to create a ContextAggregator with at least one context wait to wait for.
     * @param context
     * @return
     */
    public static CanWaitForContext waitFor(ContextWait context) {
        return ContextAggregatorBuilder.waitForContext(context);
    }

    /**
     * a factory method to create a ContextAggregator with an initial set of context wait to wait for.
     * @param context
     * @return
     */
    public static CanWaitForContext waitFor(ContextWait... context) {
        return ContextAggregatorBuilder.waitForContext(Arrays.asList(context));
    }

    /**
     * a factory method to create a ContextAggregator with an initial set of context wait to wait for.
     * @param contexts
     * @return
     */
    public static CanWaitForContext waitFor(Collection<? extends ContextWait> contexts) {
        return ContextAggregatorBuilder.waitForContext(contexts);
    }

    public interface CanWaitForContext {
        <T extends ContextWait> CanWaitForContext and(T context);

        ContextAggregator onReady(ReadyHandler handler);
    }

    @FunctionalInterface
    public interface ReadyHandler {
        void onReady();
    }

    private static class ContextAggregatorBuilder implements CanWaitForContext {

        private Set<ContextWait> contextSet = new LinkedHashSet<>();

        private ContextAggregatorBuilder(ContextWait context) {
            this.contextSet.add(context);
        }

        /**
         * a builder method to add a set of ContextWait to the ContextAggregator.
         * @param contexts
         */
        private ContextAggregatorBuilder(Collection<? extends ContextWait> contexts) {
            this.contextSet.addAll(contexts);
        }

        /**
         * a builder method create a ContextAggregatorBuilder with a single initial ContextWait.
         * @param context
         * @return CanWaitForContext
         */
        public static <T extends ContextWait> CanWaitForContext waitForContext(T context) {
            return new ContextAggregatorBuilder(context);
        }

        /**
         * a builder method to add a set of ContextWait to the ContextAggregator.
         * @param contexts
         * @return CanWaitForContext
         */
        public static CanWaitForContext waitForContext(Collection<? extends ContextWait> contexts) {
            return new ContextAggregatorBuilder(contexts);
        }

        /**
         * a builder method to add a ContextWait to the ContextAggregator.
         * @param context
         * @return CanWaitForContext
         */
        @Override
        public CanWaitForContext and(ContextWait context) {
            this.contextSet.add(context);
            return this;
        }

        /**
         * a builder method to set the ContextAggregator onRead method and build the ContextAggregator instance.
         * the onReady handler will be called once all ContextWait elements are completed
         * @param handler
         * @return ContextAggregator
         */
        @Override
        public ContextAggregator onReady(ReadyHandler handler) {
            return new ContextAggregator(contextSet, handler);
        }
    }

    public static class ContextWait<T> {
        private Set<ReadyHandler> readyHandlers = new LinkedHashSet<>();
        private T result;
        private boolean completed = false;

        /**
         * a factory method to create a ContextWait for specific data type.
         * @param <T>
         * @return
         */
        public static <T> ContextWait<T> create() {
            return new ContextWait<>();
        }

        void onReady(ReadyHandler handler) {
            this.readyHandlers.add(handler);
        }

        /**
         * complets the context with the specified value
         * @param result
         */
        public void complete(T result) {
            this.result = result;
            if (!completed) {
                readyHandlers.forEach(ReadyHandler::onReady);
            }
        }

        /**
         * get the value that was used to complete this context
         * if not completed this will return null
         * @return
         */
        public T get() {
            return result;
        }
    }
}
