package com.example.laci.listazas;

import android.graphics.Rect;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.Camera.Parameters;

import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.ArrayList;

public class FastShoppingActivity extends AppCompatActivity {

    SurfaceView surfaceView;
    TextView tv_text;
    CameraSource cameraSource;
    ListView lv_fastshop;
    NumberPicker numberPicker;
    Button btn_add;
    ArrayList<Integer> items = new ArrayList<Integer>();
    ArrayAdapter<Integer> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fast_shopping);

        surfaceView = (SurfaceView)findViewById(R.id.SurfaceView);

        tv_text = (TextView)findViewById(R.id.tv_text);
        lv_fastshop = (ListView)findViewById(R.id.lv_fastsh);

        numberPicker = (NumberPicker)findViewById(R.id.np_fs);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(100);
        numberPicker.setWrapSelectorWheel(false);

        btn_add = (Button)findViewById(R.id.btn_fs_add);
        adapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_list_item_activated_1,items);
        lv_fastshop.setAdapter(adapter);

        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if(!textRecognizer.isOperational()){
            Toast.makeText(this,"Nem elérhető!", Toast.LENGTH_SHORT).show();
        }else{
            cameraSource = new CameraSource.Builder(getApplicationContext(),textRecognizer)
                    .setAutoFocusEnabled(true)
                    .setFacing(cameraSource.CAMERA_FACING_BACK)
                    .build();
            surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    try{
                        cameraSource.start(surfaceView.getHolder());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    cameraSource.stop();
                }
            });

        }

        textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections) {
                final SparseArray<TextBlock> items = detections.getDetectedItems();
                if (items.size() != 0){
                    tv_text.post(new Runnable() {
                        @Override
                        public void run() {
                            StringBuilder stringBuilder = new StringBuilder();
                            for(int i = 0; i < items.size(); i++){
                                TextBlock item = items.valueAt(i);
                                stringBuilder.append(item.getValue());
                                stringBuilder.append("\n");
                            }
                            String kek = stringBuilder.toString();
                            //Toast.makeText(FastShoppingActivity.this,kek,Toast.LENGTH_SHORT).show();
                            kek = kek.replaceAll("/[^0-9]/g","").replace("\n", "").replace("\r", "").replace(".", "").replace("-", "");
                            tv_text.setText(kek);
                            //tv_text.setText(stringBuilder.toString());
                        }
                    });
                }
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String price = tv_text.getText().toString();
                try {
                    //price = price.replaceAll("[A-z]", "");
                    int val = numberPicker.getValue();
                    int sum = Integer.parseInt(price) * val;
                    //Toast.makeText(FastShoppingActivity.this,sum,Toast.LENGTH_SHORT).show();
                    //items.add(price);
                    //adapter.notifyDataSetChanged();
                    adapter.add(sum);
                }catch (Exception e){
                    Toast.makeText(FastShoppingActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                onTouchEvent(motionEvent);
                return true;
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP) {
            float x =  event.getX();
            float y = event.getY();
            float touchMajor = event.getTouchMajor();
            float touchMinor = event.getTouchMinor();

            Rect touchRect = new Rect((int)(x - touchMajor / 2), (int)(y - touchMinor / 2), (int)(x + touchMajor / 2), (int)(y + touchMinor / 2));

            this.submitFocusAreaRect(touchRect);
        }
        return super.onTouchEvent(event);
    }

    private void submitFocusAreaRect(final Rect touchRect) {
        java.lang.reflect.Field[] declaredFields = CameraSource.class.getDeclaredFields();

        for (java.lang.reflect.Field field : declaredFields) {
            if (field.getType() == Camera.class) {
                field.setAccessible(true);
                try {
                    Camera camera = (Camera) field.get(cameraSource);
                    if (camera != null) {
                        Camera.Parameters cameraParameters = camera.getParameters();

                        if(cameraParameters.getMaxNumFocusAreas() == 0) {
                            return;
                        }

                        Rect focusArea = new Rect();

                        focusArea.set(touchRect.left * 2000 / surfaceView.getWidth() - 1000,
                                touchRect.top * 2000 / surfaceView.getHeight() - 1000,
                                touchRect.right * 2000 / surfaceView.getWidth() - 1000,
                                touchRect.bottom * 2000 / surfaceView.getHeight() - 1000);

                        ArrayList<Camera.Area> focusAreas = new ArrayList<>();
                        focusAreas.add(new Camera.Area(focusArea, 1000));

                        cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                        cameraParameters.setFocusAreas(focusAreas);
                        camera.setParameters(cameraParameters);

                        camera.autoFocus((Camera.AutoFocusCallback) this);
                    }
                } catch (IllegalAccessException | RuntimeException e) {
                    e.getMessage();
                }

                break;
            }
        }

    }

    private static boolean cameraFocus(CameraSource cameraSource, String focusMode){
        java.lang.reflect.Field[] declaredFields = CameraSource.class.getDeclaredFields();
        for (java.lang.reflect.Field field : declaredFields) {
            if (field.getType() == Camera.class) {
                field.setAccessible(true);
                try {
                    Camera camera = (Camera) field.get(cameraSource);
                    if (camera != null) {
                        Camera.Parameters params = camera.getParameters();
                        params.setFocusMode(focusMode);
                        camera.setParameters(params);
                        return true;
                    }

                    return false;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                break;
            }
        }

        return false;

    }
}
