package org.loxsols.net.service.dns.loulandns.server.logical.model;


import java.util.*;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;


@Getter
@Setter
public class DNSResolverInstancePropertyInfo
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

    public void setCreateDate(String createDate) throws LoulanDNSSystemServiceException
    {
        ZonedDateTime dateTime;
        
        try
        {
            dateTime = LoulanDNSUtils.toDateTimeObject(createDate);
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to set DNSResolverInstancePropertyInfo createDate. value=%s", createDate);
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg, cause);
            throw exception;
        }

        this.createDate = dateTime;
    }

    public void setUpdateDate(String updateDate) throws LoulanDNSSystemServiceException
    {
        ZonedDateTime dateTime;

        try
        {
            dateTime = LoulanDNSUtils.toDateTimeObject(updateDate);
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to set DNSResolverInstancePropertyInfo updateDate. value=%s", updateDate);
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg, cause);
            throw exception;
        }

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