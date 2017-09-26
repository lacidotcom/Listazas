package com.example.laci.listazas;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Laci on 2017. 03. 29..
 */

public class EditDataActivity extends AppCompatActivity {

    private static final String TAG = "EditDataActivity";

    private Button btnSave,btnDelete;
    private EditText name, barcode, piece, price;

    DatabaseHelper mDatabaseHelper;

    private String selectedName,selectedBarcode;
    private int selectedID;
    private float selectedPiece;
    private int selectedPrice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_data_layout);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        name = (EditText) findViewById(R.id.edit_eT_name);
        barcode = (EditText) findViewById(R.id.edit_eT_barcode);
        piece = (EditText) findViewById(R.id.edit_eT_piece);
        price = (EditText) findViewById(R.id.edit_eT_price);
        mDatabaseHelper = new DatabaseHelper(this);

        Intent receivedIntent = getIntent();

        selectedID = receivedIntent.getIntExtra("id",-1);
        selectedName = receivedIntent.getStringExtra("name");
        selectedBarcode = receivedIntent.getStringExtra("barcode");
        selectedPiece = receivedIntent.getFloatExtra("piece",0);
        selectedPrice = receivedIntent.getIntExtra("price",0);

        name.setText(selectedName);
        barcode.setText(selectedBarcode);
        piece.setText(String.valueOf(selectedPiece));
        price.setText(String.valueOf(selectedPrice));

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String item = name.getText().toString();
                String bc = barcode.getText().toString();
                float darab = Float.parseFloat(piece.getText().toString());
                int ar = (int)Float.parseFloat(price.getText().toString());
                if(!item.equals("")){
                    mDatabaseHelper.updateDB(item,selectedID,selectedName,bc,darab,ar);
                    toastMessage("Sikeres szerkesztés");
                    Intent intent = new Intent(EditDataActivity.this, ListDataActivity.class);
                    startActivity(intent);
                }else{
                    toastMessage("Meg kell adnod egy nevet!");
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabaseHelper.deleteName(selectedID,selectedName);
                name.setText("");
                barcode.setText("");
                piece.setText("");
                price.setText("");
                toastMessage("Törölve a listából");
                Intent intent = new Intent(EditDataActivity.this, ListDataActivity.class);
                startActivity(intent);
            }
        });
    }

    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
