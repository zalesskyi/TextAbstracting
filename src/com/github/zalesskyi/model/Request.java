package com.github.zalesskyi.model;

public class Request {
    private String source;

    private int cofficient;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getCofficient() {
        return cofficient;
    }

    public void setCofficient(Integer cofficient) {
        if (cofficient == null) {
            cofficient = -1;
        }
        this.cofficient = cofficient;
    }
}
