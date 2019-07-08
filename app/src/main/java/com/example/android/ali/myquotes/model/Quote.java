package com.example.android.ali.myquotes.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.List;

public class Quote implements Parcelable {
    private String id;
    private int page;
    private String text;
    private String remark;

    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Quote() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(page);
        dest.writeString(text);
        dest.writeString(remark);
    }

    protected Quote(Parcel in) {
        id = in.readString();
        page = in.readInt();
        text = in.readString();
        remark = in.readString();
    }

    public static final Creator<Quote> CREATOR = new Creator<Quote>() {
        @Override
        public Quote createFromParcel(Parcel in) {
            Quote quote = new Quote();
            quote.id = in.readString();
            quote.page = in.readInt();
            quote.text = in.readString();
            quote.remark = in.readString();

            return quote;
        }

        @Override
        public Quote[] newArray(int size) {
            return new Quote[size];
        }
    };

//    public List<Quote> getData(){
//        List<Quote> quotes = new ArrayList<>();
//
//        Quote q1 = new Quote();
//        Quote q2 = new Quote();
//        q1.setText("Quote one");
//        q2.setText("I have replaced my code with yours and it works, I posted a picture with the results...I don't know what happens there, maybe you are setting some of the properties in the code also? Or it's just that I don't understand what you want.. Have you also tried the new solution provided by");
//
//        quotes.add(q1);
//        quotes.add(q2);
//        return quotes;
//    }
}
