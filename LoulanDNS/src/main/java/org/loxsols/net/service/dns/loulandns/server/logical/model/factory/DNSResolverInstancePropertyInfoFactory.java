package org.loxsols.net.service.dns.loulandns.server.logical.model.factory;

import java.util.*;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;



import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;


import org.loxsols.net.service.dns.loulandns.server.logical.model.*;

// LoulanDNSの論理モデルクラスのうちのDNSサービスインスタンスのプロパティオブジェクトのファクトリクラス.
public class DNSResolverInstancePropertyInfoFactory
{
    public DNSResolverInstancePropertyInfo createDNSResolverInstancePropertiesInfoObject(Long dnsResolverInstanceID,  String dnsResolverInstancePropertyKey, String dnsResolverInstancePropertyValue, String dnsResolverInstancePropertyExplain, long recordStatus, String memo, String createDate, String updateDate)
    {
        DNSResolverInstancePropertyInfo dnsResolverInstancePropertyInfo = new DNSResolverInstancePropertyInfo();

        // ID値はnullで設定する.
        dnsResolverInstancePropertyInfo.setDNSResolverInstancePropertyID(null);

        // 本レコード固有の項目.
        dnsResolverInstancePropertyInfo.setDnsResolverID(dnsResolverInstanceID);
        dnsResolverInstancePropertyInfo.setDnsResolverPropertyKey(dnsResolverInstancePropertyKey);
        dnsResolverInstancePropertyInfo.setDnsResolverPropertyValue(dnsResolverInstancePropertyValue);
        dnsResolverInstancePropertyInfo.setDnsResolverPropertyExplain(dnsResolverInstancePropertyExplain);

        // LoulanDNSのDBテーブル共通項目.
        dnsResolverInstancePropertyInfo.setRecordStatus(recordStatus);

        dnsResolverInstancePropertyInfo.setCreateDate(createDate);
        dnsResolverInstancePropertyInfo.setUpdateDate(updateDate);

        return dnsResolverInstancePropertyInfo;
    }


    public DNSResolverInstancePropertyInfo createDNSResolverInstancePropertiesInfoObject(Long dnsResolverInstanceID,  String dnsResolverInstancePropertyKey, String dnsResolverInstancePropertyValue, String dnsResolverInstancePropertyExplain, long recordStatus, String memo, ZonedDateTime createZonedDateTime, ZonedDateTime updateZonedDateTime)
    {
        String createDate = LoulanDNSUtils.toDateTimeString(createZonedDateTime);
        String updateDate = LoulanDNSUtils.toDateTimeString(updateZonedDateTime);

        DNSResolverInstancePropertyInfo dnsResolverInstancePropertyInfo = 
                createDNSResolverInstancePropertiesInfoObject(dnsResolverInstanceID, dnsResolverInstancePropertyKey, dnsResolverInstancePropertyValue, dnsResolverInstancePropertyExplain, recordStatus, memo, createDate, updateDate);

        return dnsResolverInstancePropertyInfo;
    }

    public DNSResolverInstancePropertyInfo createDNSResolverInstancePropertiesInfoObject(Long dnsResolverInstanceID,  String dnsResolverInstancePropertyKey, String dnsResolverInstancePropertyValue, String dnsResolverInstancePropertyExplain, long recordStatus, String memo )
    {
        ZonedDateTime createZonedDateTime, updateZonedDateTime;
        createZonedDateTime = updateZonedDateTime = LoulanDNSUtils.getCurrentZonedDateTime();

        DNSResolverInstancePropertyInfo dnsResolverInstancePropertyInfo = 
                createDNSResolverInstancePropertiesInfoObject(dnsResolverInstanceID, dnsResolverInstancePropertyKey, dnsResolverInstancePropertyValue, dnsResolverInstancePropertyExplain, recordStatus, memo, createZonedDateTime, updateZonedDateTime);

        return dnsResolverInstancePropertyInfo;
    }


}