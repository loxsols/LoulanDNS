package org.loxsols.net.service.dns.loulandns.server.logical.model;


import java.util.*;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;


import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;



/**
 * DNSサービスインスタンスのプロパティ情報の論理モデルクラス.
 */
@Getter
@Setter
public class DNSServiceInstancePropertyInfo
{
    public Long dnsServiceInstancePropertyID;
    public Long dnsServiceInstanceID;

    public String dnsServiceInstancePropertyKey;
    public String dnsServiceInstancePropertyValue;
    public String dnsServiceInstancePropertyExplain;
    public long recordStatus;
    public String memo;
    public ZonedDateTime createDate;
    public ZonedDateTime updateDate;



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