package org.loxsols.net.service.dns.loulandns.server.logical.model;


import java.util.*;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;


import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;


@Getter
@Setter
public class DNSResolverTypePropertiesInfo
{

    public Long dnsResolverTypePropertyID;
    public Long dnsResolverTypeID;
    public String dnsResolverTypePropertyKey;
    public String dnsResolverTypePropertyValue;
    public String dnsResolverTypePropertyExplain;
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

    public Long getDNSResolverTypePropertyID()
    {
        return this.dnsResolverTypePropertyID;
    }





}