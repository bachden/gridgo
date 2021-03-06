package io.gridgo.framework.execution.impl;

import java.util.function.Function;

import io.gridgo.framework.execution.ExecutionStrategy;
import io.gridgo.framework.support.Message;
import io.gridgo.framework.support.context.ExecutionContext;
import lombok.NonNull;

public class HashedExecutionStrategy extends AbstractMultiExecutionStrategy {

    private static final Function<Message, Integer> DEFAULT_HASH_FUNCTION = Message::hashCode;

    private Function<Message, Integer> hashFunction;

    public HashedExecutionStrategy(final int noThreads, Function<Integer, ExecutionStrategy> executorSupplier) {
        this(noThreads, executorSupplier, DEFAULT_HASH_FUNCTION);
    }

    public HashedExecutionStrategy(final int noThreads, Function<Integer, ExecutionStrategy> executorSupplier,
            Function<Message, Integer> hashFunction) {
        super(noThreads, executorSupplier);
        this.hashFunction = hashFunction;
    }

    @Override
    public void execute(final @NonNull Runnable runnable, Message request) {
        var hash = calculateHash(request);
        executors[hash].execute(runnable, request);
    }

    @Override
    public void execute(ExecutionContext<Message, Message> context) {
        var hash = calculateHash(context.getRequest());
        executors[hash].execute(context);
    }

    private int calculateHash(Message request) {
        if (request == null)
            return 0;
        return hashFunction.apply(request) % noThreads;
    }
}
