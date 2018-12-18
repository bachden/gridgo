package io.gridgo.core.impl;

import org.joo.libra.PredicateContext;

import io.gridgo.core.GridgoContext;
import io.gridgo.core.support.RoutingContext;
import io.gridgo.core.support.subscription.RoutingPolicy;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RoutingPolicyEnforcer {

    private RoutingPolicy policy;

    public RoutingPolicyEnforcer(final @NonNull RoutingPolicy policy) {
        this.policy = policy;
    }

    public boolean isMatch(PredicateContext context) {
        return policy.getCondition().isEmpty() || policy.getCondition().get().satisfiedBy(context);
    }

    public void execute(RoutingContext rc, GridgoContext gc) {
        var runnable = buildRunnable(rc, gc);
        policy.getStrategy().ifPresentOrElse(s -> s.execute(runnable), runnable);
    }

    private Runnable buildRunnable(RoutingContext rc, GridgoContext gc) {
        Runnable runnable = () -> {
            try {
                doProcess(rc, gc);
            } catch (Exception ex) {
                log.error("Exception caught while executing processor", ex);
                if (rc.getDeferred() != null)
                    rc.getDeferred().reject(ex);
            }
        };
        if (policy.getInstrumenter().isPresent())
            runnable = policy.getInstrumenter().get().instrument(runnable);
        return runnable;
    }

    private void doProcess(RoutingContext rc, GridgoContext gc) {
        policy.getProcessor().process(rc, gc);
    }
}
