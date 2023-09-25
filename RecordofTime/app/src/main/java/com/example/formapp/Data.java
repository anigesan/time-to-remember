package com.example.formapp;

import java.util.ArrayList;
import java.util.Date;

public class Data {
    public  static ArrayList<Data> dataArrayList = new ArrayList<>();
    public static  String DATA_EDIT_EXTRA = "dataEdit";
    private int id;
    private String title;
    private String note;
    private String location;
    private byte[] image;
    private Date deleted;

    public Data(int id, String title, String note, String location, byte[] image, Date deleted) {
        this.id = id;
        this.title = title;
        this.note = note;
        this.location = location;
        this.image = image;
        this.deleted = deleted;
    }

    public Data(int id, String title, String note, String location, byte[] image) {
        this.id = id;
        this.title = title;
        this.note = note;
        this.location =location;
        this.image = image;
    }

    public static Data getDataForID(int passedDataID) {
        for (Data data : dataArrayList)
        {
            if(data.getId() == passedDataID)
                return data;
        }
        return null;
    }

    public static ArrayList<Data> nonDeletedDatas()
    {
        ArrayList<Data> nonDeleted = new ArrayList<>();
        for(Data data : dataArrayList)
        {
            if(data.getDeleted() == null)
                nonDeleted.add(data);
        }
        return nonDeleted;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }
}
