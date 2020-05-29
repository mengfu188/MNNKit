package com.alibaba.android.mnnkit.demo;


import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.mnnkit.actor.FaceDetector;
import com.alibaba.android.mnnkit.demo.utils.Common;
import com.alibaba.android.mnnkit.entity.FaceDetectionReport;
import com.alibaba.android.mnnkit.entity.MNNFlipType;
import com.alibaba.android.mnnkit.intf.InstanceCreatedListener;
import com.tsia.example.mnnkitdemo.R;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

public class FaceDetectionImageTestActivity extends AppCompatActivity {

    private Paint KeyPointsPaint = new Paint();
    private Paint ScorePaint = new Paint();
    private Paint TextPaint = new Paint();
    private static final String TAG = "FaceDetectionImageTest";

    private SurfaceHolder mDrawSurfaceHolder;

    private FaceDetector mFaceDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detection_image_test);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("人脸检测-图片");
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        KeyPointsPaint.setColor((Color.RED));
        KeyPointsPaint.setStyle(Paint.Style.FILL);
        KeyPointsPaint.setStrokeWidth(2);

        ScorePaint.setColor(Color.RED);
        ScorePaint.setStrokeWidth(2f);
        ScorePaint.setTextSize(40);

        TextPaint.setColor(Color.DKGRAY);
        TextPaint.setStrokeWidth(2f);
        TextPaint.setTextSize(58);

        SurfaceView drawView = findViewById(R.id.points_view);
        drawView.setZOrderOnTop(true);
        drawView.getHolder().setFormat(PixelFormat.TRANSPARENT);
        mDrawSurfaceHolder = drawView.getHolder();

        ImageView imageView = findViewById(R.id.imageView);
        Bitmap bitmap = Common.readImageFromAsset(getAssets(), "face_test.jpg");
        imageView.setImageBitmap(bitmap);
