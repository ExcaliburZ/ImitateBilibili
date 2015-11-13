package com.wings.zilizili.domain;

import java.io.Serializable;

/**
 * Created by wing on 2015/9/1.
 */
public class VideoInfo implements Serializable {
    private String Title;
    private Long Duration;
    private String Data;
    private Long Size;

    public String getData() {
        return Data;
    }


    public void setData(String data) {
        Data = data;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public Long getDuration() {
        return Duration;
    }

    public void setDuration(Long duration) {
        Duration = duration;
    }

    public Long getSize() {
        return Size;
    }

    public void setSize(Long size) {
        Size = size;
    }
}
