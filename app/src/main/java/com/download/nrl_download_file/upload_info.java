package com.download.nrl_download_file;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;



/**
 * Created by garytan on 2016/11/25.
 */

public class upload_info extends AsyncTask<login_info, Void, String> {
    private static String TAG="upload_info";
    @Override
    protected String doInBackground(login_info... params) {
        login_info info=params[0];
        InputStream input = null;
        OutputStream output = null;
        HttpsURLConnection connection = null;
        try {
            URL url = new URL("https://amds.nrl.mcu.edu.tw/amds/get_login.php");
            InputStream instream = info.getContext().getResources().openRawResource(R.raw.amds);
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            try {
                trustStore.load(instream, null);
            } catch (CertificateException e) {
                e.printStackTrace();
            }finally {
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
            List<NameValuePair> value = new ArrayList<NameValuePair>();
            if(info.getLogin_type().equals("1")){
                value.add(new BasicNameValuePair("type", info.getLogin_type()));
                value.add(new BasicNameValuePair("google_id", info.getGoogle_id()));
                value.add(new BasicNameValuePair("name", info.getGoogle_name()));
                value.add(new BasicNameValuePair("imei",info.getAndroid_id()));
            }else{
                value.add(new BasicNameValuePair("type", info.getLogin_type()));
                value.add(new BasicNameValuePair("fb_id", info.getFb_id()));
                value.add(new BasicNameValuePair("name", info.getFb_name()));
                value.add(new BasicNameValuePair("imei",info.getAndroid_id()));
            }
            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(value));
            writer.flush();
            writer.close();
            os.close();
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        String response="";
        int responseCode= 0;
        try {
            responseCode = connection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";
            }
            Log.d(TAG,"Responese"+response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String value) {
        super.onPostExecute(value);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
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
