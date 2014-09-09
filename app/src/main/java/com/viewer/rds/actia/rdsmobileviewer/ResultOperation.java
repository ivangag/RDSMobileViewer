package com.viewer.rds.actia.rdsmobileviewer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by igaglioti on 22/07/2014.
 */
public class ResultOperation implements Parcelable{

    private boolean Status;
    private String OtherInfo;
    private Object ClassReturn;
    private transient int mClassReturnSize;
    private transient String mClassReturnType = null;

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
        return Status;
    }

    public void setStatus(boolean mStatus) {
        this.Status = mStatus;
    }

    public String getOtherInfo() {
        return OtherInfo;
    }

    public void setOtherInfo(String mOtherInfo) {
        this.OtherInfo = mOtherInfo;
    }

    public Object getClassReturn() {
        return ClassReturn;
    }

    public void setClassReturn(Object mClassReturn) {
        this.ClassReturn = mClassReturn;
    }

    public String getClassReturnType(){
        return mClassReturnType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBooleanArray(new boolean[]{Status});
        dest.writeString(OtherInfo);
        setClassReturnSize();
        dest.writeInt(mClassReturnSize);
        if (ClassReturn instanceof String) {
            mClassReturnType = "String";
            dest.writeString(mClassReturnType);
            dest.writeString((String) ClassReturn);
        } else {
            mClassReturnType = "bytes";
            dest.writeString(mClassReturnType);
            dest.writeByteArray((byte[]) ClassReturn);
        }
    }


    public ResultOperation(Parcel in) {
        boolean[] val = new boolean[1];
        in.readBooleanArray(val);
        Status = val[0];
        OtherInfo = in.readString();
        mClassReturnSize = in.readInt();
        mClassReturnType = in.readString();
        if(mClassReturnType.equals("bytes")) {
            byte[] classResStream = new byte[mClassReturnSize];
            in.readByteArray(classResStream);
            ClassReturn = classResStream;
        }
        else{
            ClassReturn = in.readString();
        }
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
        if(ClassReturn instanceof byte[])
            this.mClassReturnSize = ((byte[]) ClassReturn).length;
    }
}
