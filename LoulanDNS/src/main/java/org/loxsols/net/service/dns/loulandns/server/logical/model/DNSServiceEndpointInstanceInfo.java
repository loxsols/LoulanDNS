package org.loxsols.net.service.dns.loulandns.server.logical.model;


import java.util.*;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;


import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;



/**
 * DNSサービスのエンドポイントインスタンスの論理モデルクラス.
 */
@Getter
@Setter
public class DNSServiceEndpointInstanceInfo
{

    public Long dnsServiceEndpointInstanceID;
    public Long dnsServiceInstanceID;
    public String dnsServiceEndpointInstanceName;
    public String dnsServiceEndpointInstanceExplain;
    public Long dnsServiceEndpointTypeCode;
    public Long recordStatus;
    public String memo;
    public ZonedDateTime createDate;
    public ZonedDateTime updateDate;

    List<DNSServiceEndpointInstancePropertyInfo> dnsServiceEndpointInstanceProperties;


    public DNSServiceEndpointInstanceInfo()
    {
        this.dnsServiceEndpointInstanceProperties = new ArrayList<DNSServiceEndpointInstancePropertyInfo>();
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



    public Long getDNSServiceEndpointInstanceID()
    {
        return this.dnsServiceEndpointInstanceID;        
    }

    public void setDNSServiceEndpointInstanceID(Long dnsServiceEndpointInstanceID)
    {
        this.dnsServiceEndpointInstanceID = dnsServiceEndpointInstanceID;
    }


    public Long getDNSServiceInstanceID()
    {
        return this.dnsServiceInstanceID;
    }

    public void setDNSServiceInstanceID(Long dnsServiceInstanceID)
    {
        this.dnsServiceInstanceID = dnsServiceInstanceID;
    }



    public String getDNSServiceEndpointInstanceName()
    {
        return this.dnsServiceEndpointInstanceName;
    }

    public void setDNSServiceEndpointInstanceName(String dnsServiceEndpointInstanceName)
    {
        this.dnsServiceEndpointInstanceName = dnsServiceEndpointInstanceName;
    }

    public String getDNSServiceEndpointInstanceExplain()
    {
        return this.dnsServiceEndpointInstanceExplain;
    }

    public void setDNSServiceEndpointInstanceExplain(String dnsServiceEndpointInstanceExplain)
    {
        this.dnsServiceEndpointInstanceExplain = dnsServiceEndpointInstanceExplain;
    }



    public Long getDNSServiceEndpointTypeCode()
    {
        return this.dnsServiceEndpointTypeCode;
    }

    public void setDNSServiceEndpointTypeCode(Long dnsServiceEndpointTypeCode)
    {
        this.dnsServiceEndpointTypeCode = dnsServiceEndpointTypeCode;
    }




    public List<DNSServiceEndpointInstancePropertyInfo> getDNSServiceEndpointInstanceProperties()
    {
        return this.dnsServiceEndpointInstanceProperties;
    }


    public DNSServiceEndpointInstancePropertyInfo getDNSServiceEndpointInstanceProperty(String key)
    {
        List<DNSServiceEndpointInstancePropertyInfo> list = getDNSServiceEndpointInstanceProperties();
        for( var item : list )
        {
            if ( item.getDNSServiceEndpointInstancePropertyKey().equals(key) )
            {
                return item;
            }
        }

        return null;
    }


    /**
     * DNSサービスインスタンスのプロパティをjava.util.Propertiesクラス形式で返す.
     * @return
     */
    public Properties getProperties()
    {
        Properties properties = new Properties();

        List<DNSServiceEndpointInstancePropertyInfo> list = getDNSServiceEndpointInstanceProperties();

        if ( list != null )
        {
            for( DNSServiceEndpointInstancePropertyInfo info : list )
            {
                String key = info.getDNSServiceEndpointInstancePropertyKey();
                String value = info.getDNSServiceEndpointInstancePropertyValue();

                properties.put(key, value);
            }
        }

        return properties;
    }

}