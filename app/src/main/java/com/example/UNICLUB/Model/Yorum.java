package com.example.UNICLUB.Model;

public class Yorum {

    String yorum;
    String gonderen;

    public Yorum() {
    }

    public Yorum(String yorum, String gonderen) {
        this.yorum = yorum;
        this.gonderen = gonderen;
    }

    public String getYorum() {
        return yorum;
    }

    public void setYorum(String yorum) {
        this.yorum = yorum;
    }

    public String getGonderen() {
        return gonderen;
    }

    public void setGonderen(String gonderen) {
        this.gonderen = gonderen;
    }
}
