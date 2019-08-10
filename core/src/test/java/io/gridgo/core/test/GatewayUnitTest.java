package io.gridgo.core.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import io.gridgo.bean.BValue;
import io.gridgo.connector.Connector;
import io.gridgo.connector.impl.resolvers.ClasspathConnectorResolver;
import io.gridgo.core.GridgoContext;
import io.gridgo.core.impl.DefaultGridgoContextBuilder;
import io.gridgo.core.support.ProducerJoinMode;
import io.gridgo.core.support.impl.SwitchComponent;
import io.gridgo.core.support.subscription.RoutingPolicy;
import io.gridgo.core.support.subscription.impl.DefaultRoutingPolicy;
import io.gridgo.core.support.subscription.impl.LibraSqlPredicate;
import io.gridgo.core.support.template.impl.MatchingProducerTemplate;
import io.gridgo.framework.support.Message;
import io.gridgo.framework.support.Payload;
import io.gridgo.framework.support.impl.SimpleRegistry;

public class GatewayUnitTest {

    private static final int NUM_MESSAGES = 100;

    @Test
    public void testNoSupportedProducer() throws InterruptedException {
        var latch = new CountDownLatch(2);
        var context = new DefaultGridgoContextBuilder().setName("test").build();

        context.openGateway("test").attachConnector("empty");

        context.openGateway("test2");

        context.start();

        context.findGateway("test").get().call(createType1Message()).done(response -> {
            if (response == null)
                latch.countDown();
        });

        context.findGateway("test2").get().call(createType1Message()).done(response -> {
            if (response == null)
                latch.countDown();
        });

        latch.await();

        context.stop();
    }

    @Test
    public void testSwitch() throws InterruptedException {
        var latch = new CountDownLatch(2);

        var registry = new SimpleRegistry().register("dummy", 1);
        var context = new DefaultGridgoContextBuilder().setName("test").setRegistry(registry).build();

        context.openGateway("test") //
               .attachConnector("test:dummy") //
               .subscribe((rc, gc) -> {
                   latch.countDown();
               });

        context.openGateway("test2") //
               .attachConnector("test:dummy") //
               .subscribe((rc, gc) -> {
                   latch.countDown();
               });

        context.attachComponent(new SwitchComponent("test2", "test"));

        context.start();

        context.findGateway("test2").get().push(createType1Message());

        latch.await();

        context.stop();
    }

    @Test
    public void testCallAndPush() throws InterruptedException {
        var latch1 = new CountDownLatch(1);
        var latch2 = new CountDownLatch(1);

        var registry = new SimpleRegistry().register("dummy1", 1).register("dummy2", 2);
        var context = new DefaultGridgoContextBuilder().setName("test").setRegistry(registry).build();

        RoutingPolicy policy1 = new DefaultRoutingPolicy(null).setCondition(new LibraSqlPredicate("payload.body.data == 1"));
        RoutingPolicy policy2 = new DefaultRoutingPolicy(null).setCondition(new LibraSqlPredicate("payload.body.data == 2"));
        context.openGateway("test2", new MatchingProducerTemplate((c, m) -> match(context, c, m))) //
               .attachConnector("test:dummy1", new ClasspathConnectorResolver("io.gridgo.connector.test")) //
               .attachConnector("test:dummy2", new ClasspathConnectorResolver("io.gridgo.connector.test")) //
               .subscribe((rc, gc) -> {
                   latch1.countDown();
               }).withPolicy(policy1) //
               .subscribe((rc, gc) -> {
                   latch2.countDown();
               }).withPolicy(policy2);

        context.start();

        var gateway2 = context.findGateway("test2").orElseThrow();
        gateway2.callAndPush(createType1Message());
        gateway2.callAndPush(createType2Message());

        latch1.await();
        latch2.await();

        context.stop();
    }

    @Test
    public void testProducerTemplate() throws InterruptedException {
        var registry = new SimpleRegistry().register("dummy1", 1).register("dummy2", 2);
        var context = new DefaultGridgoContextBuilder().setName("test").setRegistry(registry).build();

        context.openGateway("test", ProducerJoinMode.JOIN) //
               .attachConnector("test:dummy1") //
               .attachConnector("test:dummy2");
        context.openGateway("test2", new MatchingProducerTemplate((c, m) -> match(context, c, m))) //
               .attachConnector("test:dummy1") //
               .attachConnector("test:dummy2");

        context.start();

        var latch = new CountDownLatch(1);

        var gateway = context.findGateway("test").orElseThrow();
        gateway.call(createType1Message()).done(response -> {
            var body = response.body();
            Assert.assertTrue(body.isArray());
            Assert.assertEquals(2, body.asArray().size());
            Assert.assertEquals(Integer.valueOf(1), body.asArray().getArray(0).getInteger(2));
            Assert.assertEquals(Integer.valueOf(2), body.asArray().getArray(1).getInteger(2));
            latch.countDown();
        }).fail(ex -> ex.printStackTrace());

        latch.await();

        var latch2 = new CountDownLatch(2);

        var gateway2 = context.findGateway("test2").orElseThrow();
        gateway2.call(createType1Message()).done(response -> {
            var body = response.body();
            Assert.assertTrue(body.isArray());
            Assert.assertEquals(1, body.asArray().size());
            Assert.assertEquals(Integer.valueOf(1), body.asArray().getArray(0).getInteger(2));
            latch2.countDown();
        }).fail(ex -> ex.printStackTrace());
        gateway2.call(createType2Message()).done(response -> {
            var body = response.body();
            Assert.assertTrue(body.isArray());
            Assert.assertEquals(1, body.asArray().size());
            Assert.assertEquals(Integer.valueOf(2), body.asArray().getArray(0).getInteger(2));
            latch2.countDown();
        }).fail(ex -> ex.printStackTrace());

        latch.await();

        gateway.send(createType1Message());
        gateway2.send(createType1Message());

        context.stop();
    }

