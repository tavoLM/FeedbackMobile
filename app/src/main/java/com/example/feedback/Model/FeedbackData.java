package com.example.feedback.Model;

import java.util.Date;

public class FeedbackData {

    private int CAR_TYPE;
    private  String BODY_NO;
    private int PART;
    private int DEFECT;
    private int LOCATION;
    private String ROUND;
    private String PROCESS;
    private Date STDT;
    private Date ENDT;
    private Date RGDT;
    private Integer RESPID;
    private String USERID;
    private int AREA;
    private int SHIFT;
    private int IDCOLOR;
    private int LINE;
    private String imageString;

    public int getCAR_TYPE() {
        return CAR_TYPE;
    }

    public void setCAR_TYPE(int CAR_TYPE) {
        this.CAR_TYPE = CAR_TYPE;
    }

    public String getBODY_NO() {
        return BODY_NO;
    }

    public void setBODY_NO(String BODY_NO) {
        this.BODY_NO = BODY_NO;
    }

    public int getPART() {
        return PART;
    }

    public void setPART(int PART) {
        this.PART = PART;
    }

    public int getDEFECT() {
        return DEFECT;
    }

    public void setDEFECT(int DEFECT) {
        this.DEFECT = DEFECT;
    }

    public int getLOCATION() {
        return LOCATION;
    }

    public void setLOCATION(int LOCATION) {
        this.LOCATION = LOCATION;
    }

    public String getROUND() {
        return ROUND;
    }

    public void setROUND(String ROUND) {
        this.ROUND = ROUND;
    }

    public String getPROCESS() {
        return PROCESS;
    }

    public void setPROCESS(String PROCESS) {
        this.PROCESS = PROCESS;
    }

    public Date getSTDT() {
        return STDT;
    }

    public void setSTDT(Date STDT) {
        this.STDT = STDT;
    }

    public Date getENDT() {
        return ENDT;
    }

    public void setENDT(Date ENDT) {
        this.ENDT = ENDT;
    }

    public Date getRGDT() {
        return RGDT;
    }

    public void setRGDT(Date RGDT) {
        this.RGDT = RGDT;
    }

    public Integer getRESPID() {
        return RESPID;
    }

    public void setRESPID(Integer RESPID) {
        this.RESPID = RESPID;
    }

    public String getUSERID() {
        return USERID;
    }

    public void setUSERID(String USERID) {
        this.USERID = USERID;
    }

    public int getAREA() {
        return AREA;
    }

    public void setAREA(int AREA) {
        this.AREA = AREA;
    }

    public int getSHIFT() {
        return SHIFT;
    }

    public void setSHIFT(int SHIFT) {
        this.SHIFT = SHIFT;
    }

    public int getIDCOLOR() {
        return IDCOLOR;
    }

    public void setIDCOLOR(int IDCOLOR) {
        this.IDCOLOR = IDCOLOR;
    }

    public int getLINE() {
        return LINE;
    }

    public void setLINE(int LINE) {
        this.LINE = LINE;
    }

    public String getImageString() {
        return imageString;
    }

    public void setImageString(String imageString) {
        this.imageString = imageString;
    }
}
