package com.example.liuliangqi.CP.bean;

import java.io.Serializable;

import com.baidu.android.itemview.helper.BaseStyle;
public abstract class BaseData extends BaseStyle implements Serializable {

    protected static final int STATUS_OK = 0;
    protected static final int STATUS_ERROR = -1;
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private transient int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
