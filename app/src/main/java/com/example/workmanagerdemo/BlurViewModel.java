package com.example.workmanagerdemo;

import android.app.Application;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.workmanagerdemo.workers.BlurWorker;
import com.example.workmanagerdemo.workers.CleanupWorker;
import com.example.workmanagerdemo.workers.SaveImageToFileWorker;

import java.util.List;

import static com.example.workmanagerdemo.Constants.IMAGE_MANIPULATION_WORK_NAME;
import static com.example.workmanagerdemo.Constants.KEY_IMAGE_URI;
import static com.example.workmanagerdemo.Constants.TAG_OUTPUT;
import static com.example.workmanagerdemo.Constants.TAG_PROGRESS;

public class BlurViewModel extends AndroidViewModel {

    private WorkManager mWorkManager;
    private Uri mImageUri;
    private Uri mOutputUri;
    private LiveData<List<WorkInfo>> mSavedWorkInfo;
    private LiveData<List<WorkInfo>> mProgressWorkInfoItem;

    public BlurViewModel(@NonNull Application application) {
        super(application);
        mWorkManager = WorkManager.getInstance(application);

        // This transformation makes sure that whenever the current work Id changes the WorkInfo
        // the UI is listening to changes
        mSavedWorkInfo = mWorkManager.getWorkInfosByTagLiveData(TAG_OUTPUT);
        mProgressWorkInfoItem = mWorkManager.getWorkInfosByTagLiveData(TAG_PROGRESS);
    }

    /**
     * Create the WorkRequest to apply the blur and save the resulting image
     * @param blurLevel The amount to blur the image
     */
    void applyBlur(int blurLevel) {
        // Add WorkRequest to Cleanup temporary images
        WorkContinuation continuation = mWorkManager
                .beginUniqueWork(IMAGE_MANIPULATION_WORK_NAME,
                        ExistingWorkPolicy.REPLACE,
                        OneTimeWorkRequest.from(CleanupWorker.class));

        // Add WorkRequests to blur the image the number of times requested
        for (int i = 0; i < blurLevel; i++) {
            OneTimeWorkRequest.Builder blurBuilder =
                    new OneTimeWorkRequest.Builder(BlurWorker.class);
            // Input the Uri if this is the first blur operation
            // After the first blur operation the input will be the output of previous
            // blur operations.
            if ( i == 0 ) {
                blurBuilder.setInputData(createInputDataForUri());
            }

            blurBuilder.addTag(TAG_PROGRESS);
            continuation = continuation.then(blurBuilder.build());
        }

        // Create charging constraint
        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(true)
                .build();

        // Add WorkRequest to save the image to the filesystem
        OneTimeWorkRequest save = new OneTimeWorkRequest.Builder(SaveImageToFileWorker.class)
                .setConstraints(constraints)
                .addTag(TAG_OUTPUT)
                .build();
        continuation = continuation.then(save);

        // Actually start the work
        continuation.enqueue();

    }
    /**
     * Cancel work using the work's unique name
     */
    void cancelWork() {
        mWorkManager.cancelUniqueWork(IMAGE_MANIPULATION_WORK_NAME);
    }
    /**
     * Creates the input data bundle which includes the Uri to operate on
     * @return Data which contains the Image Uri as a String
     */
    private Data createInputDataForUri() {
        Data.Builder builder = new Data.Builder();
        if (mImageUri != null) {
            builder.putString(KEY_IMAGE_URI, mImageUri.toString());
        }
        return builder.build();
    }

    private Uri uriOrNull(String uriString) {
        if (!TextUtils.isEmpty(uriString)) {
            return Uri.parse(uriString);
        }
        return null;
    }

    /**
     * Setters
     */
    void setImageUri(String uri) {
        mImageUri = uriOrNull(uri);
    }

    void setOutputUri(String outputImageUri) {
        mOutputUri = uriOrNull(outputImageUri);
    }

    /**
     * Getters
     */
    Uri getImageUri() {
        return mImageUri;
    }

    Uri getOutputUri() { return mOutputUri; }

    LiveData<List<WorkInfo>> getOutputWorkInfo() { return mSavedWorkInfo; }

    LiveData<List<WorkInfo>> getProgressWorkInfo(){
        return mProgressWorkInfoItem;
    }
}
