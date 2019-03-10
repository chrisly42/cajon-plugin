package de.platon42.intellij.jupiter;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface AddLocalJarToModule {
    Class[] value();
}