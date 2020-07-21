package com.straightgait.straightgaitapp;

import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;

public class Movement {
    private String angle;
    private Long timeStamp;
    private int status;

    public Movement(){

    }
    public Movement(String angle, Long timeStamp, int status) {
        this.setAngle(angle);
        this.setDate(timeStamp);
        this.setStatus(status);
    }

    public Long getDate() {
        return timeStamp;
    }

    public int getStatus() {
        return status;
    }

    public String getAngle() {
        return angle;
    }

    public void setAngle(String angle) {
        this.angle = angle;
    }

    public void setDate(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDateStr(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("dd/MM/yyyy, HH:mm", cal).toString();
        return date;
    }

}
