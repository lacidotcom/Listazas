package com.example.laci.listazas;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.util.AsyncListUtil;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    DatabaseHelper mDatabaseHelper;
    private Button btnAdd, btnViewData, btn_barcode_scan;
    private EditText eT_barcode, eT_piece,eT_price;
    private AutoCompleteTextView editText;


    private Toolbar myToolbar;
    private ArrayList<String> ItemNames = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
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
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.initiateScan();
            }

        });

        btnAdd.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                //CheckName();
                String newEntry = editText.getText().toString();
                String entry_vonalK = eT_barcode.getText().toString();
                float entry_darab = 0;
                if("".equals(eT_piece.getText().toString())){
                    entry_darab = 0;
                }else{
                    entry_darab = Float.parseFloat(eT_piece.getText().toString());
                }
                float entry_darab_ar = 0;
                if("".equals(eT_price.getText().toString())){
                    entry_darab_ar = 0;
                }else{
                    entry_darab_ar = Integer.parseInt(eT_price.getText().toString());
                }
                if(editText.length() != 0 && entry_vonalK.length() != 0 && entry_darab > 0 && entry_darab_ar > 0){
                    AddData(newEntry,entry_vonalK,entry_darab,entry_darab_ar);
                    editText.setText("");
                    eT_barcode.setText("");
                    eT_piece.setText("");
                    eT_price.setText("");

                }else{
                    Toast.makeText(MainActivity.this,"Muszáj minden mezőt kitölteni",Toast.LENGTH_LONG).show();
                }
            }
        });

        btnViewData.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ListDataActivity.class);
                startActivity(intent);
                mDatabaseHelper.allprice();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,intent);
        if(scanResult != null){
            String re = scanResult.getContents();
            eT_barcode.setText(re);
            Cursor cursor = mDatabaseHelper.fetchAll();
            if(cursor.moveToFirst()){
                do{
                    String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALL_KEYS[1]));
                    String bc = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALL_KEYS[2]));
                    String piece = "1";
                    String price = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALL_KEYS[4]));
                    if(bc.equals(re)){
                        editText.setText(name);
                        eT_piece.setText(piece);
                        eT_price.setText(price);
                    }
                }while(cursor.moveToNext());
            }
        }
    }

    public void AddData(String newEntry, String entry_vonalK, float entry_darab, float entry_darab_ar){
        boolean insertData = mDatabaseHelper.addData(newEntry,entry_vonalK,entry_darab,entry_darab_ar);
        if(insertData){
            Toast.makeText(MainActivity.this,"Sikeresen hozzaadva",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(MainActivity.this,"Hiba történt hozzáadáskor",Toast.LENGTH_LONG).show();
        }
        if(!Exist_in_fix(newEntry)) {
            insertData = mDatabaseHelper.addData_FIX(newEntry, entry_vonalK, entry_darab, entry_darab_ar);
            if(insertData){
                Toast.makeText(MainActivity.this,"Sikeresen hozzáadva",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(MainActivity.this,"Hiba történt hozzáadáskor",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.shops) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void SuggestName(){
        //AutoComplete
        Cursor cursor_autoc = mDatabaseHelper.getAllRows_FIX();
        if(cursor_autoc.moveToFirst()){
            do{
                String name = cursor_autoc.getString(cursor_autoc.getColumnIndex(DatabaseHelper.ALL_KEYS[1]));
                ItemNames.add(name);
            }while(cursor_autoc.moveToNext());
        }

        if(ItemNames != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, ItemNames);
            editText.setThreshold(1);
            editText.setAdapter(adapter);
        }else{
            Toast.makeText(MainActivity.this,"Hiba ItemNames",Toast.LENGTH_LONG).show();
        }
        //CheckName();
    }

    public void CheckName(){
        Cursor cursor_autoc = mDatabaseHelper.getAllRows_FIX();
        String name_sug = editText.getText().toString();
        //Toast.makeText(MainActivity.this,"Itt a hiba",Toast.LENGTH_LONG).show();
            if(cursor_autoc.moveToFirst()){
                do{
                    String name = cursor_autoc.getString(cursor_autoc.getColumnIndex(DatabaseHelper.ALL_KEYS[1]));
                    String bc = cursor_autoc.getString(cursor_autoc.getColumnIndex(DatabaseHelper.ALL_KEYS[2]));
                    String piece = "1";
                    String price = cursor_autoc.getString(cursor_autoc.getColumnIndex(DatabaseHelper.ALL_KEYS[4]));
                    if(name.equals(name_sug)){
                        eT_barcode.setText(bc);
                        eT_piece.setText(piece);
                        eT_price.setText(price);
                    }
                }while(cursor_autoc.moveToNext());
            }

    }

    public boolean Exist_in_fix(String name){
        Cursor cursor = mDatabaseHelper.getAllRows_FIX();
        if(cursor.moveToFirst()){
            do{
                String db_name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALL_KEYS[1]));
                if (name.equals(db_name))
                    return true;
            }while (cursor.moveToNext());
        }
        return false;
    }


}
