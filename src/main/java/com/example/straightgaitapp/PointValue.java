package com.example.straightgaitapp;

public class PointValue {
    long xValue;
    int yValue;

    public PointValue(){

    }
    public PointValue(long xValue, int yValue){
        this.setxValue(xValue);
        this.setyValue(yValue);
    }

    public int getyValue() {
        return yValue;
    }

    public long getxValue() {
        return xValue;
    }

    public void setxValue(long xValue) {
        this.xValue = xValue;
    }

    public void setyValue(int yValue) {
        this.yValue = yValue;
    }
}
