package org.loxsols.net.service.dns.loulandns.server.logical.model.factory;

import java.util.*;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;



import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;


import org.loxsols.net.service.dns.loulandns.server.logical.model.*;

// LoulanDNSの論理モデルクラスのうちのDNSサービスエンドポイントインスタンスのプロパティオブジェクトのファクトリクラス.
public class DNSServiceEndpointInstancePropertyInfoFactory
{
    public DNSServiceEndpointInstancePropertyInfo createDNSServiceEndpointInstancePropertyInfoObject(Long dnsServiceEndpointInstanceID,  String dnsServiceEndpointInstancePropertyKey, String dnsServiceEndpointInstancePropertyValue, String dnsServiceEndpointInstancePropertyExplain, long recordStatus, String memo, String createDate, String updateDate)
    {
        DNSServiceEndpointInstancePropertyInfo dnsServiceEndpointInstancePropertyInfo = new DNSServiceEndpointInstancePropertyInfo();

        // ID値はnullで設定する.
        dnsServiceEndpointInstancePropertyInfo.setDNSServiceEndpointInstancePropertyID( null );

        // 本レコード固有の項目.
        dnsServiceEndpointInstancePropertyInfo.setDNSServiceEndpointInstanceID( dnsServiceEndpointInstanceID );
        dnsServiceEndpointInstancePropertyInfo.setDNSServiceEndpointInstancePropertyKey( dnsServiceEndpointInstancePropertyKey );
        dnsServiceEndpointInstancePropertyInfo.setDNSServiceEndpointInstancePropertyValue( dnsServiceEndpointInstancePropertyValue );
        dnsServiceEndpointInstancePropertyInfo.setDNSServiceEndpointInstancePropertyExplain( dnsServiceEndpointInstancePropertyExplain );

        // LoulanDNSのDBテーブル共通項目.
        dnsServiceEndpointInstancePropertyInfo.setRecordStatus(recordStatus);

        dnsServiceEndpointInstancePropertyInfo.setCreateDate(createDate);
        dnsServiceEndpointInstancePropertyInfo.setUpdateDate(updateDate);

        return dnsServiceEndpointInstancePropertyInfo;
    }


    public DNSServiceEndpointInstancePropertyInfo createDNSServiceEndpointInstancePropertyInfoObject(Long dnsServiceEndpointInstanceID,  String dnsServiceEndpointInstancePropertyKey, String dnsServiceEndpointInstancePropertyValue, String dnsServiceEndpointInstancePropertyExplain, long recordStatus, String memo, ZonedDateTime createZonedDateTime, ZonedDateTime updateZonedDateTime)
    {
        String createDate = LoulanDNSUtils.toDateTimeString(createZonedDateTime);
        String updateDate = LoulanDNSUtils.toDateTimeString(updateZonedDateTime);

        DNSServiceEndpointInstancePropertyInfo dnsServiceEndpointInstancePropertyInfo = 
                createDNSServiceEndpointInstancePropertyInfoObject(dnsServiceEndpointInstanceID, dnsServiceEndpointInstancePropertyKey, dnsServiceEndpointInstancePropertyValue, dnsServiceEndpointInstancePropertyExplain, recordStatus, memo, createDate, updateDate);

        return dnsServiceEndpointInstancePropertyInfo;
    }

    public DNSServiceEndpointInstancePropertyInfo createDNSServiceEndpointInstancePropertyInfoObject(Long dnsServiceEndpointInstanceID,  String dnsServiceEndpointInstancePropertyKey, String dnsServiceEndpointInstancePropertyValue, String dnsServiceEndpointInstancePropertyExplain, long recordStatus, String memo )
    {
        ZonedDateTime createZonedDateTime, updateZonedDateTime;
        createZonedDateTime = updateZonedDateTime = LoulanDNSUtils.getCurrentZonedDateTime();

        DNSServiceEndpointInstancePropertyInfo dnsServiceEndpointInstancePropertyInfo = 
                createDNSServiceEndpointInstancePropertyInfoObject(dnsServiceEndpointInstanceID, dnsServiceEndpointInstancePropertyKey, dnsServiceEndpointInstancePropertyValue, dnsServiceEndpointInstancePropertyExplain, recordStatus, memo, createZonedDateTime, updateZonedDateTime);

        return dnsServiceEndpointInstancePropertyInfo;
    }


}