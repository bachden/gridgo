package io.gridgo.core.support.config;

import java.util.List;

import io.gridgo.core.GridgoContext;
import io.gridgo.core.support.subscription.GatewaySubscription;
import io.gridgo.core.support.subscription.ProcessorSubscription;
import lombok.Builder;
import lombok.Getter;

public class ConfiguratorNode {

    @Getter
    @Builder
    public static class RootNode {

        private String applicationName;

        private List<ComponentNode> componentsContexts;

        private List<GatewayNode> gatewayContexts;

        public void visitChildren(ContextConfiguratorVisitor visitor, GridgoContext gc) {
            for (var ctx : gatewayContexts) {
                visitor.visit(gc, ctx);
            }
            for (var ctx : componentsContexts) {
                visitor.visit(gc, ctx);
            }
        }
    }

    @Getter
    @Builder
    public static class GatewayNode {

        private boolean autoStart;

        private String name;

        private int order;

        private List<ConnectorNode> connectorContexts;

        private List<SubscriberNode> subscriberContexts;

        public void visitChildren(ContextConfiguratorVisitor visitor, GatewaySubscription sub) {
            for (var connectorCtx : connectorContexts) {
                visitor.visit(sub, connectorCtx);
            }
            for (var subscriberCtx : subscriberContexts) {
                visitor.visit(sub, subscriberCtx);
            }
        }
    }

    @Getter
    @Builder
    public static class ConnectorNode {

        private String endpoint;

        private String contextBuilder;
    }

    @Getter
    @Builder
    public static class SubscriberNode {

        private String processor;

        private String condition;

        private String executionStrategy;

        private List<InstrumenterNode> instrumenterContexts;

        public void visitChildren(ContextConfiguratorVisitor visitor, ProcessorSubscription procSub) {
            for (var ctx : instrumenterContexts) {
                visitor.visit(procSub, ctx);
            }
        }
    }

    @Getter
    @Builder
    public static class InstrumenterNode {

        private String instrumenter;

        private String condition;
    }

    @Getter
    @Builder
    public static class ComponentNode {

        private String component;
    }
}