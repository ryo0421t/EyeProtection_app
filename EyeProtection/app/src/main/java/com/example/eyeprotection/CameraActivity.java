/* カメラ画面 */
/* 説明
 * カメラ機能と目認識 */
package com.example.eyeprotection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class CameraActivity extends AppCompatActivity implements ImageAnalysis.Analyzer {
    // カメラ
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    // 顔の検知
    private FaceDetector faceDetector;
    // 顔の部位をリスト化
    private List<View> pointViews = new ArrayList<>();

    // 最後に取得した時間
    private long lastGetTime = 0;

    // 適正な距離の回数
    private int eyeCountGood = 0;
    // 近づいた回数
    private int eyeCountNg = 0;

    // カメラプレビュー
    private PreviewView previewView;

    //  通知テキストビュー
    private TextView notificationTV;
    // 目の距離適性テキストビュー
    private TextView goodTV;
    // 目の距離不適正テキストビュー
    private TextView ngTV;
    //  タイマーのカウント
    private Timer timer;
    //  通知音のダウンロード
    private SoundPool soundPool;
    //  近づいた時の通知音
    private int soundID1;
    //  10秒ごとの通知音
    private int soundID2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // カメラを画面上に表示するためのPreviewView
        previewView = findViewById(R.id.previewView);

        //  目の距離適性を表示するためのTextView
        goodTV = findViewById(R.id.goodTextView);
        // 目の距離不適性を表示するためのTextView
        ngTV = findViewById(R.id.ngTextView);
        //  通知を表示するためのTextView
        notificationTV = findViewById(R.id.notificationTextView);
        //  タイマーの起動
        startTimer();
        // SoundPoolの初期化と音声ファイルのロード
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        soundID1 = soundPool.load(this, R.raw.maou_se_system47, 1);
        soundID2 = soundPool.load(this, R.raw.sanjuu, 1);

        // カメラ起動用
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, getExecutor());

        //顔認証設定
        FaceDetectorOptions options =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .setMinFaceSize(0.15f)
                        .enableTracking()
                        .build();
        faceDetector = FaceDetection.getClient(options);

        // 終了ボタンを押したとき
        findViewById(R.id.endButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 画面遷移　現在の画面→結果画面
                        Intent intent = new Intent(CameraActivity.this, ResultActivity.class);
                        // 値の受け渡し ("受取先に使う名前", 格納する値);
                        intent.putExtra("EyeCountGood", eyeCountGood);
                        intent.putExtra("EyeCountNg", eyeCountNg);
                        // タイマー停止
                        stopTimer();
                        // 結果画面起動
                        startActivity(intent);
                    }
                }
        );
    }

    Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }


    // カメラ起動
    @SuppressLint("RestrictedApi")
    private void startCameraX(ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();
        Preview preview = new Preview.Builder()
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();
        imageAnalysis.setAnalyzer(getExecutor(), this);

        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageAnalysis);
    }


    // 顔検知
    @SuppressLint("UnsafeOptInUsageError")
    @Override
    public void analyze(@NonNull ImageProxy imageProxy) {
        int rotationDegrees = degreesToFirebaseRotation(imageProxy.getImageInfo().getRotationDegrees());

        InputImage image =
                InputImage.fromMediaImage(imageProxy.getImage(), rotationDegrees);

        faceDetector.process(image)
                .addOnSuccessListener(
                        new OnSuccessListener<List<Face>>() {
                            @Override
                            public void onSuccess(List<Face> faces) {
                                //  顔を認識できなかった時処理
                                String notFace = "顔が認識できません";
                                if (faces.isEmpty()) {
                                    notificationTV.setText(notFace);
                                } else {
                                    // Process the detected faces
                                    processFaces(faces);
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                e.printStackTrace();
                            }
                        })
                .addOnCompleteListener(
                        new OnCompleteListener<List<Face>>() {
                            @Override
                            public void onComplete(@NonNull Task<List<Face>> task) {
                                // Close the imageProxy after the analysis is complete
                                imageProxy.close();
                            }
                        });
    }


    // 顔の部位を検知
    private void processFaces(List<Face> faces) {
        for (View pointView : pointViews) {
            previewView.removeView(pointView);
        }
        pointViews.clear();

        for (Face face : faces) {
            Rect bounds = face.getBoundingBox();

            // 目の座標
            // 左目の座標
            float leftEyeX = face.getLandmark(FaceLandmark.LEFT_EYE).getPosition().x;
            float leftEyeY = face.getLandmark(FaceLandmark.LEFT_EYE).getPosition().y;

            // 右目の座標
            float rightEyeX = face.getLandmark(FaceLandmark.RIGHT_EYE).getPosition().x;
            float rightEyeY = face.getLandmark(FaceLandmark.RIGHT_EYE).getPosition().y;

            // 両目の距離（px）
            float distance = rightEyeX - leftEyeX;

            // テキストの表示
            String good = " 適正距離です";
            String ng = " 画面から離れてください";

            // ログに左目と右目の距離を描く
            Log.d("eyeDistance", String.valueOf(rightEyeX - leftEyeX));
//            eyeDistanceTV.setText("左目：" + leftEyeX + "\n右目：" + rightEyeX + "\n距離差：" + distance);

            // 現在の時間を取得
            long currentTime = System.currentTimeMillis();
            // 現在の時間が最後にゲットした時間から5秒立っていて、目の距離が101以上になったら
            if (currentTime - lastGetTime >= 5000 && distance > 100) {
                // notificationTextView"画面から離れてください"と表示
                notificationTV.setText(ng);
                // 現在の時間を最後の時間に入れる
                lastGetTime = currentTime;
                // Ngカウントをインクリメント
                eyeCountNg++;
                // 音を鳴らす
                soundPool.play(soundID1, 1.0f, 1.0f, 1, 0, 1.0f);
            } else if (currentTime - lastGetTime >= 5000 && distance <= 100) {
                // notificationTextView"適切な距離です"と表示
                notificationTV.setText(good);
                // 現在の時間を最後の時間に入れる
                lastGetTime = currentTime;
                // Goodカウントをインクリメント
                eyeCountGood++;
            }
            goodTV.setText(String.valueOf("近い：" + eyeCountNg));
            ngTV.setText(String.valueOf("適正：" + eyeCountGood));

            // 点の位置
            Log.d("FaceDetection", "Bounds: " + bounds.toString()
                    + "\nleftEye X：" + leftEyeX + " Y：" + leftEyeY
                    + "\nrightEye X：" + rightEyeX + " Y：" + rightEyeY
            );
        }
    }

    //  最初に表示しない
    boolean first = true;

    //  10秒ごとにタイマーでテキストと音で通知
    private void startTimer() {
        // タイマーを開始して30分ごとにテキストを更新
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //  1回目の表示しない
                        if (first) {
                            first = false;
                            return;
                        } else {
                            // 2回目以降の表示
                            notificationTV.setText("目を休めましょう");
                            soundPool.play(soundID2, 1.0f, 1.0f, 1, 0, 1.0f);
                        }
                    }
                });
            }
        }, 0, 1800000);   //30分＝1800000
    }

    //  タイマーの停止
    private void stopTimer() {
        // MainActivityから受け取ったタイマーを停止
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    // 端末の傾きを検知
    private int degreesToFirebaseRotation(int degrees) {
        switch (degrees) {
            case 0:
                return 0;
            case 90:
                return 90;
            case 180:
                return 180;
            case 270:
                return 270;
            default:
                throw new IllegalArgumentException("Invalid degrees value");
        }
    }
}