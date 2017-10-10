package com.example.laci.listazas;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class PdfViewerActivity extends AppCompatActivity {

/*
    WebView webview;
    ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        webview = (WebView)findViewById(R.id.webview);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        webview.getSettings().setJavaScriptEnabled(true);
        String filename ="https://firebasestorage.googleapis.com/v0/b/bolti-koltes.appspot.com/o/akciok%2Ftesco_hipermarket_2017-09-28.pdf?alt=media&token=45c4cbac-7ff2-444f-bf77-43ba3dbc2dc8";
        webview.loadUrl("http://docs.google.com/gview?embedded=true&url="+filename);

        webview.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                // do your stuff here
                progressbar.setVisibility(View.GONE);
            }
        });

    }

*/


    private StorageReference mStorageRef;
    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        /*
        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://bolti-koltes.appspot.com").child("/akciok/tesco.pdf");


        mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Log.i("Main", "File uri: " + o.toString());
                new RetrivePDFStream().execute(o.toString());
            }
        });
*/
        pdfView = (PDFView) findViewById(R.id.pdfView);

        new RetrivePDFStream().execute("https://firebasestorage.googleapis.com/v0/b/bolti-koltes.appspot.com/o/akciok%2Ftesco.pdf?alt=media&token=72b39d69-9e0b-4b6b-bdff-77e35be048d2");new RetrivePDFStream().execute("www.pdf995.com/samples/pdf.pdf");

    }

    class RetrivePDFStream extends AsyncTask<String,Void,InputStream>{

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try{
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                if(urlConnection.getResponseCode() == 200){
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            }catch (IOException e){
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            pdfView.fromStream(inputStream).load();
        }
    }



/*
    private PDFView pdfView;

    private static int PICKFILE_RESULT_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        pdfView = (PDFView) findViewById(R.id.pdfView);
        Button readButton = (Button) findViewById(R.id.btn_readPdf);
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                Uri uri = Uri.parse("/Downloads");
                intent.setDataAndType(uri, "*\/*");
                startActivityForResult(intent, PICKFILE_RESULT_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICKFILE_RESULT_CODE){
            Log.d("Result", data.getData().toString());
            loadPdf(data.getData());
        }

    }

    private void loadPdf(Uri uri){
        pdfView.fromUri(uri).load();
    }
*/

}
