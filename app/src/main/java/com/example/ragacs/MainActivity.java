package com.example.ragacs;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
    Button btn_kamera;
    Button btn_galeria;
    Bitmap bitmap;
    ImageView imageView;
    Uri imageUri;
    Mat mat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        OpenCVLoader.initDebug();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_kamera =  findViewById(R.id.buttonCamera);
        btn_galeria =   findViewById(R.id.button);
        imageView = findViewById(R.id.imageView);
        btn_galeria.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,100);
            System.gc();
        });
        btn_kamera.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Kamera.class);
            startActivity(intent);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data!=null){
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                bitmap = BitmapFactory.decodeStream(inputStream);
                bitmap = szamolo(bitmap);

                imageView.setImageBitmap(bitmap);
                System.gc();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public Bitmap szamolo(Bitmap bitmap){
        mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);

//		Mat hsv = new Mat();
//		Imgproc.cvtColor(src, hsv, Imgproc.COLOR_BGR2HSV);
//		Mat mask = new Mat();
//		Scalar hsvLower = new Scalar(0, 140, 140);
//		Scalar hsvUpper = new Scalar(200, 255, 255);
//		Core.inRange(hsv, hsvLower, hsvUpper, mask);


//		Mat kernel1 = Mat.ones(new Size(50.0, 50.0), CvType.CV_8U);
//		Mat mask2 = new Mat();
//		Imgproc.morphologyEx(mask, mask2, Imgproc.MORPH_OPEN, kernel1);
//		Mat crop = new Mat();
//
//		Size size2 = new Size(100.0, 100.0);
//		Mat kernel2 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, size2);
//		Imgproc.morphologyEx(mask2, mask2, Imgproc.MORPH_CLOSE, kernel2);
//		Imgcodecs.imwrite("mask2.jpg", mask2);
//
//		src.copyTo(crop, mask2);
//		Imgcodecs.imwrite("crop.jpg", crop);

        Mat gray = new Mat();
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_RGB2GRAY);
        Imgproc.medianBlur(gray, gray, 3);
        Mat binaryKicsi = new Mat();
        Mat binaryNagy = new Mat();
        Imgproc.threshold(gray, binaryKicsi, 60, 255, Imgproc.THRESH_BINARY_INV);
        Imgproc.threshold(gray, binaryNagy, 65, 255, Imgproc.THRESH_BINARY_INV);

//		Imgproc.adaptiveThreshold(gray, binary, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 55,
//				21);
        //Imgcodecs.imwrite("binary.jpg", binaryKicsi);
        Mat kernel1 = Mat.ones(new Size(10.0, 10.0), CvType.CV_8U);
        Imgproc.dilate(binaryKicsi, binaryKicsi, kernel1);

        Mat kernel12 = Mat.ones(new Size(10.0, 10.0), CvType.CV_8U);
        Imgproc.morphologyEx(binaryKicsi, binaryKicsi, Imgproc.MORPH_CLOSE, kernel12);

        /*
        Mat kernel2 = Mat.ones(new Size(50.0, 1.0), CvType.CV_8U);
        Imgproc.morphologyEx(binaryKicsi, binaryKicsi, Imgproc.MORPH_CLOSE, kernel2);

        Mat kernel22 = Mat.ones(new Size(1.0, 50.0), CvType.CV_8U);
        Imgproc.morphologyEx(binaryKicsi, binaryKicsi, Imgproc.MORPH_CLOSE, kernel22);
         */

        Mat labels = new Mat();
        Imgproc.connectedComponents(binaryKicsi, labels);

        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(binaryKicsi, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        int kicsi = 0;

        //ArrayList<Double> kontur = new ArrayList<Double>();

        for (MatOfPoint matOfPoint : contours) {
//			System.out.println(Imgproc.contourArea(matOfPoint));


            //kontur.add(Imgproc.contourArea(matOfPoint));
            if (Imgproc.contourArea(matOfPoint) < 3000) {
                kicsi++;
            }

        }

//		Mat kernel3 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(50, 1));
//		Imgproc.morphologyEx(binary, binary, Imgproc.MORPH_CLOSE, kernel3);


//		Mat kernel4 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 50));
//		Imgproc.morphologyEx(binary, binary, Imgproc.MORPH_CLOSE, kernel4);


        Mat kernel11 = Mat.ones(new Size(7.0, 9.0), CvType.CV_8U);
        Imgproc.dilate(binaryNagy, binaryNagy, kernel11);

        Mat kernel222 = Mat.ones(new Size(9.0, 9.0), CvType.CV_8U);
        Imgproc.morphologyEx(binaryNagy, binaryNagy, Imgproc.MORPH_CLOSE, kernel222);

        Mat kernel5 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 75));
        Imgproc.morphologyEx(binaryNagy, binaryNagy, Imgproc.MORPH_OPEN, kernel5);

        Mat kernel6 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(80, 5));
        Imgproc.morphologyEx(binaryNagy, binaryNagy, Imgproc.MORPH_OPEN, kernel6);

        Imgproc.dilate(binaryNagy, binaryNagy, Mat.ones(new Size(15.0, 15.0), CvType.CV_8U));
        Imgproc.morphologyEx(binaryNagy, binaryNagy, Imgproc.MORPH_CLOSE, kernel222);

//		Mat kernel7 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(80, 5));
//		Imgproc.morphologyEx(binary, binary, Imgproc.MORPH_OPEN, kernel7);
//
//		Mat kernel8 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(10, 100));
//		Imgproc.morphologyEx(binary, binary, Imgproc.MORPH_OPEN, kernel8);

//		Size size = new Size(3.0, 3.0);
//		Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, size);

        Mat labelsNagy = new Mat();
        Imgproc.connectedComponents(binaryNagy, labelsNagy);

        ArrayList<MatOfPoint> contoursNagy = new ArrayList<>();
        Imgproc.findContours(binaryNagy, contoursNagy, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        TextView textViewNagy;
        textViewNagy = findViewById(R.id.textViewNagy);
        TextView textViewKicsi;
        textViewKicsi = findViewById(R.id.textViewKicsi);
        textViewKicsi.setText("Kicsi: " + kicsi);
        textViewNagy.setText("Nagy: " + contoursNagy.size());

        Imgproc.drawContours(mat, contours, -1, new Scalar(0, 0, 255), 5);
        Imgproc.drawContours(mat, contoursNagy, -1, new Scalar(255, 0, 0), 5);
        Utils.matToBitmap(mat, bitmap);

        float aspectRatio = bitmap.getWidth() /
                (float) bitmap.getHeight();
        int width = imageView.getWidth();
        int height = Math.round(width / aspectRatio);

        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        mat.release();
        gray.release();
        binaryKicsi.release();
        binaryNagy.release();
        labels.release();
        labelsNagy.release();
        contours.clear();
        contoursNagy.clear();
        kernel1.release();
        //kernel2.release();
        kernel5.release();
        kernel6.release();
        kernel11.release();
       // kernel22.release();
        kernel222.release();
        System.gc();

        return bitmap;
    }

}