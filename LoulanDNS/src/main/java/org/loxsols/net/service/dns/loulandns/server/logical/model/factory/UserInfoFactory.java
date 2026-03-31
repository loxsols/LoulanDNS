package org.loxsols.net.service.dns.loulandns.server.logical.model.factory;

import java.util.*;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;



import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;


import org.loxsols.net.service.dns.loulandns.server.logical.model.*;

// LoulanDNSの論理モデルクラスのうちのユーザーオブジェクトのファクトリクラス.
public class UserInfoFactory
{
    public UserInfo createUserInfoObject(Long userID, String userName, String userPassword, long recordStatus, String memo, ZonedDateTime createDate, ZonedDateTime updateDate)
    {
        UserInfo userInfo = new UserInfo();
        
        userInfo.setUserID(userID);
        userInfo.setUserName(userName);
        userInfo.setUserPassword(userPassword);
        userInfo.setRecordStatus(recordStatus);
        userInfo.setMemo(memo);
        userInfo.setCreateDate(createDate);
        userInfo.setUpdateDate(updateDate);

        return userInfo;
    }

    public UserInfo createUserInfoObject(Long userID, String userName, String userPassword, long recordStatus, String memo, String createDateString, String updateDateString)
    {
        ZonedDateTime createDate = LoulanDNSUtils.toDateTimeObject(createDateString);
        ZonedDateTime updateDate = LoulanDNSUtils.toDateTimeObject(updateDateString);

        UserInfo userInfo = createUserInfoObject(userID, userName, userPassword, recordStatus, memo, createDate, updateDate );
        return userInfo;
    }

    public UserInfo createUserInfoObject(Long userID, String userName, String userPassword, long recordStatus, String memo )
    {
        ZonedDateTime createDate, updateDate;
        createDate = updateDate = LoulanDNSUtils.getCurrentZonedDateTime();

        UserInfo userInfo = createUserInfoObject(userID, userName, userPassword, recordStatus, memo, createDate, updateDate );
        return userInfo;
    }

}