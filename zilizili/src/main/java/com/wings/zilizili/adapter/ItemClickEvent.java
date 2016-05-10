package com.wings.zilizili.adapter;

import android.widget.ImageView;

/**
 * Created by wing on 2016/5/10.
 */
public class ItemClickEvent {
    private int id;
    private ImageView iv;
    private String url;

    public ItemClickEvent(int id, ImageView iv, String url) {
        this.id = id;
        this.iv = iv;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ImageView getIv() {
        return iv;
    }

    public void setIv(ImageView iv) {
        this.iv = iv;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
