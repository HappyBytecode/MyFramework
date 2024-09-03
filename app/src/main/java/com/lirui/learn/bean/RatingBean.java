package com.lirui.learn.bean;

/**
 * 评分
 */

public class RatingBean {
    private int max;
    private double average;
    private String stars;
    private int min;

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public String getStars() {
        return stars;
    }

    public void setStars(String stars) {
        this.stars = stars;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    @Override
    public String toString() {
        return "RatingBean{" +
                "max=" + max +
                ", average=" + average +
                ", stars=" + stars +
                ", min=" + min +
                '}';
    }
}