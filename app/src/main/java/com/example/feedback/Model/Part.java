package com.example.feedback.Model;

public class Part {
    private int id;
    private String name;
    private int seq;
    private int active;
    private int idArea;
    private String shop;
    private String process;
    private int idOverflow;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getIdArea() {
        return idArea;
    }

    public void setIdArea(int idArea) {
        this.idArea = idArea;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public int getIdOverflow() {
        return idOverflow;
    }

    public void setIdOverflow(int idOverflow) {
        this.idOverflow = idOverflow;
    }
}
