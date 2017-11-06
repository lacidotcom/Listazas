package com.example.laci.listazas;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;

public class DiscountAddActivity extends MainActivity {

    private static final String TAG = "DscountedAddActivity";

    NumberPicker np_pay, np_get;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount_add);

        np_pay = (NumberPicker)findViewById(R.id.np_pay);
        np_pay.setMinValue(1);
        np_pay.setMaxValue(100);
        np_pay.setWrapSelectorWheel(false);

        np_get = (NumberPicker)findViewById(R.id.np_get);
        np_get.setMinValue(2);
        np_get.setMaxValue(100);
        np_get.setWrapSelectorWheel(false);

        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);


        //setContentView(R.layout.activity_main);
        //editText = (EditText) findViewById(R.id.editText);
        editText = (AutoCompleteTextView) findViewById(R.id.editText);
        eT_barcode = (EditText) findViewById(R.id.editText_vonalkod);
        eT_piece = (EditText) findViewById(R.id.editText_darab);
        eT_price = (EditText) findViewById(R.id.editText_darabar);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnViewData = (Button) findViewById(R.id.btnView);
        mDatabaseHelper = new DatabaseHelper(this);
        btn_barcode_scan = (Button) findViewById(R.id.btn_barcode_scan);

        SuggestName();

        editText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckName();
            }
        });

        btn_barcode_scan.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(DiscountAddActivity.this);
                integrator.initiateScan();
            }

        });

        btnAdd.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                //numberpicker
                int pay = np_pay.getValue();
                int get = np_get.getValue();
                if(get > pay) {

                    //CheckName();
                    String newEntry = editText.getText().toString();
                    String entry_vonalK = eT_barcode.getText().toString();
                    int entry_darab = np_get.getValue();
                    /*
                    if ("".equals(eT_piece.getText().toString())) {
                        entry_darab = 0;
                    } else {
                        entry_darab = Float.parseFloat(eT_piece.getText().toString());
                    }
                    */
                    float entry_darab_ar = 0;
                    if ("".equals(eT_price.getText().toString())) {
                        entry_darab_ar = 0;
                    } else {
                        entry_darab_ar = Integer.parseInt(eT_price.getText().toString());
                        entry_darab_ar = (int)(entry_darab_ar * (pay/(float)get));
                    }
                    if (editText.length() != 0 && entry_vonalK.length() != 0 && entry_darab > 0 && entry_darab_ar > 0) {
                        AddData(newEntry, entry_vonalK, entry_darab, entry_darab_ar);
                        editText.setText("");
                        eT_barcode.setText("");
                        eT_piece.setText("");
                        eT_price.setText("");

                    } else {
                        Toast.makeText(DiscountAddActivity.this, "Muszáj minden mezőt kitölteni", Toast.LENGTH_LONG).show();
                        Log.d(TAG,entry_darab+"");
                        Log.d(TAG,entry_darab_ar+"");
                    }
                }else{
                    Toast.makeText(DiscountAddActivity.this, "Ne fizess többet, mint amennyit kapsz!", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnViewData.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DiscountAddActivity.this, ListDataActivity.class);
                startActivity(intent);
                mDatabaseHelper.allprice();
            }
        });

    }


}
