package org.loxsols.net.service.dns.loulandns.server.logical.model.factory;

import java.util.*;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;



import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;


import org.loxsols.net.service.dns.loulandns.server.logical.model.*;

// LoulanDNSの論理モデルクラスのうちのDNSサービスインスタンスのプロパティオブジェクトのファクトリクラス.
public class DNSServiceInstancePropertyInfoFactory
{
    public DNSServiceInstancePropertyInfo createDNSServiceInstancePropertiesInfoObject(Long dnsServiceInstanceID,  String dnsServiceInstancePropertyKey, String dnsServiceInstancePropertyValue, String dnsServiceInstancePropertyExplain, long recordStatus, String memo, String createDate, String updateDate)
    {
        DNSServiceInstancePropertyInfo dnsServiceInstancePropertyInfo = new DNSServiceInstancePropertyInfo();

        // ID値はnullで設定する.
        dnsServiceInstancePropertyInfo.setDnsServiceInstancePropertyID(null);

        // 本レコード固有の項目.
        dnsServiceInstancePropertyInfo.setDnsServiceInstanceID(dnsServiceInstanceID);
        dnsServiceInstancePropertyInfo.setDnsServiceInstancePropertyKey(dnsServiceInstancePropertyKey);
        dnsServiceInstancePropertyInfo.setDnsServiceInstancePropertyValue(dnsServiceInstancePropertyValue);
        dnsServiceInstancePropertyInfo.setDnsServiceInstancePropertyExplain(dnsServiceInstancePropertyExplain);

        // LoulanDNSのDBテーブル共通項目.
        dnsServiceInstancePropertyInfo.setRecordStatus(recordStatus);

        dnsServiceInstancePropertyInfo.setCreateDate(createDate);
        dnsServiceInstancePropertyInfo.setUpdateDate(updateDate);

        return dnsServiceInstancePropertyInfo;
    }


    public DNSServiceInstancePropertyInfo createDNSServiceInstancePropertiesInfoObject(Long dnsServiceInstanceID,  String dnsServiceInstancePropertyKey, String dnsServiceInstancePropertyValue, String dnsServiceInstancePropertyExplain, long recordStatus, String memo, ZonedDateTime createZonedDateTime, ZonedDateTime updateZonedDateTime)
    {
        String createDate = LoulanDNSUtils.toDateTimeString(createZonedDateTime);
        String updateDate = LoulanDNSUtils.toDateTimeString(updateZonedDateTime);

        DNSServiceInstancePropertyInfo dnsServiceInstancePropertyInfo = 
                createDNSServiceInstancePropertiesInfoObject(dnsServiceInstanceID, dnsServiceInstancePropertyKey, dnsServiceInstancePropertyValue, dnsServiceInstancePropertyExplain, recordStatus, memo, createDate, updateDate);

        return dnsServiceInstancePropertyInfo;
    }

    public DNSServiceInstancePropertyInfo createDNSServiceInstancePropertiesInfoObject(Long dnsServiceInstanceID,  String dnsServiceInstancePropertyKey, String dnsServiceInstancePropertyValue, String dnsServiceInstancePropertyExplain, long recordStatus, String memo )
    {
        ZonedDateTime createZonedDateTime, updateZonedDateTime;
        createZonedDateTime = updateZonedDateTime = LoulanDNSUtils.getCurrentZonedDateTime();

        DNSServiceInstancePropertyInfo dnsServiceInstancePropertyInfo = 
                createDNSServiceInstancePropertiesInfoObject(dnsServiceInstanceID, dnsServiceInstancePropertyKey, dnsServiceInstancePropertyValue, dnsServiceInstancePropertyExplain, recordStatus, memo, createZonedDateTime, updateZonedDateTime);

        return dnsServiceInstancePropertyInfo;
    }


}