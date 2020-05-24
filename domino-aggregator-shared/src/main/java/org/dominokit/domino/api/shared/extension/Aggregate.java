package org.dominokit.domino.api.shared.extension;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as an aggregate method, aggregate method will be called wen aggregate context wait elements are completed.
 * each argument of the aggregate method will produce a context inside the generated aggregate.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Aggregate {

    /**
     * The name should be a valid java class name.
     * @return Valid java class name.
     */
    String name();
}
