package org.loxsols.net.service.dns.loulandns.server.logical.model;


import java.util.*;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;


import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;


@Getter
@Setter
public class DNSResolverTypeInfo
{

    public Long dnsResolverTypeID;
    public Long dnsResolverTypeCode;
    public String dnsResolverTypeName;
    public String dnsResolverTypeExplain;
    public String dnsResolverClassName;
    public String dnsResolverClassLoaderName;
    public String dnsResolverOption1;
    public String dnsResolverOption2;
    public String dnsResolverOption3;
    public long recordStatus;
    public String memo;
    public ZonedDateTime createDate;
    public ZonedDateTime updateDate;


    private List<DNSResolverTypePropertiesInfo> dnsResolverTypePropertiesInfoList;


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

    public Long getDNSResolverTypeID()
    {
        return this.dnsResolverTypeID;
    }





}