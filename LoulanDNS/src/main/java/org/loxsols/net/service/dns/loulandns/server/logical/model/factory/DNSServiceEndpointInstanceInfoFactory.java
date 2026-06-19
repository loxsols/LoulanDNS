package org.loxsols.net.service.dns.loulandns.server.logical.model.factory;

import java.util.*;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;



import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;


import org.loxsols.net.service.dns.loulandns.server.logical.model.*;

// LoulanDNSの論理モデルクラスのうちのDNSサービスエンドポイントインスタンスオブジェクトのファクトリクラス.
public class DNSServiceEndpointInstanceInfoFactory
{
    public DNSServiceEndpointInstanceInfo createDNSServiceEndpointInstanceInfoObject(Long dnsServiceEndpointInstanceID, Long dnsServiceInstanceID, String dnsServiceEndpointInstanceName, String dnsServiceEndpointInstanceExplain, Long dnsServiceEndpointTypeCode, long recordStatus, String memo, ZonedDateTime createDate, ZonedDateTime updateDate)
    {
        DNSServiceEndpointInstanceInfo dnsServiceEndpointInstanceInfo = new DNSServiceEndpointInstanceInfo();
        
        dnsServiceEndpointInstanceInfo.setDNSServiceEndpointInstanceID(dnsServiceEndpointInstanceID);
        dnsServiceEndpointInstanceInfo.setDNSServiceInstanceID(dnsServiceInstanceID);
        dnsServiceEndpointInstanceInfo.setDNSServiceEndpointInstanceName(dnsServiceEndpointInstanceName);
        dnsServiceEndpointInstanceInfo.setDNSServiceEndpointInstanceExplain(dnsServiceEndpointInstanceExplain);
        dnsServiceEndpointInstanceInfo.setDNSServiceEndpointTypeCode(dnsServiceEndpointTypeCode);

        dnsServiceEndpointInstanceInfo.setRecordStatus(recordStatus);
        dnsServiceEndpointInstanceInfo.setMemo(memo);
        dnsServiceEndpointInstanceInfo.setCreateDate(createDate);
        dnsServiceEndpointInstanceInfo.setUpdateDate(updateDate);

        return dnsServiceEndpointInstanceInfo;
    }

    public DNSServiceEndpointInstanceInfo createDNSServiceEndpointInstanceInfoObject(Long dnsServiceEndpointInstanceID, Long dnsServiceInstanceID, String dnsServiceEndpointInstanceName, String dnsServiceEndpointInstanceExplain, Long dnsServiceEndpointTypeCode, long recordStatus, String memo, String createDateString, String updateDateString)
    {
        ZonedDateTime createDate = LoulanDNSUtils.toDateTimeObject(createDateString);
        ZonedDateTime updateDate = LoulanDNSUtils.toDateTimeObject(updateDateString);

        DNSServiceEndpointInstanceInfo dnsServiceEndpointInstanceInfo = createDNSServiceEndpointInstanceInfoObject(dnsServiceEndpointInstanceID, dnsServiceInstanceID, dnsServiceEndpointInstanceName, dnsServiceEndpointInstanceExplain, dnsServiceEndpointTypeCode, recordStatus, memo, createDate, updateDate );
        return dnsServiceEndpointInstanceInfo;
    }

    public DNSServiceEndpointInstanceInfo createDNSServiceEndpointInstanceInfoObject(Long dnsServiceEndpointInstanceID, Long dnsServiceInstanceID, String dnsServiceEndpointInstanceName, String dnsServiceEndpointInstanceExplain, Long dnsServiceEndpointTypeCode, long recordStatus, String memo)
    {
        ZonedDateTime createDate, updateDate;
        createDate = updateDate = LoulanDNSUtils.getCurrentZonedDateTime();

        DNSServiceEndpointInstanceInfo dnsServiceEndpointInstanceInfo = createDNSServiceEndpointInstanceInfoObject( dnsServiceEndpointInstanceID, dnsServiceInstanceID, dnsServiceEndpointInstanceName, dnsServiceEndpointInstanceExplain, dnsServiceEndpointTypeCode, recordStatus, memo, createDate, updateDate );
        return dnsServiceEndpointInstanceInfo;
    }

}