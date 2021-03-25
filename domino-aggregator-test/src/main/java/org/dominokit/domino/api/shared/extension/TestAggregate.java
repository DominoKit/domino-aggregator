package org.dominokit.domino.api.shared.extension;

public class TestAggregate {

    private TestComplete testComplete;

    public TestAggregate() {
        TestComplete testComplete = new TestComplete().init(this);
        testComplete.completeParam1("some string");
        testComplete.completeParam2(10);
        testComplete.completeParam3(10.0);
    }

    public static void main(String[] args) {
        TestAggregate testAggregate= new TestAggregate();

    }

    @Aggregate(name="TestComplete")
    public void onCompleteAll(String param1, Integer param2, Double param3){
        System.out.println(param1);
        System.out.println(param2);
        System.out.println(param3);
    }
}
