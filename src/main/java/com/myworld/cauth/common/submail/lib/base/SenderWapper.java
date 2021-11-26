package com.myworld.cauth.common.submail.lib.base;


import com.myworld.cauth.common.submail.entity.DataStore;

/**
 * 包装类 ADDRESSBOOKMail、ADDRESSBOOKMessage、MAILSend、MAILXSend、MESSAGEXsend等父类
 */
public abstract class SenderWapper {

    protected DataStore requestData = new DataStore();

    public abstract ISender getSender();
}
