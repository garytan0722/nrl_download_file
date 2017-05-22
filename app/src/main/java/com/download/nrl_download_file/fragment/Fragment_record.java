package com.download.nrl_download_file.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.download.nrl_download_file.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

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
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment_record.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment_record#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_record extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String TAG="Fragment_record";
    public  LineChart lineChart;
    public SimpleDateFormat formatter;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Fragment_record() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_home.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_record newInstance(String param1, String param2) {
        Fragment_record fragment = new Fragment_record();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        QueryData queryData=new QueryData(getActivity());
        View root_view=inflater.inflate(R.layout.fragment_record,container,false);
        formatter= new SimpleDateFormat("hh:mm");
        queryData.execute(getArguments().getString("imei"));
        lineChart = (LineChart) root_view.findViewById(R.id.chart_line);
        return root_view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    private class QueryData extends AsyncTask<String, Integer, String> {
        private Context context;
        private PowerManager.WakeLock mWakeLock;
        public QueryData(Context context) {
            this.context = context;
        }
        @Override
        protected String doInBackground(String... arg) {
            String imei=arg[0];
            InputStream input = null;
            OutputStream output = null;
            HttpsURLConnection connection = null;
            try {
                URL url = new URL("https://amds.nrl.mcu.edu.tw:8080/query");
                InputStream instream = context.getResources().openRawResource(R.raw.amds);
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
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("type", "packet_timechart"));
                params.add(new BasicNameValuePair("ask", "phone"));
                params.add(new BasicNameValuePair("imei", "123"));
                params.add(new BasicNameValuePair("malware_tag", "1"));
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
            Log.d(TAG,"Result"+result);
            if(result!=null){
                JSONObject jsonResponse = null;
                JSONArray jsonArray=null,labelArray=null;
                ArrayList<Entry> data=new ArrayList<Entry>();
                final ArrayList<String> label=new ArrayList<String>();
                try {
                    jsonResponse = new JSONObject(result);
                    jsonArray=jsonResponse.getJSONArray("info");
                    labelArray=jsonResponse.getJSONArray("label");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for(int i=0;i<jsonArray.length();i++){
                    try {
                        JSONObject json_label=labelArray.getJSONObject(i);
                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        String time_label = formatter.format(new Date(Integer.valueOf(json_label.getString("time"))));
                        Integer size=Integer.valueOf(jsonObject.getString("size"));
                        label.add(time_label);
                        data.add(new Entry(i,size));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                LineDataSet setpacket = new LineDataSet(data, "Packet Size");
                setpacket.setAxisDependency(YAxis.AxisDependency.LEFT);
                LineData showdata = new LineData(setpacket);
                lineChart.setData(showdata);
                IAxisValueFormatter formatter = new IAxisValueFormatter() {

                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        Log.d(TAG,"Value"+value);
                        return label.get((int) value);
                    }

                };

                XAxis xAxis = lineChart.getXAxis();
                xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
                xAxis.setValueFormatter(formatter);
                Log.d(TAG,"Size"+label.size());
                Log.d(TAG,"chart show");
                lineChart.invalidate();
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
