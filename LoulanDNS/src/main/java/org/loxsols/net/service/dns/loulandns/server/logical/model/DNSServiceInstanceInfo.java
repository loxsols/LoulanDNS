package org.loxsols.net.service.dns.loulandns.server.logical.model;


import java.util.*;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;


import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;



/**
 * DNSサービスインスタンスの論理モデルクラス.
 */
@Getter
@Setter
public class DNSServiceInstanceInfo
{
    public Long dnsServiceInstanceID;
    public Long userID;

    public String dnsServiceInstanceName;
    public String dnsServiceInstanceExplain;
    public Long dnsServiceTypeCode;
    
    public Long dnsResolverInstanceID;
    public long recordStatus;
    public String memo;
    public ZonedDateTime createDate;
    public ZonedDateTime updateDate;

    List<DNSServiceInstancePropertyInfo> dnsServiceInstanceProperties;

    public DNSResolverInstanceInfo dnsResolverInstanceInfo;

    public List<DNSServiceEndpointInstanceInfo> dnsServiceEndpointInstanceInfoList;


    /**
     * コンストラクタ
     * 
     */
    public DNSServiceInstanceInfo()
    {
        this.dnsServiceInstanceProperties = new ArrayList<DNSServiceInstancePropertyInfo>();
        this.dnsServiceEndpointInstanceInfoList = new ArrayList<DNSServiceEndpointInstanceInfo>();
    }



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
        setUpdateDate(dateTime);
    }

    public void setUpdateDate(ZonedDateTime updateDate)
    {
        this.updateDate = updateDate;
    }



    public DNSResolverInstanceInfo getDNSResolverInfo()
    {
        return dnsResolverInstanceInfo;
    }

    public void setDNSResolverInstanceInfo(DNSResolverInstanceInfo info)
    {
        this.dnsResolverInstanceInfo = info;
    }

    public Long getDNSServiceInstanceID()
    {
        return this.dnsServiceInstanceID;
    }

    public void setDNSServiceInstanceID(Long dnsServiceInstanceID)
    {
        this.dnsServiceInstanceID = dnsServiceInstanceID;
    }


    public Long getDNSResolverInstanceID()
    {
        return this.dnsResolverInstanceID;
    }

    public void setDNSResolverInstanceID(long dnsResolverInstanceID)
    {
        this.dnsResolverInstanceID = dnsResolverInstanceID;
    }


    public String getDNSServiceInstanceExplain()
    {
        return this.dnsServiceInstanceExplain;
    }

    public void setDNSServiceInstanceExplain(String dnsServiceInstanceExplain)
    {
        this.dnsServiceInstanceExplain = dnsServiceInstanceExplain;
    }

    public String getDNSServiceInstanceName()
    {
        return this.dnsServiceInstanceName;
    }

    public void setDNSServiceInstanceName(String dnsServiceInstanceName)
    {
        this.dnsServiceInstanceName = dnsServiceInstanceName;
    }




    public List<DNSServiceInstancePropertyInfo> getDNSServiceInstanceProperties()
    {
        return this.dnsServiceInstanceProperties;
    }

    /**
     * DNSサービスインスタンスのプロパティエントリを返す.
     * @param key
     * @return
     */
    public DNSServiceInstancePropertyInfo getDNSServiceInstanceProperty(String key)
    {
        DNSServiceInstancePropertyInfo dnsServiceInstancePropertyInfo = null;

        for( DNSServiceInstancePropertyInfo item : getDNSServiceInstanceProperties() )
        {
            if ( item.dnsServiceInstancePropertyKey.equals(key) )
            {
                dnsServiceInstancePropertyInfo = item;
                break;
            }
        }

        return dnsServiceInstancePropertyInfo;
    }


    /**
     * DNSサービスインスタンスのプロパティをjava.util.Propertiesクラス形式で返す.
     * @return
     */
    public Properties getProperties()
    {
        Properties properties = new Properties();

        List<DNSServiceInstancePropertyInfo> list = getDNSServiceInstanceProperties();

        if ( list != null )
        {
            for( DNSServiceInstancePropertyInfo info : list )
            {
                String key = info.getDnsServiceInstancePropertyKey();
                String value = info.getDnsServiceInstancePropertyValue();

                properties.put(key, value);
            }
        }

        return properties;
    }


    public List<DNSServiceEndpointInstanceInfo> getDNSServiceEndpointInstanceInfoList()
    {

        return dnsServiceEndpointInstanceInfoList;
    }

    public void setDNSServiceEndpointInstanceInfoList(List<DNSServiceEndpointInstanceInfo> list)
    {
        this.dnsServiceEndpointInstanceInfoList = list;
    }

}