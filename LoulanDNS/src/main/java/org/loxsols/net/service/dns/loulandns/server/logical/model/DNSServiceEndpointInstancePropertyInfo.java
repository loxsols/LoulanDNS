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
public class DNSServiceEndpointInstancePropertyInfo
{
    public Long dnsServiceEndpointInstancePropertyID;
    public Long dnsServiceEndpointInstanceID;

    public String dnsServiceEndpointInstancePropertyKey;
    public String dnsServiceEndpointInstancePropertyValue;
    public String dnsServiceEndpointInstancePropertyExplain;
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


    public Long getDNSServiceEndpointInstancePropertyID()
    {
        return dnsServiceEndpointInstancePropertyID;
    }

    public void setDNSServiceEndpointInstancePropertyID(Long value)
    {
        this.dnsServiceEndpointInstancePropertyID = value;
    }

    public Long getDNSServiceEndpointInstanceID()
    {
        return dnsServiceEndpointInstanceID;        
    }

    public void setDNSServiceEndpointInstanceID(Long value)
    {
        this.dnsServiceEndpointInstanceID = value;
    }

    public String getDNSServiceEndpointInstancePropertyKey()
    {
        return dnsServiceEndpointInstancePropertyKey;
    }

    public void setDNSServiceEndpointInstancePropertyKey(String value)
    {
        this.dnsServiceEndpointInstancePropertyKey = value;
    }

    public String getDNSServiceEndpointInstancePropertyValue()
    {
        return dnsServiceEndpointInstancePropertyValue;
    }

    public void setDNSServiceEndpointInstancePropertyValue(String value)
    {
        this.dnsServiceEndpointInstancePropertyValue = value;
    }

    public String getDNSServiceEndpointInstancePropertyExplain()
    {
        return dnsServiceEndpointInstancePropertyExplain;
    }

    public void setDNSServiceEndpointInstancePropertyExplain(String value)
    {
        this.dnsServiceEndpointInstancePropertyExplain = value;
    }


}