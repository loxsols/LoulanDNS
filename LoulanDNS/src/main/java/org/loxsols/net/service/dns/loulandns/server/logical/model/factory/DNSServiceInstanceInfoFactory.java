package org.loxsols.net.service.dns.loulandns.server.logical.model.factory;

import java.util.*;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;
import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;


import org.loxsols.net.service.dns.loulandns.server.logical.model.*;

// LoulanDNSの論理モデルクラスのうちのDNSサービスインスタンスオブジェクトのファクトリクラス.
public class DNSServiceInstanceInfoFactory
{
    public DNSServiceInstanceInfo createDNSServiceInstanceInfoObject(Long dnsServiceInstanceID, Long userID, String dnsServiceInstanceName, String dnsServiceInstanceExplain, Long dnsServiceTypeCode, Long dnsResolverInstanceID, long recordStatus, String memo, ZonedDateTime createDate, ZonedDateTime updateDate)
    {

        DNSServiceInstanceInfo dnsServiceInstanceInfo = new DNSServiceInstanceInfo();

        dnsServiceInstanceInfo.setDNSServiceInstanceID(dnsServiceInstanceID);
        dnsServiceInstanceInfo.setUserID(userID);
        dnsServiceInstanceInfo.setDNSServiceInstanceName(dnsServiceInstanceName);
        dnsServiceInstanceInfo.setDNSServiceInstanceExplain(dnsServiceInstanceExplain);
        dnsServiceInstanceInfo.setDnsServiceTypeCode(dnsServiceTypeCode);
        dnsServiceInstanceInfo.setDNSResolverInstanceID(dnsResolverInstanceID);

        dnsServiceInstanceInfo.setRecordStatus(recordStatus);
        dnsServiceInstanceInfo.setMemo(memo);
        dnsServiceInstanceInfo.setCreateDate(createDate);
        dnsServiceInstanceInfo.setUpdateDate(updateDate);

        return dnsServiceInstanceInfo;
    }

    public DNSServiceInstanceInfo createDNSServiceInstanceInfoObject(Long dnsServiceInstanceID, Long userID, String dnsServiceInstanceName, String dnsServiceInstanceExplain, Long dnsServiceTypeCode, Long dnsResolverInstanceID, long recordStatus, String memo, String createDateString, String updateDateString) throws LoulanDNSSystemServiceException
    {
        ZonedDateTime createDate;
        try
        {
            createDate = LoulanDNSUtils.toDateTimeObject(createDateString);
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to convert to ZonedDateTime object from String. value=%s", createDateString);
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg, cause);
            throw exception;
        }


        ZonedDateTime updateDate;
        try
        {
            updateDate = LoulanDNSUtils.toDateTimeObject(updateDateString);
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to convert to ZonedDateTime object from String. value=%s", updateDateString);
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg, cause);
            throw exception;
        }

        DNSServiceInstanceInfo dnsServiceInstanceInfo = createDNSServiceInstanceInfoObject(dnsServiceInstanceID, userID, dnsServiceInstanceName, dnsServiceInstanceExplain, dnsServiceTypeCode, dnsResolverInstanceID, recordStatus, memo, createDate, updateDate );
        return dnsServiceInstanceInfo;
    }

    public DNSServiceInstanceInfo createDNSServiceInstanceInfoObject(Long dnsServiceInstanceID, Long userID, String dnsServiceInstanceName, String dnsServiceInstanceExplain, Long dnsServiceTypeCode, Long dnsResolverInstanceID, long recordStatus, String memo)
    {
        ZonedDateTime createDate, updateDate;
        createDate = updateDate = LoulanDNSUtils.getCurrentZonedDateTime();

        DNSServiceInstanceInfo dnsServiceInstanceInfo = createDNSServiceInstanceInfoObject(dnsServiceInstanceID, userID, dnsServiceInstanceName, dnsServiceInstanceExplain, dnsServiceTypeCode, dnsResolverInstanceID, recordStatus, memo, createDate, updateDate );
        return dnsServiceInstanceInfo;
    }

}