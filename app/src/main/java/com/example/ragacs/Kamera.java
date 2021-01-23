package com.example.ragacs;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class Kamera extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "MainActivity";
    JavaCameraView javaCameraView;
    Mat mat1;
    Mat mat2;
    Scalar color = new Scalar(0, 0, 255);
    Mat kernel1;
    Mat labels;
    ArrayList<MatOfPoint> contours;
    TextView textView;
    ImageView kameraGomb;

    private final BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            super.onManagerConnected(status);
            if (status == BaseLoaderCallback.SUCCESS) {
                javaCameraView.enableView();
            } else {
                super.onManagerConnected(status);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_kamera);
        javaCameraView = findViewById (R.id.my_camera_view);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);

        kameraGomb = findViewById(R.id.kameraGomb);
        kameraGomb.setOnClickListener(view -> {
            this.onPause();
            kameraGomb.setVisibility(SurfaceView.INVISIBLE);
        });

    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {
       mat1.release();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
       mat1 = inputFrame.rgba();
       mat2 = new Mat();
       Imgproc.cvtColor(mat1, mat2, Imgproc.COLOR_RGB2GRAY);
       Imgproc.medianBlur(mat2, mat2, 3);

        Imgproc.threshold(mat2, mat2, 70, 255, Imgproc.THRESH_BINARY_INV);
        kernel1 = Mat.ones(new Size(5.0, 5.0), CvType.CV_8U);
        labels = new Mat();
        contours= new ArrayList<>();

        Imgproc.dilate(mat2, mat2, kernel1);
        Imgproc.connectedComponents(mat2, labels);
        Imgproc.findContours(mat2, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.drawContours(mat1, contours, -1, color, 2);
        textView = findViewById(R.id.textView);
        textView.setText("Találatok: " + contours.size());
        mat2.release();
        mat2.release();
        labels.release();
        kernel1.release();
        contours.clear();

        return mat1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(javaCameraView != null){
            javaCameraView.disableView();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(javaCameraView != null){
            javaCameraView.disableView();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV OK");
            baseLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        }else{
            Log.d(TAG, "OpenCV NOT OK");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, baseLoaderCallback);
        }

    }
}