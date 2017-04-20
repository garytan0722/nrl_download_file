package com.download.nrl_download_file;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.facebook.FacebookSdk;

import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by garytan on 2016/11/22.
 */

public class Startapp extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener{
    private final static String TAG="Startapp";
    public BootstrapButton google_btn,fb_btn;
    public LinearLayout linearLayout;
    public ImageView image;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    private Dialog dialog;
    private  String android_id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TypefaceProvider.registerDefaultIconSets();


        android_id = Settings.Secure.getString(this.getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        setContentView(R.layout.start_app);
        google_btn=(BootstrapButton)findViewById(R.id.google);
        fb_btn=(BootstrapButton)findViewById(R.id.fb);
        linearLayout=(LinearLayout)findViewById(R.id.linear_layout);
        image=(ImageView)findViewById(R.id.image);
        google_btn.setOnClickListener(this);
        fb_btn.setOnClickListener(this);
        google_init();
        facebook_init();
        animation();
    }
    public void google_init(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }
    public void facebook_init(){
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
            accessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(
                        AccessToken oldAccessToken,
                        AccessToken currentAccessToken) {
                    Log.d(TAG, "AccessToken Canage");
                    accessToken = currentAccessToken;
                    GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {

                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            Log.d(TAG, " Chanage onCompleted");

                            if (object != null) {
                                Log.d(TAG, object.optString("name"));
                                Log.d(TAG, object.optString("link"));
                                Log.d(TAG, object.optString("id"));
                                login_info info = new login_info();
                                info.setLogin_type("2");
                                info.setFb_id(object.optString("id"));
                                info.setFb_name(object.optString("name"));
                                info.setAndroid_id(android_id);
                            }

                        }
                    });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,link");
                    request.setParameters(parameters);
                    request.executeAsync();
                }
            };
        LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "Success FB Login");
                        loginResult.getAccessToken();
                        GraphRequest request=GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.d(TAG,"Completed");
                                Log.d(TAG,object.optString("name"));
                                Log.d(TAG,object.optString("link"));
                                Log.d(TAG,object.optString("id"));
                                login_info info =new login_info();
                                info.setContext(getApplicationContext());
                                info.setLogin_type("2");
                                info.setFb_id(object.optString("id"));
                                info.setFb_name(object.optString("name"));
                                info.setAndroid_id(android_id);
                                upload_info upload=new upload_info();
                                upload.execute(info);
                                Intent intent=new Intent(Startapp.this,MainActivity.class);
                                startActivity(intent);
                            }
                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,link");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, "Error FB Login"+error);
                    }
                });
    }
    public void animation(){
        Animation image_anim = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.image_anim);
        image_anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation btn_trans = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.btn_translate);
                linearLayout.startAnimation(btn_trans);
                google_btn.setVisibility(View.VISIBLE);
                fb_btn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
       image.startAnimation(image_anim);
    }

    public boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()&&networkInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
    public void Alert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Startapp.this);
        builder.setTitle("Warning");
        builder.setMessage("Network is not avialable!");
        builder.setIcon(R.drawable.warning);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
            }

        });
        dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.dailoganimation;
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.google:
                Log.d(TAG,"login google");
                if(isConnected()){
                    Google_signIn();
                }else {
                    Alert();
                }
                break;
            case R.id.fb:
                Log.d(TAG,"login fb");
                if(isConnected()){
                    Facebook_signIn();
                }else{
                    Alert();
                }
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }else{
            Log.d(TAG,"onActivityResult");
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    public void Google_signIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    public void Facebook_signIn(){
        if(accessToken.getCurrentAccessToken()!=null){
            GraphRequest request=GraphRequest.newMeRequest(accessToken,new GraphRequest.GraphJSONObjectCallback(){

                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    Log.d(TAG," already Completed");
                    //Log.d(TAG,object.optString("name"));
                    //Log.d(TAG,object.optString("link"));
                    //Log.d(TAG,object.optString("id"));
                    login_info info =new login_info();
                    info.setContext(getApplicationContext());
                    info.setLogin_type("2");
                    info.setFb_id(object.optString("id"));
                    info.setFb_name(object.optString("name"));
                    info.setAndroid_id(android_id);
                    upload_info upload=new upload_info();
                    upload.execute(info);
                    Intent intent=new Intent(Startapp.this,MainActivity.class);
                    startActivity(intent);
                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,link");
            request.setParameters(parameters);
            request.executeAsync();

        }else{
            LoginManager.getInstance().logInWithReadPermissions(Startapp.this, Arrays.asList("public_profile", "user_friends"));
        }

    }
    public void handleSignInResult(GoogleSignInResult result){
        Log.d(TAG,"handleSignInResult: "+result.isSuccess());
        if(result.isSuccess()){
            Log.d(TAG, "Success Goolge Login");
            GoogleSignInAccount acct=result.getSignInAccount();
            String personName = acct.getDisplayName();
            String personId = acct.getId();
            Log.d(TAG,"Goolge name"+personName);
            login_info info =new login_info();
            info.setContext(getApplicationContext());
            info.setLogin_type("1");
            info.setGoogle_id(personId);
            info.setGoogle_name(personName);
            info.setAndroid_id(android_id);
            upload_info upload=new upload_info();
            upload.execute(info);
            Intent intent=new Intent(Startapp.this,MainActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }
}
