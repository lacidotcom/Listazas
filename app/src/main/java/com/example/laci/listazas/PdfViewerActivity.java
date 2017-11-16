package com.example.laci.listazas;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class PdfViewerActivity extends AppCompatActivity {

    private StorageReference mStorageRef;

    private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        Intent receivedIntent = getIntent();

        final String pdf_name = receivedIntent.getStringExtra("pdf_name");

        pdfView = (PDFView) findViewById(R.id.pdfView);
        Button readButton = (Button) findViewById(R.id.btn_readPdf);

        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://bolti-koltes.appspot.com").child("/akciok/" + pdf_name + ".pdf");


        mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Log.i("Main", "File uri: " + o.toString());
                Toast.makeText(PdfViewerActivity.this, "Kérem várjon a fájl letöltésére!", Toast.LENGTH_SHORT).show();
                download(o.toString(),pdf_name);
            }
        });

        boolean finished = true;
        do{
            try {

                loadPdf(Uri.parse("file:///storage/sdcard0/Download/" + pdf_name + ".pdf"));
                finished = false;

            } catch (Exception e) {
                finished = true;
            }
        }while (!finished);

        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadPdf(Uri.parse("file:///storage/sdcard0/Download/"+ pdf_name + ".pdf"));
            }
        });
    }

    private void loadPdf(Uri uri){
        pdfView.fromUri(uri).load();
    }

    @Override
    protected void onPause() {
        finish();
        super.onPause();
    }

    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }

    public void download(String pdf_url, String pdf_name)
    {
        //new DownloadFile().execute("gs://bolti-koltes.appspot.com\").child(\"/akciok/tesco.pdf", pdf + ".pdf");
        //new DownloadFile().execute("https://firebasestorage.googleapis.com/v0/b/bolti-koltes.appspot.com/o/akciok%2Ftesco.pdf?alt=media&token=72b39d69-9e0b-4b6b-bdff-77e35be048d2", pdf + ".pdf");
        new DownloadFile().execute(pdf_url, pdf_name+".pdf");
    }

    public class DownloadFile extends AsyncTask<String, Void, Void>{

        String pdf_name;
        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];
            String fileName = strings[1];
            pdf_name = strings[1];
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "Download");
            folder.mkdir();

            File pdfFile = new File(folder, fileName);

            try{
                pdfFile.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            FileDownloader.downloadFile(fileUrl, pdfFile);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(PdfViewerActivity.this, pdf_name + " letöltése kész", Toast.LENGTH_SHORT).show();

        }

    }


}

