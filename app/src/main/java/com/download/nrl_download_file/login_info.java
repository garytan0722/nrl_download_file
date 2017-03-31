package com.download.nrl_download_file;

import android.content.Context;

/**
 * Created by garytan on 2016/11/25.
 */

public class login_info {
    public String login_type;
    public String google_id;
    public String fb_id;
    public String google_name;
    public String fb_name;
    public Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getLogin_type() {
        return login_type;
    }

    public String getGoogle_id() {
        return google_id;
    }

    public String getFb_id() {
        return fb_id;
    }

    public String getGoogle_name() {
        return google_name;
    }

    public String getFb_name() {
        return fb_name;
    }

    public void setLogin_type(String login_type) {
        this.login_type = login_type;
    }

    public void setGoogle_id(String google_id) {
        this.google_id = google_id;
    }

    public void setFb_id(String fb_id) {
        this.fb_id = fb_id;
    }

    public void setGoogle_name(String google_name) {
        this.google_name = google_name;
    }

    public void setFb_name(String fb_name) {
        this.fb_name = fb_name;
    }


}
