package com.iffcokisan.camerademo.Adapter;

/**
 * Created by Taufiq on 12-12-2017.
 */

import java.io.Serializable;

/**
 * Created by sab99r
 */
public class MovieModel implements Serializable{
    String title;
    String rating;
    String type;


    public MovieModel() {
    }

    public MovieModel(String type) {
        this.type = type;
    }

    public MovieModel(String title, String rating) {
        this.title = title;
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
