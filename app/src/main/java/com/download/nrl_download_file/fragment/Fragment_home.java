package com.download.nrl_download_file.fragment;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.download.nrl_download_file.MainActivity;
import com.download.nrl_download_file.R;
import com.download.nrl_download_file.login_info;
import com.poliveira.parallaxrecyclerview.ParallaxRecyclerAdapter;
import com.shinelw.library.ColorArcProgressBar;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
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

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment_home.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment_home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_home extends Fragment {
    private RecyclerView mRecyclerView;
    private OnFragmentInteractionListener mListener;
    private ColorArcProgressBar bar;
    public List<String> payload;
    public static String TAG="Fragment_home";
    public String malware;
    public SimpleDateFormat formatter;
    public Fragment_home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment Fragment_home.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_home newInstance(String imei, String param2) {
        Fragment_home fragment = new Fragment_home();
        Bundle bundle=new Bundle();
        bundle.putString("imei",imei);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root_view=inflater.inflate(R.layout.fragment_home,container,false);
        formatter= new SimpleDateFormat("dd MMM yyyy  hh:mm:ss a");
        mRecyclerView = (RecyclerView) root_view.findViewById(R.id.recyclerView);
        QueryData querydata=new QueryData(getActivity());
        querydata.execute(getArguments().getString("imei"));

        return root_view;
    }

    private void createAdapter(RecyclerView recyclerView,JSONArray jsonArray) throws JSONException {
        final List<String> content=new ArrayList<String>();
        payload=new ArrayList<String>();
        if(jsonArray!=null){
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String json_string=jsonObject.toString();
                Log.d(TAG,"JSon"+json_string);
                content.add(json_string);
            }
        }


        final ParallaxRecyclerAdapter<String> adapter = new ParallaxRecyclerAdapter<String>(content) {
            @Override
            public void onBindViewHolderImpl(RecyclerView.ViewHolder viewHolder, ParallaxRecyclerAdapter<String> adapter, int i){
                try {
                    JSONObject element= new JSONObject(adapter.getData().get(i));
                    payload.add(element.getString("payload"));
                    String time = formatter.format(new Date(Integer.valueOf(element.getString("time"))));
                    ((ViewHolder) viewHolder).time.setText("Time:"+time);
                    ((ViewHolder) viewHolder).dst_ip.setText("DSTIP:"+element.getString("dst_ip"));
                    ((ViewHolder) viewHolder).dst_port.setText("DSTPORT:"+element.getString("dst_port"));
                    ((ViewHolder) viewHolder).protocol.setText("PROTOCOL:"+element.getString("protocol"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolderImpl(ViewGroup viewGroup, final ParallaxRecyclerAdapter<String> adapter, int i) {
                return new ViewHolder(getActivity().getLayoutInflater().inflate(R.layout.fragment_home_item, viewGroup, false));
            }

            @Override
            public int getItemCountImpl(ParallaxRecyclerAdapter<String> adapter) {
                return content.size();
            }
        };

        adapter.setOnClickEvent(new ParallaxRecyclerAdapter.OnClickEvent() {
            @Override
            public void onClick(View v, int position) {
                String [] payload_split=payload.get(position).split("&");
                StringBuffer buf = new StringBuffer();
                for(int i=0;i<payload_split.length;i++){
                    buf.append(payload_split[i]+"\n");
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("PayLoad");
                builder.setMessage(buf);
                builder.setIcon(R.drawable.ic_bug);
                builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                    }

                });
                Dialog dialog = builder.create();
                dialog.getWindow().getAttributes().windowAnimations = R.style.dailoganimation;
                dialog.show();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        View headerview = getActivity().getLayoutInflater().inflate(R.layout.fragment_home_header, recyclerView, false);
        bar=(ColorArcProgressBar)headerview.findViewById(R.id.bar);
        bar.setCurrentValues(Integer.valueOf(malware));
        bar.setMaxValues(Integer.valueOf(malware));
        adapter.setParallaxHeader(headerview, recyclerView);
        adapter.setData(content);
        recyclerView.setAdapter(adapter);

    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView time,dst_port,dst_ip,protocol;

        public ViewHolder(View itemView) {
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.time);
            dst_port = (TextView) itemView.findViewById(R.id.dst_port);
            dst_ip = (TextView) itemView.findViewById(R.id.dst_ip);
            protocol = (TextView) itemView.findViewById(R.id.protocol);

        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
                params.add(new BasicNameValuePair("type", "packet_info"));
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
            JSONObject jsonResponse = null;
            if(result!=null){
                try {
                    jsonResponse = new JSONObject(result);
                    malware=jsonResponse.getString("malware");
                    Log.d(TAG,"Malware"+malware);
                    JSONArray jsonArray = jsonResponse.getJSONArray("data");
//                    for (int i=0; i<jsonArray.length(); i++) {
//                        JSONObject element = jsonArray.getJSONObject(i);
//                        String time = element.getString("time");
//                        Log.d(TAG,"Time"+time);
//                    }
                    createAdapter(mRecyclerView,jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
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

