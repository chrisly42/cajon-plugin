package de.platon42.intellij.jupiter;

import com.intellij.pom.java.LanguageLevel;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface TestJdk {
    LanguageLevel value();

    boolean annotations() default false;

    boolean useInternal() default false;
}