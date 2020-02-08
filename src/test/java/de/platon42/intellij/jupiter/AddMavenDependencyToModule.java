package de.platon42.intellij.jupiter;

import com.intellij.openapi.roots.DependencyScope;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Repeatable(AddMavenDependencyToModule.List.class)
public @interface AddMavenDependencyToModule {
    String value();

    boolean includeTransitiveDependencies = false;

    DependencyScope scope = DependencyScope.COMPILE;

    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @interface List {
        AddMavenDependencyToModule[] value();
    }
}