package de.platon42.intellij.plugins.cajon

import com.intellij.pom.java.LanguageLevel
import com.intellij.testFramework.TestLoggerFactory
import com.intellij.testFramework.fixtures.IdeaTestExecutionPolicy
import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import com.intellij.testFramework.runInEdtAndWait
import de.platon42.intellij.jupiter.AddLocalJarToModule
import de.platon42.intellij.jupiter.LightCodeInsightExtension
import de.platon42.intellij.jupiter.TestDataPath
import de.platon42.intellij.jupiter.TestJdk
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtendWith
import java.lang.reflect.InvocationTargetException

@ExtendWith(LightCodeInsightExtension::class)
@TestDataPath("src/test/resources")
@TestJdk(LanguageLevel.JDK_1_8, annotations = true, useInternal = true)
@AddLocalJarToModule(Assertions::class)
abstract class AbstractCajonTest {

    // See https://github.com/junit-team/junit5/issues/157
    protected fun runTest(body: () -> Unit) {
        val throwables = arrayOfNulls<Throwable>(1)

        invokeTestRunnable {
            try {
                TestLoggerFactory.onTestStarted()
                body()
                TestLoggerFactory.onTestFinished(true)
            } catch (e: InvocationTargetException) {
                TestLoggerFactory.onTestFinished(false)
                e.fillInStackTrace()
                throwables[0] = e.targetException
            } catch (e: IllegalAccessException) {
                TestLoggerFactory.onTestFinished(false)
                e.fillInStackTrace()
                throwables[0] = e
            } catch (e: Throwable) {
                TestLoggerFactory.onTestFinished(false)
                throwables[0] = e
            }
        }

        if (throwables[0] != null) {
            throw throwables[0]!!
        }
    }

    private fun invokeTestRunnable(runnable: () -> Unit) {
        val policy = IdeaTestExecutionPolicy.current()
        if (policy != null && !policy.runInDispatchThread()) {
            runnable()
        } else {
            runInEdtAndWait {
                runnable()
            }
        }
    }

    protected fun executeQuickFixes(myFixture: JavaCodeInsightTestFixture, regex: Regex, expectedFixes: Int) {
        val quickfixes = myFixture.getAllQuickFixes().filter { it.familyName.matches(regex) }
        assertThat(quickfixes).hasSize(expectedFixes)
        quickfixes.forEach(myFixture::launchAction)
    }
}