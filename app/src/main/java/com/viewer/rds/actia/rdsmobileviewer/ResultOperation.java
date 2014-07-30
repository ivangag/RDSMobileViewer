package com.viewer.rds.actia.rdsmobileviewer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by igaglioti on 22/07/2014.
 */
public class ResultOperation implements Parcelable{

    private boolean mStatus;
    private String mOtherInfo;
    private Object mClassReturn;
    private int mClassReturnSize;

    private ResultOperation() {

    }

    public static ResultOperation newInstance()
    {
        ResultOperation resultOperation = new ResultOperation();
        return resultOperation;
    }

    public static ResultOperation newInstance(boolean status,String otherInfo, Object classReturn)
    {
        ResultOperation resultOperation = new ResultOperation();
        resultOperation.setClassReturn(classReturn);
        resultOperation.setStatus(status);
        resultOperation.setOtherInfo(otherInfo);
        return resultOperation;
    }

    public boolean isStatus() {
        return mStatus;
    }

    public void setStatus(boolean mStatus) {
        this.mStatus = mStatus;
    }

    public String getOtherInfo() {
        return mOtherInfo;
    }

    public void setOtherInfo(String mOtherInfo) {
        this.mOtherInfo = mOtherInfo;
    }

    public Object getClassReturn() {
        return mClassReturn;
    }

    public void setClassReturn(Object mClassReturn) {
        this.mClassReturn = mClassReturn;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBooleanArray(new boolean[]{mStatus});
        dest.writeString(mOtherInfo);
        setClassReturnSize();
        dest.writeInt(mClassReturnSize);
        dest.writeByteArray((byte[])mClassReturn);
    }


    public ResultOperation(Parcel in) {
        boolean[] val = new boolean[1];
        in.readBooleanArray(val);
        mStatus = val[0];
        mOtherInfo = in.readString();
        mClassReturnSize = in.readInt();
        byte[] classResStream = new byte[mClassReturnSize];
        in.readByteArray(classResStream);
        mClassReturn = classResStream;
    }


    public static final Parcelable.Creator<ResultOperation> CREATOR
            = new Parcelable.Creator<ResultOperation>() {
        public ResultOperation createFromParcel(Parcel in) {
            return new ResultOperation(in);
        }

        public ResultOperation[] newArray(int size) {
            return new ResultOperation[size];
        }
    };

    public void setClassReturnSize() {
        if(mClassReturn instanceof byte[])
            this.mClassReturnSize = ((byte[])mClassReturn).length;
    }
}
