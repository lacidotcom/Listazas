package com.example.laci.listazas;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * Created by Laci on 2017. 03. 29..
 */

public class ListDataActivity extends AppCompatActivity {

    private static final String TAG = "ListDataActivity";

    DatabaseHelper mDatabasHelper;

    private ListView mListView;
    private SimpleCursorAdapter dataAdapter;
    private Button btn_add;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        mListView = (ListView) findViewById(R.id.listView);
        mDatabasHelper = new DatabaseHelper(this);
        btn_add = (Button) findViewById(R.id.btn_hozzaad);

        //plvw();
        //populateListView();
        ListAllItems();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    private void ListAllItems() {

        TextView Osszeg = (TextView) findViewById(R.id.tv_all_price);
        int Osszeg_from_db = mDatabasHelper.getOsszar();
        Osszeg.setText("Összeg:                     " + Osszeg_from_db + " Ft");
        mDatabasHelper.allprice();

        Cursor cursor = mDatabasHelper.getAllRows();
        String[] columns = mDatabasHelper.SOME_KEYS;
        int[] to = new int[]{
                R.id.tv_nev,
                R.id.tv_barcode,
                R.id.tv_piece,
                R.id.tv_ar
        };


        dataAdapter = new SimpleCursorAdapter(this, R.layout.custom_row, cursor, columns, to, 0);
        mListView.setAdapter(dataAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                view.findFocus();

                String name = ((TextView)view.findViewById(R.id.tv_nev)).getText().toString();

                Cursor data = mDatabasHelper.getItemID(name);
                int itemID = -1;
                String barcode = "";
                float piece = 0;
                int price = 0;
                while (data.moveToNext()) {
                    itemID = data.getInt(0);
                    barcode = ((TextView)view.findViewById(R.id.tv_barcode)).getText().toString();
                    piece = Float.parseFloat(((TextView)view.findViewById(R.id.tv_piece)).getText().toString());
                    price =(int)(Float.parseFloat(((TextView)view.findViewById(R.id.tv_ar)).getText().toString())/piece);
                }
                if (itemID > -1) {
                    Intent editScreenIntent = new Intent(ListDataActivity.this, EditDataActivity.class);
                    editScreenIntent.putExtra("id", itemID);
                    editScreenIntent.putExtra("name", name);
                    editScreenIntent.putExtra("barcode", barcode);
                    editScreenIntent.putExtra("piece", piece);
                    editScreenIntent.putExtra("price", price);

                    startActivity(editScreenIntent);
                } else {
                    toastMessage("Nincs ilyen nevű termék");
                }
            }
        });
    }

    public void onClick(View view) {
        Intent intent = new Intent(ListDataActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("ListData Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


}
