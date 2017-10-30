package com.example.laci.listazas;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Name_discount extends AppCompatActivity {

    EditText eT_name, eT_precent;
    Button btn_update_disc;
    DatabaseHelper mDatabasHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_discount);

        mDatabasHelper = new DatabaseHelper(this);

        eT_name = (EditText)findViewById(R.id.eT_name);
        eT_precent = (EditText)findViewById(R.id.eT_precent);
        btn_update_disc = (Button)findViewById(R.id.btn_update_disc);

        btn_update_disc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = eT_name.getText().toString();
                try {
                    int prec = Integer.parseInt(eT_precent.getText().toString());
                    mDatabasHelper.updateDB_discount(name,prec);
                    Intent intent = new Intent(Name_discount.this, ListDataActivity.class);
                    startActivity(intent);
                }catch (Exception e){
                    Toast.makeText(Name_discount.this,"Egész számot adj meg!",Toast.LENGTH_LONG).show();
                }


            }
        });
    }
}
