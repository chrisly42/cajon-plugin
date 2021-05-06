package de.platon42.intellij.jupiter;

import com.intellij.jarRepository.JarRepositoryManager;
import com.intellij.jarRepository.RemoteRepositoryDescription;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.JavaAwareProjectJdkTableImpl;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.DependencyScope;
import com.intellij.openapi.roots.LibraryOrderEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.ui.OrderRoot;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.*;
import com.intellij.testFramework.fixtures.IdeaTestExecutionPolicy;
import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.utils.library.RepositoryLibraryProperties;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LightCodeInsightExtension implements ParameterResolver, AfterTestExecutionCallback, InvocationInterceptor {

    private static final Logger LOG = Logger.getLogger(LightCodeInsightExtension.class.getName());

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Parameter parameter = parameterContext.getParameter();
        return parameter.isAnnotationPresent(MyFixture.class)
                || parameter.isAnnotationPresent(MyTestCase.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        LightCodeInsightFixtureTestCaseWrapper testCase = getWrapper(extensionContext);
        Parameter parameter = parameterContext.getParameter();
        if (parameter.isAnnotationPresent(MyFixture.class)) {
            return testCase.getMyFixture();
        } else if (parameter.isAnnotationPresent(MyTestCase.class)) {
            return testCase;
        }
        return null;
    }

    private LightCodeInsightFixtureTestCaseWrapper getWrapper(ExtensionContext extensionContext) {
        Store store = getStore(extensionContext);
        return (LightCodeInsightFixtureTestCaseWrapper) store.getOrComputeIfAbsent("testCase",
                key -> {
                    LightCodeInsightFixtureTestCaseWrapper wrapper = new LightCodeInsightFixtureTestCaseWrapper(extensionContext);
                    try {
                        wrapper.setUp();
                    } catch (Exception e) {
                        LOG.severe("Exception during setUp(): " + e);
                        throw new IllegalStateException("Exception during setUp()", e);
                    }
                    return wrapper;
                });
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        Store store = getStore(context);
        LightCodeInsightFixtureTestCaseWrapper testCase = (LightCodeInsightFixtureTestCaseWrapper) store.get("testCase");
        if (testCase != null) {
            testCase.tearDown();
        }
    }

    private static Store getStore(ExtensionContext context) {
        return context.getStore(Namespace.create(LightCodeInsightExtension.class, context.getRequiredTestMethod()));
    }

    @Override
    public void interceptTestMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
        Throwable[] throwables = new Throwable[1];

        Runnable runnable = () -> {
            try {
                TestLoggerFactory.onTestStarted();
                invocation.proceed();
                TestLoggerFactory.onTestFinished(true);
            } catch (Throwable e) {
                TestLoggerFactory.onTestFinished(false);
                throwables[0] = e;
            }
        };

        invokeTestRunnable(runnable);

        if (throwables[0] != null) {
            throw throwables[0];
        }
    }

    private static void invokeTestRunnable(@NotNull Runnable runnable) {
        IdeaTestExecutionPolicy policy = IdeaTestExecutionPolicy.current();
        if (policy != null && !policy.runInDispatchThread()) {
            runnable.run();
        } else {
            EdtTestUtilKt.runInEdtAndWait(() -> {
                runnable.run();
                return null;
            });
        }
    }

    private static class LightCodeInsightFixtureTestCaseWrapper extends LightJavaCodeInsightFixtureTestCase {
        private final ExtensionContext extensionContext;

        private LightCodeInsightFixtureTestCaseWrapper(ExtensionContext extensionContext) {
            this.extensionContext = extensionContext;
        }

        @Override
        public void setUp() throws Exception {
            super.setUp();
            Store store = getStore(extensionContext);
            store.put("disposable", Disposer.newDisposable("LightCodeInsightFixtureTestCaseWrapper"));
        }

        @Override
        public void tearDown() throws Exception {
            super.tearDown();
            Store store = getStore(extensionContext);
            Disposable disposable = (Disposable) store.get("disposable");
            UsefulTestCase.clearFields(this);
            if (myFixture != null && disposable != null) {
                Disposer.dispose(disposable);
                store.remove("disposable");
            }
        }

        @NotNull
        @Override
        protected LightProjectDescriptor getProjectDescriptor() {
            TestJdk testJdk = getMethodOrClassAnnotation(TestJdk.class);
            if (testJdk == null) {
                return super.getProjectDescriptor();
            }
            return new ProjectDescriptor(testJdk.value(), testJdk.annotations()) {
                @Override
                public Sdk getSdk() {
                    return testJdk.useInternal()
                            ? JavaAwareProjectJdkTableImpl.getInstanceEx().getInternalJdk()
                            : super.getSdk();
                }

                @Override
                public void configureModule(@NotNull Module module, @NotNull ModifiableRootModel model, @NotNull ContentEntry contentEntry) {
                    super.configureModule(module, model, contentEntry);
                    AddLocalJarToModule localJars = getMethodOrClassAnnotation(AddLocalJarToModule.class);
                    if (localJars != null) {
                        Stream.of(localJars.value()).forEach(it -> addJarContaining(model, it));
                    }
                    List<AddMavenDependencyToModule> mavenDependencies = getMethodOrClassAnnotations(AddMavenDependencyToModule.class);
                    mavenDependencies.forEach(it -> addFromMaven(model, it.value(), it.includeTransitiveDependencies, it.scope));
                }
            };
        }

        void addJarContaining(ModifiableRootModel model, Class<?> clazz) {
            try {
                Path jarPath = Paths.get(clazz.getProtectionDomain().getCodeSource().getLocation().toURI());

                VirtualFile jarFile = LocalFileSystem.getInstance().findFileByIoFile(jarPath.toFile());
                myFixture.allowTreeAccessForFile(jarFile);
                PsiTestUtil.addLibrary(
                        model,
                        jarPath.getFileName().toString().replace(".jar", ""),
                        jarPath.getParent().toString(),
                        jarPath.getFileName().toString()
                );
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("Class URL malformed", e);
            }
        }

        void addFromMaven(ModifiableRootModel model, String mavenCoordinates,
                          boolean includeTransitiveDependencies, DependencyScope dependencyScope) {
            List<RemoteRepositoryDescription> remoteRepositoryDescriptions = RemoteRepositoryDescription.DEFAULT_REPOSITORIES;
            RepositoryLibraryProperties libraryProperties = new RepositoryLibraryProperties(mavenCoordinates, includeTransitiveDependencies);
            Collection<OrderRoot> roots =
                    JarRepositoryManager.loadDependenciesModal(model.getProject(), libraryProperties, false, false, null, remoteRepositoryDescriptions);
            LibraryTable.ModifiableModel tableModel = model.getModuleLibraryTable().getModifiableModel();
            Library library = tableModel.createLibrary(mavenCoordinates);
            Library.ModifiableModel libraryModel = library.getModifiableModel();
            if (roots.isEmpty()) {
                throw new IllegalStateException(String.format("No roots for '%s'", mavenCoordinates));
            }

            for (OrderRoot root : roots) {
                libraryModel.addRoot(root.getFile(), root.getType());
            }

            LibraryOrderEntry libraryOrderEntry = model.findLibraryOrderEntry(library);
            if (libraryOrderEntry == null) {
                throw new IllegalStateException("Unable to find registered library " + mavenCoordinates);
            }
            libraryOrderEntry.setScope(dependencyScope);

            libraryModel.commit();
            tableModel.commit();
        }

        @Override
        protected String getTestDataPath() {
            TestDataPath testDataPath = getMethodOrClassAnnotation(TestDataPath.class);
            if (testDataPath == null) {
                return super.getTestDataPath();
            }
            TestDataSubPath testDataSubPath = getMethodOrClassAnnotation(TestDataSubPath.class);
            if (testDataSubPath == null) {
                return testDataPath.value();
            }
            return Paths.get(testDataPath.value(), testDataSubPath.value()).toString();
        }

        public JavaCodeInsightTestFixture getMyFixture() {
            return myFixture;
        }

        private <T extends Annotation> T getMethodOrClassAnnotation(Class<T> clazz) {
            T annotation = extensionContext.getRequiredTestMethod().getAnnotation(clazz);
            if (annotation == null) {
                annotation = extensionContext.getRequiredTestClass().getAnnotation(clazz);
            }
            return annotation;
        }

        private <T extends Annotation> List<T> getMethodOrClassAnnotations(Class<T> clazz) {
            return Stream.of(extensionContext.getRequiredTestMethod().getAnnotationsByType(clazz),
                    extensionContext.getRequiredTestClass().getAnnotationsByType(clazz))
                    .flatMap(Arrays::stream)
                    .collect(Collectors.toList());
        }
    }
}
