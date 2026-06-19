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
    
    private List<DNSResolverInstancePropertyInfo> dnsResolverPropertiesInfoList;

    public void setCreateDate(String createDate)
    {
        ZonedDateTime dateTime = LoulanDNSUtils.toDateTimeObject(createDate);
        setCreateDate(dateTime);
    }

    public void setCreateDate(ZonedDateTime createDate)
    {
        this.createDate = createDate;
    }


    public void setUpdateDate(String updateDate)
    {
        ZonedDateTime dateTime = LoulanDNSUtils.toDateTimeObject(updateDate);
        setUpdateDate( dateTime );
    }

    public void setUpdateDate(ZonedDateTime updateDate)
    {
        this.updateDate = updateDate;
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
    public List<DNSResolverInstancePropertyInfo> getDNSResolverPropertiesInfoList(String propKey)
    {
        List<DNSResolverInstancePropertyInfo> list = new ArrayList<DNSResolverInstancePropertyInfo>();
        for( DNSResolverInstancePropertyInfo info : getDNSResolverPropertiesInfoList() )
        {
            if ( propKey.equals( info.getDNSResolverInstancePropertyKey() ) )
            {
                list.add( info );
            }
        }

        return list;
    }

    public List<DNSResolverInstancePropertyInfo>  getDNSResolverPropertiesInfoList()
    {

        if( this.dnsResolverPropertiesInfoList == null )
        {
            // プロパティのリストがnullの場合は新規作成する.
            List<DNSResolverInstancePropertyInfo> list = new ArrayList<DNSResolverInstancePropertyInfo>();
            this.dnsResolverPropertiesInfoList = list;
        }

        return this.dnsResolverPropertiesInfoList;
    }

    public void setDNSResolverPropertiesInfoList(List<DNSResolverInstancePropertyInfo> list )
    {
        this.dnsResolverPropertiesInfoList = list;
    }


    // DNSリゾルバのプロパティ情報を、DNSResolverPropertiesIDに基づいて取得する.
    public DNSResolverInstancePropertyInfo getDNSPropertiesInfo(long dnsResolverPropertitesID)
    {
        List<DNSResolverInstancePropertyInfo> list = getDNSResolverPropertiesInfoList();
        
        for( DNSResolverInstancePropertyInfo info : list )
        {
            if ( info.getDNSResolverInstancePropertyID() == dnsResolverPropertitesID)
            {
                return info;
            }
        }

        return null;
    }

    // DNSリゾルバのプロパティ情報を、プロパティキーに基づいて取得する.
    public DNSResolverInstancePropertyInfo getDNSPropertiesInfo(String key)
    {
        List<DNSResolverInstancePropertyInfo> list = getDNSResolverPropertiesInfoList();
        
        for( DNSResolverInstancePropertyInfo info : list )
        {
            if ( info.getDNSResolverInstancePropertyKey().equals(key) )
            {
                return info;
            }
        }

        return null;
    }


    public boolean hasProperties(String propKey)
    {
        List<DNSResolverInstancePropertyInfo> list = getDNSResolverPropertiesInfoList(propKey);
        if ( list.size() > 0 )
        {
            return true;
        }

        return false;
    }


    DNSResolverInstancePropertyInfo getDNSResolverPropertiesInfo(long propID)
    {
        for( DNSResolverInstancePropertyInfo info  : getDNSResolverPropertiesInfoList() )
        {
            if ( info.getDNSResolverPropertyID() == null )
            {
                continue;
            }

            if ( propID == info.getDNSResolverPropertyID() )
            {
                return info;
            }
        }

        return null;
    }

    public boolean hasProperties(long propID)
    {
        DNSResolverInstancePropertyInfo info = getDNSResolverPropertiesInfo( propID );
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

        List<DNSResolverInstancePropertyInfo> list = getDNSResolverPropertiesInfoList();
        for( DNSResolverInstancePropertyInfo propInfo : list )
        {
            String key = propInfo.getDnsResolverPropertyKey();
            String value = propInfo.getDnsResolverPropertyValue();

            properties.put(key, value);
        }

        return properties;
    }


}