package com.common.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.aventstack.extentreports.ExtentTest;
import com.common.report.ExtentManager;

import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.HookTestStep;
import io.cucumber.plugin.event.PickleStepTestStep;
import io.cucumber.plugin.event.Result;
import io.cucumber.plugin.event.Status;
import io.cucumber.plugin.event.TestCase;
import io.cucumber.plugin.event.TestCaseFinished;
import io.cucumber.plugin.event.TestCaseStarted;
import io.cucumber.plugin.event.TestRunFinished;
import io.cucumber.plugin.event.TestStepFinished;
import io.cucumber.plugin.event.TestStepStarted;

/**
 * Cucumber plugin that drives scenario/step logging and the Extent report
 * lifecycle. Registered in TestRunner (and junit-platform.properties for IDE runs).
 *
 * Events are published on the thread executing the scenario, so the
 * ThreadLocals in ExtentManager and the Log4j ThreadContext stay correct
 * under parallel execution.
 */
public class ScenarioReportListener implements ConcurrentEventListener {

    private static final Logger LOG = LogManager.getLogger(ScenarioReportListener.class);
    private static final String SCENARIO_KEY = "scenario";

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestCaseStarted.class, this::onScenarioStarted);
        publisher.registerHandlerFor(TestStepStarted.class, this::onStepStarted);
        publisher.registerHandlerFor(TestStepFinished.class, this::onStepFinished);
        publisher.registerHandlerFor(TestCaseFinished.class, this::onScenarioFinished);
        publisher.registerHandlerFor(TestRunFinished.class, event -> ExtentManager.flush());
    }

    private void onScenarioStarted(TestCaseStarted event) {
        TestCase testCase = event.getTestCase();
        ThreadContext.put(SCENARIO_KEY, testCase.getName());
        LOG.info("========== START Scenario: {} {} ({}:{})", testCase.getName(), testCase.getTags(),
                testCase.getUri(), testCase.getLocation().getLine());
        ExtentManager.createScenario(testCase.getName(), testCase.getTags());
    }

    private void onStepStarted(TestStepStarted event) {
        if (event.getTestStep() instanceof PickleStepTestStep step) {
            String keyword = step.getStep().getKeyword().trim();
            String text = step.getStep().getText();
            LOG.info("STEP: {} {}", keyword, text);
            ExtentManager.startStep(keyword, text);
        }
    }

    private void onStepFinished(TestStepFinished event) {
        Result result = event.getResult();

        if (event.getTestStep() instanceof PickleStepTestStep step) {
            String stepText = step.getStep().getKeyword().trim() + " " + step.getStep().getText();
            ExtentTest node = ExtentManager.getLogger();
            switch (result.getStatus()) {
                case PASSED -> node.pass("Passed in " + result.getDuration().toMillis() + " ms");
                case FAILED, AMBIGUOUS -> {
                    LOG.error("STEP {}: {}", result.getStatus(), stepText, result.getError());
                    node.fail(result.getError());
                }
                case UNDEFINED -> {
                    LOG.warn("STEP UNDEFINED (no matching step definition): {}", stepText);
                    node.warning("Undefined step: no matching step definition");
                }
                case PENDING -> {
                    LOG.warn("STEP PENDING: {}", stepText);
                    node.skip("Pending");
                }
                case SKIPPED -> {
                    LOG.info("STEP SKIPPED: {}", stepText);
                    node.skip("Skipped");
                }
                default -> node.info(result.getStatus().name());
            }
            ExtentManager.endStep();
        } else if (event.getTestStep() instanceof HookTestStep hook && result.getStatus() == Status.FAILED) {
            LOG.error("{} hook failed: {}", hook.getHookType(), hook.getCodeLocation(), result.getError());
            if (ExtentManager.hasTest()) {
                ExtentManager.getLogger().fail(hook.getHookType() + " hook failed: " + hook.getCodeLocation())
                        .fail(result.getError());
            }
        }
    }

    private void onScenarioFinished(TestCaseFinished event) {
        Result result = event.getResult();
        String message = "========== END Scenario: {} -> {} ({} ms)";
        if (result.getStatus().isOk()) {
            LOG.info(message, event.getTestCase().getName(), result.getStatus(), result.getDuration().toMillis());
        } else {
            LOG.error(message, event.getTestCase().getName(), result.getStatus(), result.getDuration().toMillis());
        }
        ExtentManager.removeTest();
        ThreadContext.remove(SCENARIO_KEY);
    }
}
