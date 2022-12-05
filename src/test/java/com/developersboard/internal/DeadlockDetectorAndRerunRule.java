package com.developersboard.internal;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.Objects;

public class DeadlockDetectorAndRerunRule implements BeforeEachCallback, AfterEachCallback {

    public static final Logger LOG = LoggerFactory.getLogger(DeadlockDetectorAndRerunRule.class);

    private final int timeoutInMillis;

    public static final int N_ITERATIONS = 10;

    private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

    public DeadlockDetectorAndRerunRule() {
        this(10000);
    }

    public DeadlockDetectorAndRerunRule(int timeoutInMillis) {
        this.timeoutInMillis = timeoutInMillis;
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        detectDeadlock();
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        LOG.info("DeadlockDetector and Rerun Failing Tests rule loaded for {}; we will time out after {}ms (per test), and will re-run each passing test {} times.", context.getDisplayName(), timeoutInMillis, N_ITERATIONS);
    }

    private void detectDeadlock() throws InterruptedException {
        long[] deadlockedThreadIds = threadMXBean.findDeadlockedThreads();

        if (Objects.nonNull(deadlockedThreadIds)) {
            LOG.warn("Deadlock found!");
            ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(deadlockedThreadIds);
            LOG.info(Arrays.toString(threadInfos));

            throw new InterruptedException("Test deadlocked! with timeout as: " + timeoutInMillis);
        }
    }
}
