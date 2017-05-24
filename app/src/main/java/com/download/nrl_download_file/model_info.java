package com.download.nrl_download_file;

/**
 * Created by garytan on 2017/5/24.
 */

public class model_info {
    public String modelname;
    public String modeltime;
    public model_info(String modelname,String modeltime){
        this.modelname=modelname;
        this.modeltime=modeltime;
    }
    public String getModelname() {
        return modelname;
    }

    public String getModeltime() {
        return modeltime;
    }


}
