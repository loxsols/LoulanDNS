package org.loxsols.net.service.dns.loulandns.server.logical.model;


import java.util.*;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;


import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;


@Getter
@Setter
public class DNSResolverPropertiesInfo
{
    public Long dnsResolverPropertyID;
    public Long dnsResolverID;
    public String dnsResolverPropertyKey;
    public String dnsResolverPropertyValue;
    public String dnsResolverPropertyExplain;
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

    public Long getDNSResolverPropertyID()
    {
        return this.dnsResolverPropertyID;
    }

    public void setDNSResolverPropertyID(Long dnsResolverPropertyID)
    {
        this.dnsResolverPropertyID = dnsResolverPropertyID;
    }


    public Long getDNSResolverInstancePropertyID()
    {
        return getDNSResolverPropertyID();
    }

    public void setDNSResolverInstancePropertyID(Long dnsResolverPropertyID)
    {
        setDNSResolverPropertyID( dnsResolverPropertyID );
    }


    public String getDNSResolverInstancePropertyKey()
    {
        return this.dnsResolverPropertyKey;
    }

    

}