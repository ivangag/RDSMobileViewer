package com.viewer.rds.actia.rdsmobileviewer;

/**
 * Created by igaglioti on 22/07/2014.
 */
public class ResultOperation {

    private boolean mStatus;
    private String mOtherInfo;
    private Object mClassReturn;

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
}