    private boolean match(GridgoContext context, Connector connector, Message msg) {
        var beanName = connector.getConnectorConfig().getPlaceholders().getProperty("bean");
        var beanValue = context.getRegistry().lookupMandatory(beanName, Integer.class);
        return beanValue == msg.body().asValue().getInteger();
    }

    @Test
    public void testConnector() throws InterruptedException {
        var beanValue = 1;
        var registry = new SimpleRegistry().register("dummy", beanValue).register("dummy1", 2);
        var context = new DefaultGridgoContextBuilder().setName("test").setRegistry(registry).build();

        var consumerLatch = new CountDownLatch(2);

        RoutingPolicy policy = new DefaultRoutingPolicy((rc, gc) -> {
            consumerLatch.countDown();
        }).setCondition(new LibraSqlPredicate("payload.body.data == " + beanValue));
        context.openGateway("test") //
               .attachConnector("test:dummy") //
               .attachConnector("test:dummy1") //
               .attachRoutingPolicy(policy);
        context.start();

        context.findGateway("test").get().push(createType1Message());
        context.findGateway("test").get().push(createType1Message());
        context.findGateway("test").get().push(createType2Message());
        context.findGateway("test").get().push(createType2Message());

        consumerLatch.await();

        var latch = new CountDownLatch(2);

        var gateway = context.findGateway("test").orElseThrow();
        gateway.call(createType1Message()).done(response -> {
            if (response.body().asValue().getInteger() == beanValue)
                latch.countDown();
        });
        gateway.sendWithAck(createType1Message()).done(response -> {
            Assert.assertNull(response);
            latch.countDown();
        });

        latch.await();

        context.stop();
    }

    @Test
    public void testPush() throws InterruptedException {
        var latch1 = new CountDownLatch(NUM_MESSAGES / 2);
        var latch2 = new CountDownLatch(NUM_MESSAGES / 2);
        var registry = new SimpleRegistry().register("dummy", 1);
        var context = new DefaultGridgoContextBuilder().setName("test").setRegistry(registry).build();
        var firstGateway = context.openGateway("test") //
                                  .subscribe((rc, gc) -> {
                                      latch1.countDown();
                                  }) //
                                  .when("payload.body.data == 1").finishSubscribing()//
                                  .subscribe((rc, gc) -> {
                                      latch2.countDown();
                                  }) //
                                  .when("payload.body.data == 2").finishSubscribing();

        context.start();

        var gateway = context.findGateway("test").orElseThrow();
        Assert.assertEquals(firstGateway, gateway);

        for (int i = 0; i < NUM_MESSAGES / 2; i++)
            gateway.push(createType1Message());
        for (int i = 0; i < NUM_MESSAGES / 2; i++)
            gateway.push(createType2Message());

        latch1.await();
        latch2.await();

        Assert.assertEquals(firstGateway, context.openGateway("test"));

        context.closeGateway("test");
        Assert.assertTrue(context.findGateway("test").isEmpty());

        context.stop();
    }

    @Test
    public void testInstrumenter() throws InterruptedException {
        var atomic = new AtomicInteger();
        var latch1 = new CountDownLatch(2);
        var context = new DefaultGridgoContextBuilder().setName("test").build();
        context.openGateway("test") //
               .subscribe((rc, gc) -> {
                   latch1.countDown();
               }) //
               .when("payload.body.data == 1") //
               .instrumentWith((msg, deferred, runnable) -> (() -> {
                   atomic.incrementAndGet();
                   runnable.run();
               })) //
               .finishSubscribing() //
               .subscribe((rc, gc) -> {
                   latch1.countDown();
               }) //
               .when("payload.body.data == 2") //
               .instrumentWith((msg, deferred, runnable) -> (() -> {
                   runnable.run();
               }));

        context.start();

        var gateway = context.findGateway("test").orElseThrow();
        gateway.push(createType1Message());
        gateway.push(createType2Message());

        latch1.await();

        Assert.assertEquals(1, atomic.get());

        context.stop();
    }

    private Message createType2Message() {
        return Message.of(Payload.of(BValue.of(2)));
    }

    private Message createType1Message() {
        return Message.of(Payload.of(BValue.of(1)));
    }
}
