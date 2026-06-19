package org.loxsols.net.service.dns.loulandns.server.logical.model.factory;

import java.util.*;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;



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

    public DNSServiceInstanceInfo createDNSServiceInstanceInfoObject(Long dnsServiceInstanceID, Long userID, String dnsServiceInstanceName, String dnsServiceInstanceExplain, Long dnsServiceTypeCode, Long dnsResolverInstanceID, long recordStatus, String memo, String createDateString, String updateDateString)
    {
        ZonedDateTime createDate = LoulanDNSUtils.toDateTimeObject(createDateString);
        ZonedDateTime updateDate = LoulanDNSUtils.toDateTimeObject(updateDateString);

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