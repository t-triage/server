package com.clarolab.runner.custom;

import java.lang.annotation.*;

import static com.clarolab.runner.custom.Categories.CombinationRule.OR;

// Annotation for enumerating category classes for a suite

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Categories {
    /**
     * @return categories of classes to be run
     */
    public Class<?>[] categoryClasses();

    /**
     * @return rule to combine Category classes
     */
    CombinationRule rule() default OR;

    enum CombinationRule {
        AND, OR
    }
}
