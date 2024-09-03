package com.lirui.learn.bean;

import java.util.List;

/**
 * 正在上映的电影
 */
public class InTheaters {
    private int start;
    private int count;
    private int total;//总数
    private List<Movie> subject;//电影条目
    private String title;//标题

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Movie> getSubject() {
        return subject;
    }

    public void setSubject(List<Movie> subject) {
        this.subject = subject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "InTheaters{" +
                "start=" + start +
                ", count=" + count +
                ", total=" + total +
                ", subject=" + subject +
                ", title='" + title + '\'' +
                '}';
    }
}
