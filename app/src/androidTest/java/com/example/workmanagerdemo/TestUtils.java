package com.example.workmanagerdemo;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import static com.example.workmanagerdemo.Constants.OUTPUT_PATH;

public class TestUtils {
    /**
     * Copy a file from the asset folder in the testContext to the OUTPUT_PATH in the target context.
     * @param testCtx android test context
     * @param targetCtx target context
     * @param filename source asset file
     * @return Uri for temp file
     */
    public static Uri copyFileFromTestToTargetCtx(Context testCtx, Context targetCtx, String filename) throws IOException {

        String destinationFileName = String.format("blur-test-%s.png", UUID.randomUUID().toString());
        File outputDir = new File(targetCtx.getFilesDir(),OUTPUT_PATH);

        File outputFile = new File(outputDir, destinationFileName);
        BufferedInputStream bis = new BufferedInputStream(testCtx.getAssets().open(filename));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile));
        byte[] buf = new byte[1024];
        bis.read(buf);
        do {
            bos.write(buf);
        } while (bis.read(buf) != -1);
        bis.close();
        bos.close();
        return Uri.fromFile(outputFile);
    }

    /**
     * Check if a file exists in the given context.
     * @param targetCtx android test context
     * @param uri for the file
     * @return true if file exist, false if the file does not exist of the Uri is not valid
     */
    public static boolean uriFileExists(Context targetCtx, String uri ){
        if (uri.isEmpty()){ return false; }
        ContentResolver resolver = targetCtx.getContentResolver();
        try {
            BitmapFactory.decodeStream(resolver.openInputStream(Uri.parse(uri)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
