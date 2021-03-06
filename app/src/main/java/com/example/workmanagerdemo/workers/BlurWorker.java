package com.example.workmanagerdemo.workers;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.workmanagerdemo.Constants;
import java.io.FileNotFoundException;

import static com.example.workmanagerdemo.Constants.PROGRESS;
import static com.example.workmanagerdemo.workers.WorkerUtils.sleep;

public class BlurWorker extends Worker {
    /**
     * Creates an instance of the {@link Worker}.
     * @param appContext   the application {@link Context}
     * @param workerParams the set of {@link WorkerParameters}
     */
    public BlurWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    private static final String TAG = BlurWorker.class.getSimpleName();

    @NonNull
    @Override
    public Worker.Result doWork() {

        Context applicationContext = getApplicationContext();
        // Makes a notification when the work starts and slows down the work so that it's easier to
        // see each WorkRequest start, even on emulated devices
        WorkerUtils.makeStatusNotification("Blurring image", applicationContext);
        String resourceUri = getInputData().getString(Constants.KEY_IMAGE_URI);
        try {
            if (TextUtils.isEmpty(resourceUri)) {
                Log.e(TAG, "Invalid input uri");
                throw new IllegalArgumentException("Invalid input uri");
            }
            ContentResolver resolver = applicationContext.getContentResolver();
            // Create a bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(
                    resolver.openInputStream(Uri.parse(resourceUri)));
            // Blur the bitmap
            Bitmap output = WorkerUtils.blurBitmap(bitmap, applicationContext);
            // Write bitmap to a temp file
            Uri outputUri = WorkerUtils.writeBitmapToFile(applicationContext, output);
            // Return the output for the temp file
            Data outputData = new Data.Builder().putString(
                    Constants.KEY_IMAGE_URI, outputUri.toString()).build();

            int i= 0;
            while (i<101){
//                setProgressAsync(outputData);
                setProgressAsync(new Data.Builder().putInt(PROGRESS,i).build());
                sleep();
                i = i + 10;
            }
            // If there were no errors, return SUCCESS
            return Result.success(outputData);
        } catch (FileNotFoundException fileNotFoundException) {
            Log.e(TAG, "Failed to decode input stream", fileNotFoundException);
            throw new RuntimeException("Failed to decode input stream", fileNotFoundException);

        } catch (Throwable throwable) {
            // If there were errors, return FAILURE
            Log.e(TAG, "Error applying blur", throwable);
            return Result.failure();
        }
    }
}
