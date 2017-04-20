package com.download.nrl_download_file;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.download.nrl_download_file.android_gcm.RegistrationIntentService;
import com.download.nrl_download_file.fragment.Fragment_about;
import com.download.nrl_download_file.fragment.Fragment_home;
import com.download.nrl_download_file.fragment.Fragment_record;
import com.download.nrl_download_file.fragment.Fragment_setting;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,OnTabSelectListener {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    Button download,dialog_btn;
    BottomBar bottomBar;
    public static String TAG="MainActivity";
    private Fragment_home fragment_home;
    private Fragment_record fragment_record;
    private Fragment_about fragment_about;
    private Fragment_setting fragment_setting;
    private FragmentManager fragmentManager;
    private FragmentTransaction trans;
    private Dialog dialog;
    Process p;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        create_fragment();
        defineUI();
        alert();
        if(RootUtil.isDeviceRooted()){
            Log.d(TAG,"Rooted");
        }else{
            Log.d(TAG,"not root");
        }
        if (checkPlayServices()) {
             //Start IntentService to register this application with GCM.
            Log.d(TAG,"go GCM");
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
        execute();
    }
    public void defineUI(){
        download=(Button)findViewById(R.id.dowload);
        download.setOnClickListener(this);
//        dialog_btn=(Button)findViewById(R.id.dialog);
//        dialog_btn.setOnClickListener(this);
        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(this);
    }
    public void alert(){
        Bundle extras = getIntent().getExtras();
        Log.d(TAG,"get extra");
        if(extras!=null){
            Log.d(TAG,"extra is not null");
            String title=extras.getString("title");
            String content=extras.getString("content");
            if(title!=null&&content!=null){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(title);
                builder.setMessage(content);
                builder.setIcon(R.drawable.ic_stat_ic_notification);
                builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                    }

                });
                dialog = builder.create();
                dialog.getWindow().getAttributes().windowAnimations = R.style.dailoganimation;
                dialog.show();
            }
        }

    }
    public void create_fragment(){
        fragment_home=Fragment_home.newInstance("fragment","home");
        fragment_record=Fragment_record.newInstance("fragment","record");
        fragment_about=Fragment_about.newInstance("fragment","about");
        fragment_setting=Fragment_setting.newInstance("fragment","setting");
        fragmentManager=getSupportFragmentManager();

//        trans.add(R.id.fragment,fragment_home);
//        trans.add(R.id.fragment,fragment_record);
//        trans.add(R.id.fragment,fragment_about);
//        trans.add(R.id.fragment,fragment_setting);
//        trans.commit();
    }
    @Override
    public void onTabSelected(@IdRes int tabId) {
        switch (tabId){
            case R.id.tab_home:
                Log.d(TAG,"Home");
                trans=fragmentManager.beginTransaction();
                trans.replace(R.id.fragment,fragment_home);
                trans.addToBackStack(null);
                trans.commit();
                break;
            case R.id.tab_record:
                Log.d(TAG,"Record");
                trans=fragmentManager.beginTransaction();
                trans.replace(R.id.fragment,fragment_record);
                trans.addToBackStack(null);
                trans.commit();
                break;
            case R.id.tab_about:
                Log.d(TAG,"About");
                trans=fragmentManager.beginTransaction();
                trans.replace(R.id.fragment,fragment_about);
                trans.addToBackStack(null);
                trans.commit();
                break;
            case R.id.tab_setting:
                Log.d(TAG,"Setting");
                trans=fragmentManager.beginTransaction();
                trans.replace(R.id.fragment,fragment_setting);
                trans.addToBackStack(null);
                trans.commit();
                break;
        }


    }
    private boolean checkPlayServices() {
        GoogleApiAvailability googleapi= GoogleApiAvailability.getInstance();
        int resultCode = googleapi.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleapi.isUserResolvableError(resultCode)) {
                googleapi.getErrorDialog(this,resultCode,PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    public void execute(){
        String link,mkdir;
        String cmd ="mount -o rw,remount rootfs / \n";
        String cmd2="mount -o rw,remount /data \n";
        File tmp=new File("/amds/");
        if(!tmp.exists()){
            Log.d(TAG,"not exist");
            link="ln -s /data/data/com.download.nrl_download_file amds";
        }else{
            link=" ";
        }
        try {

            p = Runtime.getRuntime().exec("su");
            DataOutputStream dos = new DataOutputStream(p.getOutputStream());
            dos.writeBytes(cmd);
            dos.flush();
            dos.writeBytes(cmd2);
            dos.flush();
            dos.writeBytes(link);
            dos.flush();
            dos.close();
//                    BufferedReader reader = new BufferedReader(
//                            new InputStreamReader(p.getInputStream()));
//                    char[] buffer = new char[4096];
//                    int read;
//                    StringBuffer output = new StringBuffer();
//                    while ((read = reader.read(buffer)) > 0) {
//                        output.append(buffer, 0, read);
//                    }
//                    reader.close();
            try {
                p.waitFor();
                if (p.exitValue() != 255) {
                    // TODO Code to run on success
                    Toast.makeText(MainActivity.this,"root",Toast.LENGTH_LONG).show();
                }
                else {
                    // TODO Code to run on unsuccessful
                    Toast.makeText(MainActivity.this,"run on unsuccessful",Toast.LENGTH_LONG).show();
                }
            } catch (InterruptedException e) {
                // TODO Code to run in interrupted exception
                Toast.makeText(MainActivity.this,"run in interrupted exception",Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            // TODO Code to run in input/output exception
            Toast.makeText(MainActivity.this,"input/output exception",Toast.LENGTH_LONG).show();
        }
        //---------------------------check files have been download
        File monitor_file=new File("/amds/monitor.bin");
        File post_file=new File("/amds/post.bin");

        if(!monitor_file.exists()&&!post_file.exists()){
            Log.d(TAG,"monitor_file and post_file not exist");

            DownloadMonitor downloadmonitor = new DownloadMonitor(MainActivity.this);
            downloadmonitor.execute();
        }else{
            try{
                Log.d(TAG,"monitor.bin post.bin exist");
                Process p = Runtime.getRuntime().exec("su");
                String cd ="cd /amds/\n";
                //String cmd2 ="chmod 777 monitor.bin && chmod 777 post.bin\n";
                String cmd3 ="/amds/monitor.bin\n";
                //String cmd4="exit\n";
                DataOutputStream dos = new DataOutputStream(p.getOutputStream());
                dos.writeBytes(cd);
                //dos.writeBytes(cmd2);
                dos.writeBytes(cmd3);
                //dos.writeBytes(cmd4);
                dos.flush();
                dos.close();
//                        BufferedReader reader = new BufferedReader(
//                                new InputStreamReader(process.getInputStream()));
//                        char[] buffer = new char[4096];
//                        int read;
//                        StringBuffer output = new StringBuffer();
//                        while ((read = reader.read(buffer)) > 0) {
//                            output.append(buffer, 0, read);
//                        }
//                        reader.close();
                // Log.d(TAG,"Execute:"+output.toString());
                //process.destroy();
            }catch (IOException e){
                Toast.makeText(MainActivity.this,"input/output exception",Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dowload:

                break;
//            case R.id.dialog:
//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                builder.setTitle("123");
//                builder.setMessage("456");
//                builder.setIcon(R.drawable.ic_stat_ic_notification);
//                builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface arg0, int arg1) {
//                        // TODO Auto-generated method stub
//                    }
//
//                });
//                dialog = builder.create();
//                dialog.getWindow().getAttributes().windowAnimations = R.style.dailoganimation;
//                dialog.show();
//                break;
        }


    }



    private class DownloadMonitor extends AsyncTask<String, Integer, String> {
        private Context context;
        private PowerManager.WakeLock mWakeLock;
        public DownloadMonitor(Context context) {
            this.context = context;
        }
        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpsURLConnection connection = null;
            try {
                URL url = new URL("https://amds.nrl.mcu.edu.tw:8080/download");
                InputStream instream = getResources().openRawResource(R.raw.amds);
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                try {
                    trustStore.load(instream, null);
                } catch (NoSuchAlgorithmException e) {
                    // TODO Auto-generated catch block
                    Log.d(TAG, "NoSuchAlgorithmException");
                    e.printStackTrace();
                } catch (CertificateException e) {
                    // TODO Auto-generated catch blockß
                    Log.d(TAG, "CertificateException");
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    Log.d(TAG, "IOException");
                    e.printStackTrace();
                } finally {
                    instream.close();
                }
                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                tmf.init(trustStore);
                SSLContext context = SSLContext.getInstance("TLS");
                context.init(null, tmf.getTrustManagers(), null);
                connection = (HttpsURLConnection) url.openConnection();
                connection.setSSLSocketFactory(context.getSocketFactory());
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("type", "monitor"));
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(params));
                writer.flush();
                writer.close();
                os.close();
                connection.connect();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }
                int fileLength = connection.getContentLength();
                String filetype=connection.getContentType();
                Log.d("MainActivity","File type:"+filetype);
                Log.d("MainActivity","File size:"+fileLength);
                //File data_path = Environment.getExternalStorageDirectory();
                String data_path="/amds/";
                input = connection.getInputStream();
                Log.d("MainActivity","InputStream::"+input);
                Log.d("TAG","PATH:"+data_path);
                output = new FileOutputStream(data_path+"monitor.bin");
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count=input.read(data)) !=-1) {
                    Log.d("MainActivity","Monitor file reading");
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    if (fileLength > 0)
                        //publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
                Log.d("MainActivity","done while");
            } catch (Exception e) {
                Log.d("MainActivity",e.toString());
                return e.toString();
            } finally {
                Log.d("MainActivity","finally");
            }

            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null) {
                connection.disconnect();
            }
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            if (result != null) {
                Toast.makeText(context, "monitor.bin download error: " + result, Toast.LENGTH_LONG).show();
                Log.d(TAG,"Eorror:"+result);
            }
            else{

                final DownloadPost downloadpost=new DownloadPost(MainActivity.this);
                downloadpost.execute();


            }
        }
    }
    //----------------------download post.bin
    private class DownloadPost extends AsyncTask<String, Integer, String> {
        private Context context;
        private PowerManager.WakeLock mWakeLock;
        public DownloadPost(Context context) {
            this.context = context;
        }
        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpsURLConnection connection = null;
            try {
                URL url = new URL("https://amds.nrl.mcu.edu.tw:8080/download");
                InputStream instream = getResources().openRawResource(R.raw.amds);
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                try {
                    trustStore.load(instream, null);
                } catch (NoSuchAlgorithmException e) {
                    // TODO Auto-generated catch block
                    Log.d(TAG, "NoSuchAlgorithmException");
                    e.printStackTrace();
                } catch (CertificateException e) {
                    // TODO Auto-generated catch block
                    Log.d(TAG, "CertificateException");
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    Log.d(TAG, "IOException");
                    e.printStackTrace();
                } finally {
                    instream.close();
                }
                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                tmf.init(trustStore);
                SSLContext context = SSLContext.getInstance("TLS");
                context.init(null, tmf.getTrustManagers(), null);
                connection = (HttpsURLConnection) url.openConnection();
                connection.setSSLSocketFactory(context.getSocketFactory());
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("type", "post"));
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(params));
                writer.flush();
                writer.close();
                os.close();
                connection.connect();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }
                int fileLength = connection.getContentLength();
                String filetype=connection.getContentType();
                Log.d("MainActivity","File type:"+filetype);
                Log.d("MainActivity","File size:"+fileLength);
                //File data_path = Environment.getExternalStorageDirectory();
                String data_path="/amds/";
                input = connection.getInputStream();
                Log.d("MainActivity","InputStream::"+input);
                Log.d("TAG","PATH:"+data_path);
                output = new FileOutputStream(data_path+"post.bin");
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count=input.read(data)) !=-1) {
                    Log.d("MainActivity","Post file reading");
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    if (fileLength > 0)
                        //publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
                Log.d("MainActivity","done while");
            } catch (Exception e) {
                Log.d("MainActivity",e.toString());
                return e.toString();
            } finally {
                Log.d("MainActivity","finally");
            }

            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null) {
                connection.disconnect();
            }
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            //post_ProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);

        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            if (result != null) {
                Toast.makeText(context, "Post.bin download error: " + result, Toast.LENGTH_LONG).show();
                Log.d(TAG,"Eorror:"+result);
            }
            else{
                Toast.makeText(context, "File downloaded comlpete", Toast.LENGTH_SHORT).show();
                try {
                    Process p = Runtime.getRuntime().exec("su");
                    String cmd ="cd /amds/\n";
                    String cmd2 ="chmod 777 monitor.bin && chmod 777 post.bin\n";
                    String cmd3 ="/amds/monitor.bin\n";
                    //String cmd4="exit\n";
                    DataOutputStream dos = new DataOutputStream(p.getOutputStream());
                    dos.writeBytes(cmd);
                    dos.writeBytes(cmd2);
                    dos.writeBytes(cmd3);
                    //dos.writeBytes(cmd4);
                    dos.flush();
                    dos.close();
//                    BufferedReader reader = new BufferedReader(
//                            new InputStreamReader(p.getInputStream()));
//                    char[] buffer = new char[4096];
//                    int read;
//                    StringBuffer output = new StringBuffer();
//                    while ((read = reader.read(buffer)) > 0) {
//                        output.append(buffer, 0, read);
//                    }
//                    reader.close();
//                    try {
//                        p.waitFor();
//                        //Log.d(TAG,"Execute:"+output.toString());
//                        if (p.exitValue() != 255) {
//                            // TODO Code to run on success
//                            Toast.makeText(MainActivity.this,"root!!!!",Toast.LENGTH_LONG).show();
//                            //p.destroy();
//                        }
//                        else {
//                            // TODO Code to run on unsuccessful
//                            Toast.makeText(MainActivity.this,"run on unsuccessful",Toast.LENGTH_LONG).show();
//                        }
//                    } catch (InterruptedException e) {
//                        // TODO Code to run in interrupted exception
//                        Toast.makeText(MainActivity.this,"run in interrupted exception",Toast.LENGTH_LONG).show();
//                    }
                } catch (IOException e) {
                    // TODO Code to run in input/output exception
                    Toast.makeText(MainActivity.this,"input/output exception",Toast.LENGTH_LONG).show();
                }
            }

        }
    }
    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first){
                first = false;
            }
            else{
                result.append("&");
            }

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
