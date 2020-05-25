package org.dominokit.domino.apt.client.processors.aggregate;

import org.dominokit.domino.api.shared.extension.Aggregate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AggregateTest {

    private boolean called = false;

    private EventsAggregate eventsAggregate;
    private String event1;
    private Long event2;

    @Before
    public void setup(){
        eventsAggregate= new EventsAggregate(this);
    }

    @Aggregate(name = "EventsAggregate")
    public void onAllEventCompleted(String event1, Long event2){
        this.called = true;
        this.event1 = event1;
        this.event2 = event2;
    }

    @Test
    public void shouldCallAggregateMethod(){
        eventsAggregate.completeEvent1("some String");
        eventsAggregate.completeEvent2(5L);
        Assert.assertTrue(called);
        Assert.assertEquals(event1,"some String");
        Assert.assertTrue(event2.equals(5L));
    }
}
