![GWT3/J2CL compatible](https://img.shields.io/badge/GWT3/J2CL-compatible-brightgreen.svg)
![Deploy](https://github.com/DominoKit/domino-aggregator/workflows/Deploy/badge.svg)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.dominokit/domino-aggregator/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.dominokit/domino-aggregator)


# domino-aggregate

provides a declarative way to wait for a set of events to be completed before executing some code.

> This might be implemented later using Rx or Futures.

### Maven dependencies

 - Lib
 
```
<dependency>
  <groupId>org.dominokit</groupId>
  <artifactId>domino-aggregator-shared</artifactId>
  <version>1.0.0</version>
</dependency>

```
 - Annotation processor

```
<dependency>
  <groupId>org.dominokit</groupId>
  <artifactId>domino-aggregator-apt</artifactId>
  <version>1.0.0</version>
  <scope>provided</scope>
</dependency>
```

### Usage

- Basic

```java
ContextAggregator.ContextWait<String> waitForString = ContextAggregator.ContextWait.create();
ContextAggregator.ContextWait<Integer> waitForInteger = ContextAggregator.ContextWait.create();

ContextAggregator.waitFor(waitForString, waitForInteger)
        .onReady(() -> {
            //will be called when both waits are resolved.
        });

waitForString.complete("some string");
waitForInteger.complete(5);
```

- Declarative

 1 - iIn a class define a method and annotate it as `@Aggregate` specifying a desired class name, and the method parameters represent the events to be received.

```java
public class App{
    @Aggregate(name = "EventsAggregate")
    public void onAllEventCompleted(String event1, Long event2){
        //will be called when both waits are resolved.
    }
}

```

 2 - Define a field of the aggregate class, which should be generated for you

```java
private EventsAggregate eventsAggregate = new EventsAggregate();
```

 3 - Init the aggregate with an instance of the target class from which the method will be called.

```java
eventsAggregate.init(appInstance);
```

4- Complete the events to invoke the target method

```java
eventsAggregate.completeEvent1("some string");
eventsAggregate.completeEvent2(5);
```

### Full sample

```java

public class AggregateTest {

    private EventsAggregate eventsAggregate = new EventsAggregate();

    
    public void AggregateTest(){
        eventsAggregate.init(this);
    }

    @Aggregate(name = "EventsAggregate")
    public void onAllEventCompleted(String event1, Long event2){
        //this will be called when both events are completed.
    }

    //In some other parts you complete the events, something like in a success of failed rest call.
    public void shouldCallAggregateMethod(){
        eventsAggregate.completeEvent1("some String");
        eventsAggregate.completeEvent2(5L);
    }
}

```



