package com.download.nrl_download_file.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.download.nrl_download_file.R;
import com.download.nrl_download_file.model_info;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by garytan on 2017/5/24.
 */

public class setting_listview_adapter  extends BaseAdapter implements View.OnClickListener {
    private LayoutInflater myInflater;
    private List<model_info> model;
    private Context context;
    private static  final String TAG="listview_adapter";
    private String  imei;
    public setting_listview_adapter(Context context, List<model_info> model){
        myInflater = LayoutInflater.from(context);
        this.model = model;
    }
    @Override
    public int getCount() {
        return model.size();
    }

    @Override
    public Object getItem(int position) {
        return model.get(position);
    }

    @Override
    public long getItemId(int position) {
        return model.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView==null){
            convertView = myInflater.inflate(R.layout.listview_items, null);
            holder = new ViewHolder(
                    (TextView) convertView.findViewById(R.id.modelname),
                    (TextView) convertView.findViewById(R.id.modeltime),
                    (Button) convertView.findViewById(R.id.chanage_model),
                    (Button) convertView.findViewById(R.id.delete_model)
            );
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        model_info modelInfo = (model_info)getItem(position);
        holder.modelname.setText(modelInfo.getModelname());
        holder.modeltime.setText(modelInfo.getModeltime());
        holder.chanage.setText("Chanage to "+modelInfo.getModelname());
        holder.delete.setText("Delete to "+modelInfo.getModelname());
        context=parent.getContext();
        TelephonyManager tM=(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        imei= tM.getDeviceId();
        holder.chanage.setOnClickListener(this);
        holder.delete.setOnClickListener(this);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chanage_model:
                Button chanage=(Button)v;
                String chanage_text=chanage.getText().toString();
                Log.d("listview_adapter"," Button"+chanage_text);
                String[] file_count = chanage_text.split("_");
                Log.d(TAG,"FileCount"+file_count[2]);
                String[] arg=new String[]{"update",file_count[2],imei};
                ModelAction chanageAction=new ModelAction(context);
                chanageAction.execute(arg);
                break;
            case R.id.delete_model:
                Button delete=(Button)v;
                String delete_text=delete.getText().toString();
                Log.d("listview_adapter","Bootstrap Button"+delete_text);
                String[] model = delete_text.split(" ");
                Log.d(TAG,"Model"+model[2]);
                String[] arg1=new String[]{"delete",model[2]+".txt",imei};
                ModelAction deteleAction=new ModelAction(context);
                deteleAction.execute(arg1);
                break;
        }
    }

    private class ViewHolder {
        TextView modelname;
        TextView modeltime;
        Button chanage,delete;

        public ViewHolder(TextView modelname, TextView modeltime
        ,Button chanage,Button delete){
            this.modelname = modelname;
            this.modeltime = modeltime;
            this.chanage=chanage;
            this.delete=delete;
        }
    }
    private class ModelAction extends AsyncTask<String, Integer, String> {
        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public ModelAction(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... arg) {
            InputStream input = null;
            OutputStream output = null;
            HttpsURLConnection connection = null;
            try {
                URL url = new URL("https://amds.nrl.mcu.edu.tw:8080/query");
                InputStream instream = context.getResources().openRawResource(R.raw.amds);
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                try {
                    trustStore.load(instream, null);
                } catch (java.security.cert.CertificateException e) {
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
                if(arg[0].equals("update")){
                    params.add(new BasicNameValuePair("type", "model"));
                    params.add(new BasicNameValuePair("ask", "phone"));
                    params.add(new BasicNameValuePair("action", arg[0]));
                    params.add(new BasicNameValuePair("imei", "123"));//arg[2]
                    params.add(new BasicNameValuePair("file_count", arg[1]));
                }else{
                    params.add(new BasicNameValuePair("type", "model"));
                    params.add(new BasicNameValuePair("ask", "phone"));
                    params.add(new BasicNameValuePair("action", arg[0]));
                    params.add(new BasicNameValuePair("imei", "123"));//arg[2]
                    params.add(new BasicNameValuePair("model", arg[1]));
                }
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(params));
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
            String response = "";
            int responseCode = 0;
            try {
                responseCode = connection.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                } else {
                    response = "";
                }
                //Log.d(TAG,"Responese"+response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
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
            Log.d(TAG, "Result" + result);


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
