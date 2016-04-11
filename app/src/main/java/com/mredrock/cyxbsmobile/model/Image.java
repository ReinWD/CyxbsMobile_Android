package com.mredrock.cyxbsmobile.model;

/**
 * Created by mathiasluo on 16-4-11.
 */
public class Image {
    private String url;
    private int width;
    private int height;

    public static final int ADDIMAG = 0001;
    public static final int NORMALIMAGE = 0002;
    private int type;


    public Image(String url, int width, int height, int type) {
        this.url = url;
        this.width = width;
        this.height = height;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public Image setType(int type) {
        this.type = type;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "image---->>url=" + url + "width=" + width + "height" + height;
    }

}
