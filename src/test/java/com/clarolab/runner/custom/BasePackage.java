package com.clarolab.runner.custom;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Annotation for specifying a package with categorized classes to lookup

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BasePackage {
    /**
     * @return name of package
     */
    String name();
}
