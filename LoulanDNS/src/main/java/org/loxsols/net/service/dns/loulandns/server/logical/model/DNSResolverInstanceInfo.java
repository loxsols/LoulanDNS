package org.loxsols.net.service.dns.loulandns.server.logical.model;


import java.util.*;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;


@Getter
@Setter
public class DNSResolverInstanceInfo
{
    public Long dnsResolverInstanceID;
    public Long userID;
    public String dnsResolverInstanceName;
    public String dnsResolverInstanceExplain;
    public Long dnsResolverTypeCode;
    public long recordStatus;
    public String memo;
    public ZonedDateTime createDate;
    public ZonedDateTime updateDate;
    
    private List<DNSResolverPropertiesInfo> dnsResolverPropertiesInfoList;

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

    public Long getDNSResolverInstanceID()
    {
        return this.dnsResolverInstanceID;
    }

    public String getDNSResolverInstanceName()
    {
        return this.dnsResolverInstanceName;
    }

    public String getDNSResolverInstanceExplain()
    {
        return this.dnsResolverInstanceExplain;
    }


    // DNSリゾルバのプロパティ情報を取得する.
    public List<DNSResolverPropertiesInfo> getDNSResolverPropertiesInfoList(String propKey)
    {
        List<DNSResolverPropertiesInfo> list = new ArrayList<DNSResolverPropertiesInfo>();
        for( DNSResolverPropertiesInfo info : getDNSResolverPropertiesInfoList() )
        {
            if ( propKey.equals( info.getDNSResolverInstancePropertyKey() ) )
            {
                list.add( info );
            }
        }

        return list;
    }

    public List<DNSResolverPropertiesInfo>  getDNSResolverPropertiesInfoList()
    {

        if( this.dnsResolverPropertiesInfoList == null )
        {
            // プロパティのリストがnullの場合は新規作成する.
            List<DNSResolverPropertiesInfo> list = new ArrayList<DNSResolverPropertiesInfo>();
            this.dnsResolverPropertiesInfoList = list;
        }

        return this.dnsResolverPropertiesInfoList;
    }

    public void setDNSResolverPropertiesInfoList(List<DNSResolverPropertiesInfo> list )
    {
        this.dnsResolverPropertiesInfoList = list;
    }


    // DNSリゾルバのプロパティ情報を、DNSResolverPropertiesIDに基づいて取得する.
    public DNSResolverPropertiesInfo getDNSPropertiesInfoList(long dnsResolverPropertitesID)
    {
        List<DNSResolverPropertiesInfo> list = getDNSResolverPropertiesInfoList();
        
        for( DNSResolverPropertiesInfo info : list )
        {
            if ( info.getDNSResolverInstancePropertyID() == dnsResolverPropertitesID)
            {
                return info;
            }
        }

        return null;
    }


    public boolean hasProperties(String propKey)
    {
        List<DNSResolverPropertiesInfo> list = getDNSResolverPropertiesInfoList( propKey );
        if ( list.size() > 0 )
        {
            return true;
        }

        return false;
    }


    DNSResolverPropertiesInfo getDNSResolverPropertiesInfo(long propID)
    {
        for( DNSResolverPropertiesInfo info  : getDNSResolverPropertiesInfoList() )
        {
            if ( propID == info.getDNSResolverPropertyID() )
            {
                return info;
            }
        }

        return null;
    }

    public boolean hasProperties(long propID)
    {
        DNSResolverPropertiesInfo info = getDNSResolverPropertiesInfo( propID );
        if ( info != null )
        {
            return true;
        }

        return false;
    }


    /**
     * DNSリゾルバインスタンスのプロパティ情報をjava.util.Propertiesクラスに変換して返す.
     * 
     * @return
     */
    public Properties getProperties()
    {
        Properties properties = new Properties();

        List<DNSResolverPropertiesInfo> list = getDNSResolverPropertiesInfoList();
        for( DNSResolverPropertiesInfo propInfo : list )
        {
            String key = propInfo.getDnsResolverPropertyKey();
            String value = propInfo.getDnsResolverPropertyValue();

            properties.put(key, value);
        }

        return properties;
    }


}