package de.platon42.intellij.jupiter;

import com.intellij.testFramework.EdtTestUtilKt;
import com.intellij.testFramework.TestLoggerFactory;
import com.intellij.testFramework.fixtures.IdeaTestExecutionPolicy;
import org.jetbrains.annotations.NotNull;

// See https://github.com/junit-team/junit5/issues/157
public class WorkaroundUntilJupiterGetsExecutorProvider {

    protected static void runTest(Runnable test) throws Throwable {
        Throwable[] throwables = new Throwable[1];

        Runnable runnable = () -> {
            try {
                TestLoggerFactory.onTestStarted();
                test.run();
                TestLoggerFactory.onTestFinished(true);
            } catch (Throwable e) {
                TestLoggerFactory.onTestFinished(false);
                e.fillInStackTrace();
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
}
