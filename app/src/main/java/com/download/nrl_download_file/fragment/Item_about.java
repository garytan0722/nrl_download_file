package com.download.nrl_download_file.fragment;

/**
 * Created by garytan on 2016/11/22.
 */

public class Item_about {

    public int name;
    public int info;
    public int img;

    public Item_about(int name, int info, int img) {
        this.name=name;
        this.info=info;
        this.img=img;
    }
    public int getName() {
        return name;
    }
    public int getInfo() {
        return info;
    }
    public int getImg() {
        return img;
    }


}
