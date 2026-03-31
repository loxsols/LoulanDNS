package org.loxsols.net.service.dns.loulandns.server.logical.model;


import java.util.*;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;


import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;


@Getter
@Setter
public class CommonSystemLogInfo
{

    public Long commonSystemLogID;
    public String commonSystemLogTag;
    public String commonSystemLogRecord;
    public ZonedDateTime commonSystemLogOccurDate;
    
    public String commonSystemLogOption1;
    public String commonSystemLogOption2;
    public String commonSystemLogOption3;

    public long recordStatus;
    public String memo;
    public ZonedDateTime createDate;
    public ZonedDateTime updateDate;


    public List<CommonSystemLogPropertiesInfo> commonSystemLogPropertiesInfoList;

    /**
     * ログ発生日時を設定する.
     * 
     */
    public void setCommonSystemLogOccurDate(String logOccurDate)
    {
        ZonedDateTime dateTime = LoulanDNSUtils.toDateTimeObject(logOccurDate);
        setCommonSystemLogOccurDate(dateTime);
    }

    /**
     * ログ発生日時を設定する.
     * 
     * @param logOccurDate
     */
    public void setCommonSystemLogOccurDate(ZonedDateTime logOccurDate)
    {
        this.commonSystemLogOccurDate = logOccurDate;
    }


    public void setCreateDate(String createDate)
    {
        ZonedDateTime dateTime = LoulanDNSUtils.toDateTimeObject(createDate);
        this.createDate = dateTime;
    }

    public void setUpdateDate(String updateDate)
    {
        ZonedDateTime dateTime = LoulanDNSUtils.toDateTimeObject(updateDate);
        this.updateDate = dateTime;
    }


}