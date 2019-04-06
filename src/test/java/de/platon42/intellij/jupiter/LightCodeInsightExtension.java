package de.platon42.intellij.jupiter;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.JavaAwareProjectJdkTableImpl;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.PsiTestUtil;
import com.intellij.testFramework.UsefulTestCase;
import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class LightCodeInsightExtension implements ParameterResolver, AfterTestExecutionCallback {

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

    private static class LightCodeInsightFixtureTestCaseWrapper extends LightCodeInsightFixtureTestCase {
        private final ExtensionContext extensionContext;

        private LightCodeInsightFixtureTestCaseWrapper(ExtensionContext extensionContext) {
            this.extensionContext = extensionContext;
        }

        @Override
        public void setUp() throws Exception {
            super.setUp();
        }

        @Override
        public void tearDown() throws Exception {
            super.tearDown();
            UsefulTestCase.clearFields(this);
            if (myFixture != null && getProject() != null && !getProject().isDisposed()) {
                Disposer.dispose(getProject());
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
                    AddLocalJarToModule methodOrClassAnnotation = getMethodOrClassAnnotation(AddLocalJarToModule.class);
                    if (methodOrClassAnnotation != null) {
                        Stream.of(methodOrClassAnnotation.value()).forEach(it -> addJarContaining(model, it));
                    }
                }
            };
        }

        protected void addJarContaining(ModifiableRootModel model, Class clazz) {
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
    }
}
