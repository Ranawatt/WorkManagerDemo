package com.example.workmanagerdemo;

import android.net.Uri;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import com.example.workmanagerdemo.workers.CleanupWorker;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.MatcherAssert.assertThat;


public class CleanupWorkerTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Rule
    public WorkManagerTestRule wmRule = new WorkManagerTestRule();

    @Test
    public  void  testCleanupWork() throws IOException, ExecutionException, InterruptedException {

        Uri testUri = TestUtils.copyFileFromTestToTargetCtx(wmRule.getTestContext(),wmRule.getTargetContext(),"test_image.jpg");
        assertThat(String.valueOf(TestUtils.uriFileExists(wmRule.getTargetContext(),testUri.toString())),true);

        // Create Request
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(CleanupWorker.class).build();
        // Enqueue and wait for result. This also run the work asynchronously
        // because we are using a SynchronousExecutor
        wmRule.getWorkManager().enqueue(request).getResult().get();
        // Get Work Info
        WorkInfo workInfo = wmRule.getWorkManager().getWorkInfoById(request.getId()).get();

        // Assert
        assertThat(String.valueOf(TestUtils.uriFileExists(wmRule.getTargetContext(),testUri.toString())),true);
        assertThat(String.valueOf(workInfo.getState().isFinished()),true);
    }
}
