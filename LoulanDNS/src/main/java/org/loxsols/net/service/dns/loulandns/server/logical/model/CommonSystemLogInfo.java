package org.loxsols.net.service.dns.loulandns.server.logical.model;


import java.util.*;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;
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
    public void setCommonSystemLogOccurDate(String logOccurDate) throws LoulanDNSSystemServiceException
    {
        ZonedDateTime dateTime;
        
        try
        {
            dateTime = LoulanDNSUtils.toDateTimeObject(logOccurDate);
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to set CommonSystemLog LogOccurDate. value=%s", logOccurDate);
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg, cause);
            throw exception;
        }

        setCommonSystemLogOccurDate(dateTime);
    }

    /**
     * ログ発生日時を設定する.
     * 
     * @param logOccurDate
     */
    public void setCommonSystemLogOccurDate(ZonedDateTime logOccurDate) throws LoulanDNSSystemServiceException
    {
        this.commonSystemLogOccurDate = logOccurDate;
    }


    public void setCreateDate(String createDate) throws LoulanDNSSystemServiceException
    {
        ZonedDateTime dateTime;
        
        try
        {
            dateTime = LoulanDNSUtils.toDateTimeObject(createDate);
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to set CommonSystemLog createDate. value=%s", createDate);
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg, cause);
            throw exception;
        }

        this.createDate = dateTime;
    }

    public void setUpdateDate(String updateDate) throws LoulanDNSSystemServiceException
    {
        ZonedDateTime dateTime;
        
        try
        {
            dateTime = LoulanDNSUtils.toDateTimeObject(updateDate);
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to set CommonSystemLog updateDate. value=%s", updateDate);
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg, cause);
            throw exception;
        }

        this.updateDate = dateTime;
    }


}