//        imageView.setImageResource(R.mipmap.face_test);

        FaceDetector.FaceDetectorCreateConfig createConfig = new FaceDetector.FaceDetectorCreateConfig();
        createConfig.mode = FaceDetector.FaceDetectMode.MOBILE_DETECT_MODE_IMAGE;
        FaceDetector.createInstanceAsync(this, createConfig, new InstanceCreatedListener<FaceDetector>() {
            @Override
            public void onSucceeded(FaceDetector faceDetector) {
                mFaceDetector = faceDetector;
            }

            @Override
            public void onFailed(int i, Error error) {
                Log.e(Common.TAG, "create face detetector failed: " + error);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

            case R.id.action_start:
                doDetect();
                return true;

            case R.id.action_test:
                Log.d(TAG, "onOptionsItemSelected: select action_test");
//                File file = Environment.getExternalStorageDirectory();
                File file = Environment.getExternalStoragePublicDirectory("facein");
                Log.d(TAG, "onOptionsItemSelected: file " + file);
                File[] list = file.listFiles();
                for(int i = 0; i < list.length; i++){
                    Log.d(TAG, "onOptionsItemSelected: " + list[i]);
                    if(list[i].isFile()){
                        Log.d(TAG, "onOptionsItemSelected: " + Common.read(list[i]));
                    }
                }
                File subFile = new File(file, "file.txt");
                Common.write(subFile, "xxxxx");
                Log.d(TAG, "onOptionsItemSelected: " + Common.read(subFile));
                File dcim = Environment.getExternalStoragePublicDirectory("DCIM");
                String[] suffixs = {".jpg", ".png"};
                List<File> arr = Common.getFileList(dcim, suffixs);
                for(File f:arr){
                    Log.d(TAG, "onOptionsItemSelected: " + f.toString());
                }
            case R.id.action_batch:
                doBatchDetect();


            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    void doBatchDetect() {
        if (mFaceDetector==null) {
            Toast.makeText(this, "正在初始化...", Toast.LENGTH_LONG).show();
            return;
        }

        String directory = ((TextView)findViewById(R.id.path)).getText().toString();
        Log.d(TAG, "doBatchDetect: directory is " + directory);
        File root = Environment.getExternalStoragePublicDirectory(directory);
        Log.d(TAG, "doBatchDetect: root is " + root.toString());
        String[] suffixs = {".jpg", ".png"};
        List<File> arr = Common.getFileList(root, suffixs);
        Log.d(TAG, "doBatchDetect: file list is " + arr.size());
        for(File f: arr){
            Bitmap bitmap = Common.readImageFromFile(f, getContentResolver());
            if(null == bitmap){
                continue;
            }
            Log.d(TAG, "doDetect: " + String.format("width is %d, height is %d", bitmap.getWidth(), bitmap.getHeight()));
            long start = System.currentTimeMillis();
            FaceDetectionReport[] results = mFaceDetector.inference(bitmap, 0, 0, 0, MNNFlipType.FLIP_NONE);

            DrawResult(results, bitmap, System.currentTimeMillis() - start);
            SaveResult(results, f);
        }

//        Bitmap bitmap = Common.readImageFromAsset(getAssets(), "face_test.jpg");
////        Bitmap bitmap = BitmapFactory.decodeResource(FaceDetectionImageTestActivity.this.getResources(), R.mipmap.face_test);
//        Log.d(TAG, "doDetect: " + String.format("width is %d, height is %d", bitmap.getWidth(), bitmap.getHeight()));
//        long start = System.currentTimeMillis();
//        FaceDetectionReport[] results = mFaceDetector.inference(bitmap, 0, 0, 0, MNNFlipType.FLIP_NONE);
//
//        DrawResult(results, bitmap, System.currentTimeMillis() - start);
//        SaveResult(results);
    }

    void doDetect() {
        if (mFaceDetector==null) {
            Toast.makeText(this, "正在初始化...", Toast.LENGTH_LONG).show();
            return;
        }

        Bitmap bitmap = Common.readImageFromAsset(getAssets(), "face_test.jpg");
//        Bitmap bitmap = BitmapFactory.decodeResource(FaceDetectionImageTestActivity.this.getResources(), R.mipmap.face_test);
        Log.d(TAG, "doDetect: " + String.format("width is %d, height is %d", bitmap.getWidth(), bitmap.getHeight()));
        long start = System.currentTimeMillis();
        FaceDetectionReport[] results = mFaceDetector.inference(bitmap, 0, 0, 0, MNNFlipType.FLIP_NONE);

        DrawResult(results, bitmap, System.currentTimeMillis() - start);
        SaveResult(results);
    }

    private void SaveResult(FaceDetectionReport[] reports, File image){
        if (null == reports || reports.length == 0){
            Log.d(TAG, "SaveResult: skip for zero face detect");
            return;
        }

        DecimalFormat format = new DecimalFormat("0.000");
        StringBuilder builder = new StringBuilder();
        builder.append("version: 1\n")
                .append("landmarks: 106\n")
                .append("{\n");
        FaceDetectionReport report = reports[0];
        for (int j=0; j<106; j++) {

            float keyX = report.keyPoints[j*2];
            float keyY = report.keyPoints[j*2 + 1];
            builder.append(format.format(keyX))
                    .append(" ")
                    .append(format.format(keyY))
                    .append("\n");

        }
        builder.append("}");
        File file = Environment.getExternalStoragePublicDirectory("facein");
        File subFile = new File(file, "img.pts");
//        Common.write(subFile, builder.toString());
        Log.d(TAG, "SaveResult: " + builder.toString());
    }

    private void SaveResult(FaceDetectionReport[] reports){
        if (null == reports || reports.length == 0){
            Log.d(TAG, "SaveResult: skip for zero face detect");
            return;
        }
        DecimalFormat format = new DecimalFormat("0.000");
        StringBuilder builder = new StringBuilder();
        builder.append("version: 1\n")
                .append("landmarks: 106\n")
                .append("{\n");
        FaceDetectionReport report = reports[0];
        for (int j=0; j<106; j++) {

            float keyX = report.keyPoints[j*2];
            float keyY = report.keyPoints[j*2 + 1];
            builder.append(format.format(keyX))
                    .append(" ")
                    .append(format.format(keyY))
                    .append("\n");

        }
        builder.append("}");
        File file = Environment.getExternalStoragePublicDirectory("facein");
        File subFile = new File(file, "img.pts");
        Common.write(subFile, builder.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_face_image_test, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void DrawResult(FaceDetectionReport[] reports, Bitmap bitmap, long timeCost) {

        Canvas canvas = null;
        try {
            canvas = mDrawSurfaceHolder.lockCanvas();
            if (canvas == null) {
                return;
            }
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            // 屏幕长宽
            DisplayMetrics metric = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metric);
            int screenW = metric.widthPixels;

            int imgW = bitmap.getWidth();
            int imgH = bitmap.getHeight();

            int previewWidth = screenW;
            int previewHeight  = (int) (screenW*imgH/(float)imgW);

            float kx = ((float) previewWidth)/imgW;
            float ky = (float) previewHeight/imgH;

            // 绘制人脸关键点
            for (int i=0; i<reports.length; i++) {

                FaceDetectionReport report = reports[i];

                for (int j=0; j<106; j++) {

                    float keyX = report.keyPoints[j*2];
                    float keyY = report.keyPoints[j*2 + 1];
                    canvas.drawCircle(keyX * kx, keyY * ky, 4.0f, KeyPointsPaint);
                }

                float left = report.rect.left;
                float top = report.rect.top;
                float right = report.rect.right;
                float bottom = report.rect.bottom;
                canvas.drawLine(left * kx, top * ky,
                        right * kx, top * ky, KeyPointsPaint);
                canvas.drawLine(right * kx, top * ky,
                        right * kx, bottom * ky, KeyPointsPaint);
                canvas.drawLine(right * kx, bottom * ky,
                        left * kx, bottom * ky, KeyPointsPaint);
                canvas.drawLine(left * kx, bottom * ky,
                        left * kx, top * ky, KeyPointsPaint);

                canvas.drawText(report.score+"", left * kx, top * ky-10, ScorePaint);
            }

            canvas.drawText(timeCost+" ms", 20, previewHeight+70, TextPaint);

        } catch (Throwable t) {
            Log.e(Common.TAG, "Draw result error:");
        } finally {
            if (canvas != null) {
                mDrawSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mFaceDetector!=null) {
            mFaceDetector.release();
        }
    }
}
