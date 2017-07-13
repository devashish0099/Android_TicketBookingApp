package com.example.tonmo.bookticket;


import java.io.Serializable;

public class Bus implements Serializable{

    private String mCompany,mFrom,mTo,mSeat,mBookedSeat,mFare,mTime,mDate,mId;
    private boolean mIsAc,mIsSleeper;

    public Bus(){

    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public void setmBookedSeat(String mBookedSeat) {
        this.mBookedSeat = mBookedSeat;
    }


    public String getmBookedSeat() {
        return mBookedSeat;
    }

    public String getmCompany() {
        return mCompany;
    }

    public void setmCompany(String mCompany) {
        this.mCompany = mCompany;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public String getmFare() {
        return mFare;
    }

    public void setmFare(String mFare) {
        this.mFare = mFare;
    }

    public String getmFrom() {
        return mFrom;
    }

    public void setmFrom(String mFrom) {
        this.mFrom = mFrom;
    }

    public boolean ismIsAc() {
        return mIsAc;
    }

    public void setmIsAc(boolean mIsAc) {
        this.mIsAc = mIsAc;
    }

    public boolean ismIsSleeper() {
        return mIsSleeper;
    }

    public void setmIsSleeper(boolean mIsSleeper) {
        this.mIsSleeper = mIsSleeper;
    }

    public String getmSeat() {
        return mSeat;
    }

    public void setmSeat(String mSeat) {
        this.mSeat = mSeat;
    }

    public String getmTo() {
        return mTo;
    }

    public void setmTo(String mTo) {
        this.mTo = mTo;
    }

    public String getmTime() {
        return mTime;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }
}
