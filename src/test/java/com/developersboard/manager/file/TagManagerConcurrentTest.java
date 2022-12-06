package com.developersboard.manager.file;

import com.developersboard.config.ApplicationConfig;
import com.developersboard.internal.DeadlockDetectorAndRerunRule;
import com.developersboard.manager.command.FileTagCommandManager;
import com.developersboard.manager.file.impl.FileTagManager;
import com.developersboard.shared.impl.DefaultTag;
import com.developersboard.shared.impl.DefaultTaggedFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.nio.file.Paths;

@ContextConfiguration(classes = {
        DefaultTag.class,
        FileTagManager.class,
        DefaultTaggedFile.class,
        ApplicationConfig.class,
})
@Execution(ExecutionMode.CONCURRENT)
@ExtendWith({DeadlockDetectorAndRerunRule.class, SpringExtension.class})
class TagManagerConcurrentTest {

    /* Leave at 6 please */
    public static final int N_THREADS = 6;

    /**
     * Use this instance of fileManager in each of your tests - it will be
     * created fresh for each test.
     */
    @Autowired
    private TagManager cut; // class under test (cut)

    /**
     * Automatically called before each test, initializes the fileManager
     * instance
     * <p>
     * Each test will be run DeadlockDetectorAndRerunRule.N_ITERATIONS
     */
    @BeforeEach
    void setup() throws IOException {
        cut.init(FileTagCommandManager.listAllFiles());
    }

    /**
     * Create N_THREADS threads, with half of the threads adding new tags and
     * half iterating over the list of tags and counting the total number of
     * tags. Each thread should do its work (additions or iterations) 1,000
     * times. Assert that the additions all succeed and that the counting
     * doesn't throw any ConcurrentModificationException. There is no need to
     * make any assertions on the number of tags in each step.
     */
    @RepeatedTest(DeadlockDetectorAndRerunRule.N_ITERATIONS)
    void testP1AddAndListTag() {
        Assertions.fail("Not Implemented");
    }

    /**
     * Create N_THREADS threads, and have each thread add 1,000 different tags;
     * assert that each thread creating a different tag succeeds, and that at
     * the end, the list of tags contains all tags that should exist
     */
    @RepeatedTest(DeadlockDetectorAndRerunRule.N_ITERATIONS)
    void testP1ConcurrentAddTagDifferentTags() {
        Assertions.fail("Not Implemented");
    }

    /**
     * Create N_THREADS threads. Each thread should try to add the same 1,000
     * tags of your choice. Assert that each unique tag is added exactly once
     * (there will be N_THREADS attempts to add each tag). At the end, assert
     * that all tags that you created exist by iterating over all tags returned
     * by listTags()
     */
    @RepeatedTest(DeadlockDetectorAndRerunRule.N_ITERATIONS)
    void testP1ConcurrentAddTagSameTags() {
        Assertions.fail("Not Implemented");
    }

    /**
     * Create 1000 tags. Save the number of files (returned by listFiles()) to a
     * local variable.
     * <p>
     * Then create N_THREADS threads. Each thread should iterate over all files
     * (from listFiles()). For each file, it should select a tag and random from
     * the list returned by listTags(). Then, it should tag that file with that
     * tag. Then (regardless of the tagging succeeding or not), it should pick
     * another random tag, and delete it. You do not need to care if the
     * deletions pass or not either.
     * <p>
     * <p>
     * At the end (once all threads are completed) you should check that the
     * total number of files reported by listFiles matches what it was at the
     * beginning. Then, you should list all of the tags, and all of the files
     * that have each tag, making sure that the total number of files reported
     * this way also matches the starting count. Finally, check that the total
     * number of tags on all of those files matches the count returned by
     * listTags.
     */
    @RepeatedTest(DeadlockDetectorAndRerunRule.N_ITERATIONS)
    void testP2ConcurrentDeleteTagTagFile() throws Exception {
        Assertions.fail("Not Implemented");
    }

    /**
     * Create a tag. Add each tag to every file. Then, create N_THREADS and have
     * each thread iterate over all of the files returned by listFiles(),
     * calling removeTag on each to remove that newly created tag from each.
     * Assert that each removeTag succeeds exactly once.
     *
     */
    @RepeatedTest(DeadlockDetectorAndRerunRule.N_ITERATIONS)
    void testP2RemoveTagWhileRemovingTags() throws Exception {
        Assertions.fail("Not Implemented");
    }

    /**
     * Create N_THREADS threads and N_THREADS/2 tags. Half of the threads will
     * attempt to tag every file with (a different) tag. The other half of the
     * threads will count the number of files currently having each of those
     * N_THREADS/2 tags. Assert that there all operations succeed, and that
     * there are no ConcurrentModificationExceptions. Do not worry about how
     * many files there are of each tag at each step (no need to assert on
     * this).
     */
    @RepeatedTest(DeadlockDetectorAndRerunRule.N_ITERATIONS)
    void testP2TagFileAndListFiles() throws Exception {
        Assertions.fail("Not Implemented");
    }

    /**
     * Create N_THREADS threads, and have each try to echo some text into all of
     * the files using echoAll. At the end, assert that all files have the same
     * text.
     */
    @RepeatedTest(DeadlockDetectorAndRerunRule.N_ITERATIONS)
    void testP3ConcurrentEchoAll() throws Exception {
        Assertions.fail("Not Implemented");
    }

    /**
     * Create N_THREADS threads, and have half of those threads try to echo some
     * text into all of the files. The other half should try to cat all of the
     * files, asserting that all of the files should always have the same
     * content.
     */
    @RepeatedTest(DeadlockDetectorAndRerunRule.N_ITERATIONS)
    void testP3EchoAllAndCatAll() throws Exception {
        Assertions.fail("Not Implemented");
    }
}