package com.example.workmanagerdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.work.Data;
import androidx.work.WorkInfo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;
import com.example.workmanagerdemo.databinding.ActivityBlurBinding;

import static com.example.workmanagerdemo.Constants.PROGRESS;

public class BlurActivity extends AppCompatActivity {
    private BlurViewModel mViewModel;

    private ActivityBlurBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBlurBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Get the ViewModel
        mViewModel = ViewModelProviders.of(this).get(BlurViewModel.class);
        // Image uri should be stored in the ViewModel; put it there then display
        Intent intent = getIntent();
        String imageUriExtra = intent.getStringExtra(Constants.KEY_IMAGE_URI);
        mViewModel.setImageUri(imageUriExtra);
        if (mViewModel.getImageUri() != null) {
            Glide.with(this).load(mViewModel.getImageUri()).into(binding.imageView);
        }
        // Setup blur image file button
        binding.goButton.setOnClickListener(view -> mViewModel.applyBlur(getBlurLevel()));
        // Setup view output image file button
        binding.seeFileButton.setOnClickListener(view -> {
            Uri currentUri = mViewModel.getOutputUri();
            if (currentUri != null) {
                Intent actionView = new Intent(Intent.ACTION_VIEW, currentUri);
                if (actionView.resolveActivity(getPackageManager()) != null) {
                    startActivity(actionView);
                }
            }
        });
        // Hookup the Cancel button
        binding.cancelButton.setOnClickListener(view -> mViewModel.cancelWork());
        // Show work status
        mViewModel.getOutputWorkInfo().observe(this, listOfWorkInfo -> {
            // Note that these next few lines grab a single WorkInfo if it exists
            // This code could be in a Transformation in the ViewModel; they are included here
            // so that the entire process of displaying a WorkInfo is in one location.

            // If there are no matching work info, do nothing
            if (listOfWorkInfo == null || listOfWorkInfo.isEmpty()) {
                return;
            }
            // We only care about the one output status.
            // Every continuation has only one worker tagged TAG_OUTPUT
            WorkInfo workInfo = listOfWorkInfo.get(0);

            boolean finished = workInfo.getState().isFinished();
            if (!finished) {
                showWorkInProgress();
            } else {
                showWorkFinished();
                // Normally this processing, which is not directly related to drawing views on
                // screen would be in the ViewModel. For simplicity we are keeping it here.
                Data outputData = workInfo.getOutputData();

                String outputImageUri =
                        outputData.getString(Constants.KEY_IMAGE_URI);
                // If there is an output file show "See File" button
                if (!TextUtils.isEmpty(outputImageUri)) {
                    mViewModel.setOutputUri(outputImageUri);
                    binding.seeFileButton.setVisibility(View.VISIBLE);
                }
            }
        });
        //Show Work Progress
        mViewModel.getProgressWorkInfo().observe(this, listOfWorkInfo ->{
            if (listOfWorkInfo == null || listOfWorkInfo.isEmpty()) {
                return;
            }
            WorkInfo workInfo = listOfWorkInfo.get(0);
            if (WorkInfo.State.RUNNING == workInfo.getState()){
                int progress = workInfo.getProgress().getInt(PROGRESS, 0);
                binding.progressBar.setProgress(progress);
            }
        });
    }
    /**
     * Shows and hides views for when the Activity is processing an image
     */
    private void showWorkInProgress() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.cancelButton.setVisibility(View.VISIBLE);
        binding.goButton.setVisibility(View.GONE);
        binding.seeFileButton.setVisibility(View.GONE);
    }
    /**
     * Shows and hides views for when the Activity is done processing an image
     */
    private void showWorkFinished() {
        binding.progressBar.setVisibility(View.GONE);
        binding.cancelButton.setVisibility(View.GONE);
        binding.goButton.setVisibility(View.VISIBLE);
    }

    /**
     * Get the blur level from the radio button as an integer
     * @return Integer representing the amount of times to blur the image
     */
    private int getBlurLevel() {
        RadioGroup radioGroup = findViewById(R.id.radio_blur_group);

        switch(radioGroup.getCheckedRadioButtonId()) {
            case R.id.radio_blur_lv_1:
                return 1;
            case R.id.radio_blur_lv_2:
                return 2;
            case R.id.radio_blur_lv_3:
                return 3;
        }
        return 1;
    }
}
