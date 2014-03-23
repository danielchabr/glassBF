package com.bibliofair.glass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.io.IOException;


public class ScanCodeActivity extends Activity {

    public static final String EXTRA_TEXT = "text";


    ImageScanner scanner;

    Camera.PreviewCallback previewCb = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            //Log.d("ScanCodeActivity", "preview callback");
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();

            //grayscale image
            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);
            int result = scanner.scanImage(barcode);

            if (result != 0) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();

                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {
                    String text = sym.getData();
                    Intent intent = new Intent();
                    intent.putExtra(EXTRA_TEXT, text);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    break;
                }
            }
        }
    };
    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };
    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    private boolean previewing = true;

    static {
        System.loadLibrary("iconv");
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ScanCodeActivity", "1");
        //content view and keep the window on
        setContentView(R.layout.activity_scan_code);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();

        //barcode scanner
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        FrameLayout preview = (FrameLayout) findViewById(R.id.cameraPreview);
        preview.addView(mPreview);
        Log.e("ScanCodeActivity", "2");
    }

    @Override
    public void onStart() {
        super.onStart();
        //acquireWakeLock();
    }

    @Override
    public void onStop() {
        //releaseWakeLock();
        super.onStop();
    }

    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void releaseCamera() {
        Log.d("ScanCodeActivity", "releaseCamera");
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;
        private Camera mCamera;
        private Camera.PreviewCallback previewCallback;
        private Camera.AutoFocusCallback autoFocusCallback;

        public CameraPreview(Context context, Camera camera,
                             Camera.PreviewCallback previewCb,
                             Camera.AutoFocusCallback autoFocusCb) {
            super(context);
            mCamera = camera;
            previewCallback = previewCb;
            autoFocusCallback = autoFocusCb;

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);

            // deprecated setting, but required on Android versions prior to 3.0
            //mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, now tell the camera where to draw the preview.
            try {
                //Log.d("ScanCodeActivity", (mCamera == null);
                //Log.d("ScanCodeActivity", holder == null);
                mCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                Log.d("DBG", "Error setting camera preview: " + e.getMessage());
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // Camera preview released in activity
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        /*
         * If your preview can change or rotate, take care of those events here.
         * Make sure to stop the preview before resizing or reformatting it.
         */
            Log.e("ScanCodeActivity", "surfaceChanged called");
            if (mHolder.getSurface() == null) {
                // preview surface does not exist
                return;
            }

            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                // ignore: tried to stop a non-existent preview
            }

            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.setPreviewCallback(previewCallback);

                //set params
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setPreviewSize(640, 360);
                parameters.setPreviewFpsRange(30000, 30000);
                mCamera.setParameters(parameters);

                //start the preview && auto focus
                mCamera.startPreview();
                mCamera.autoFocus(autoFocusCallback);
            } catch (Exception e) {
                Log.d("DBG", "Error starting camera preview: " + e.getMessage());
            }
        }
    }
}
