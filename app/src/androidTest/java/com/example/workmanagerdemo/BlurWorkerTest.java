package com.example.workmanagerdemo;

import android.net.Uri;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;

import com.example.workmanagerdemo.workers.BlurWorker;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.example.workmanagerdemo.Constants.KEY_IMAGE_URI;
import static org.hamcrest.MatcherAssert.assertThat;

public class BlurWorkerTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Rule
    public WorkManagerTestRule wmRule = new WorkManagerTestRule();

    @Test
    public void testFailsIfNoInput() throws ExecutionException, InterruptedException {
        // Create Request
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(BlurWorker.class).build();
        // Enqueue and wait for result. This also run the work asynchronously
        // because we are using Synchronous Executor
        wmRule.getWorkManager().enqueue(request).getResult().get();
        // Get Work Info
        WorkInfo workInfo = wmRule.getWorkManager().getWorkInfoById(request.getId()).get();

        assertThat(String.valueOf(workInfo.getState()),true);

    }

    @Test
    public void testAppliesBlur() throws IOException, ExecutionException, InterruptedException {
        // Define Input Data
        Uri inputDataUri = TestUtils.copyFileFromTestToTargetCtx(wmRule.getTestContext(),
                wmRule.getTargetContext(),"test_image.jpg");

        Data inputData = new Data.Builder().put(KEY_IMAGE_URI,inputDataUri.toString()).build();

        // Create Request
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(BlurWorker.class)
                .setInputData(inputData)
                .build();
        // Enqueue and wait for Result. This also run the work asynchronously
        // because we are using a SynchronousExecutor
        wmRule.getWorkManager().enqueue(request).getResult().get();
        // Get Work Info
        WorkInfo workInfo = wmRule.getWorkManager().getWorkInfoById(request.getId()).get();
        Uri outputUri = Uri.parse(workInfo.getOutputData().getString(KEY_IMAGE_URI));

        // Assert
        assertThat(String.valueOf(TestUtils.uriFileExists(wmRule.getTargetContext(),
                String.valueOf(outputUri))),true);
        assertThat(String.valueOf((workInfo.getState().equals(WorkInfo.State.SUCCEEDED))),true);
    }
}
