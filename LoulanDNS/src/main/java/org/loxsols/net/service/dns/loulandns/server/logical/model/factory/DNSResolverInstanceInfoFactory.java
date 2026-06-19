package org.loxsols.net.service.dns.loulandns.server.logical.model.factory;

import java.util.*;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;



import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;


import org.loxsols.net.service.dns.loulandns.server.logical.model.*;

// LoulanDNSの論理モデルクラスのうちのDNSリゾルバインスタンスオブジェクトのファクトリクラス.
public class DNSResolverInstanceInfoFactory
{
    public DNSResolverInstanceInfo createDNSResolverInstanceInfoObject(Long dnsResolverInstanceID, Long userID, String dnsResolverInstanceName, String dnsResolverInstanceExplain, Long dnsResolverTypeCode, long recordStatus, String memo, ZonedDateTime createDate, ZonedDateTime updateDate, List<DNSResolverInstancePropertyInfo> dnsRespolverProperties)
    {

        DNSResolverInstanceInfo dnsResolverInstanceInfo = new DNSResolverInstanceInfo();

        dnsResolverInstanceInfo.setDnsResolverInstanceID(dnsResolverInstanceID);
        dnsResolverInstanceInfo.setUserID(userID);
        dnsResolverInstanceInfo.setDnsResolverInstanceName(dnsResolverInstanceName);
        dnsResolverInstanceInfo.setDnsResolverInstanceExplain(dnsResolverInstanceExplain);
        dnsResolverInstanceInfo.setDnsResolverTypeCode(dnsResolverTypeCode);
        dnsResolverInstanceInfo.setRecordStatus(recordStatus);
        dnsResolverInstanceInfo.setMemo(memo);
        dnsResolverInstanceInfo.setCreateDate(createDate);
        dnsResolverInstanceInfo.setUpdateDate(updateDate);

        dnsResolverInstanceInfo.setDNSResolverPropertiesInfoList(dnsRespolverProperties);

        return dnsResolverInstanceInfo;
    }

    public DNSResolverInstanceInfo createDNSResolverInstanceInfoObject(Long dnsResolverInstanceID, Long userID, String dnsResolverInstanceName, String dnsResolverInstanceExplain, Long dnsResolverTypeCode, long recordStatus, String memo, String createDate, String updateDate, List<DNSResolverInstancePropertyInfo> dnsRespolverProperties)
    {
        ZonedDateTime createZonedDateTime = LoulanDNSUtils.toDateTimeObject(createDate);
        ZonedDateTime updateZonedDateTime = LoulanDNSUtils.toDateTimeObject(updateDate);

        DNSResolverInstanceInfo dnsResolverInstanceInfo = createDNSResolverInstanceInfoObject(dnsResolverInstanceID, userID, dnsResolverInstanceName, dnsResolverInstanceExplain, dnsResolverTypeCode, recordStatus, memo, createZonedDateTime, updateZonedDateTime, dnsRespolverProperties); 

        return dnsResolverInstanceInfo;
    }


    public DNSResolverInstanceInfo createDNSResolverInstanceInfoObject(Long dnsResolverInstanceID, Long userID, String dnsResolverInstanceName, String dnsResolverInstanceExplain, Long dnsResolverTypeCode, long recordStatus, String memo, ZonedDateTime createDate, ZonedDateTime updateDate )
    {
        DNSResolverInstanceInfo dnsResolverInstanceInfo = createDNSResolverInstanceInfoObject(dnsResolverInstanceID, userID, dnsResolverInstanceName, dnsResolverInstanceExplain, dnsResolverTypeCode, recordStatus, memo, createDate, updateDate, null); 
        return dnsResolverInstanceInfo;
    }

    public DNSResolverInstanceInfo createDNSResolverInstanceInfoObject(Long dnsResolverInstanceID, Long userID, String dnsResolverInstanceName, String dnsResolverInstanceExplain, Long dnsResolverTypeCode, long recordStatus, String memo, String createDate, String updateDate )
    {
        DNSResolverInstanceInfo dnsResolverInstanceInfo = createDNSResolverInstanceInfoObject(dnsResolverInstanceID, userID, dnsResolverInstanceName, dnsResolverInstanceExplain, dnsResolverTypeCode, recordStatus, memo, createDate, updateDate, null); 
        return dnsResolverInstanceInfo;
    }

    public DNSResolverInstanceInfo createDNSResolverInstanceInfoObject(Long dnsResolverInstanceID, Long userID, String dnsResolverInstanceName, String dnsResolverInstanceExplain, Long dnsResolverTypeCode, long recordStatus, String memo )
    {
        ZonedDateTime createZonedDateTime = ZonedDateTime.now();
        ZonedDateTime updateZonedDateTime = createZonedDateTime;

        DNSResolverInstanceInfo dnsResolverInstanceInfo = createDNSResolverInstanceInfoObject(dnsResolverInstanceID, userID, dnsResolverInstanceName, dnsResolverInstanceExplain, dnsResolverTypeCode, recordStatus, memo, createZonedDateTime, updateZonedDateTime ); 
        return dnsResolverInstanceInfo;
    }


}