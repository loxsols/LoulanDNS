package org.loxsols.net.service.dns.loulandns.server.logical.model;


import java.util.*;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;
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



    public void setCreateDate(String createDate) throws LoulanDNSSystemServiceException
    {
        ZonedDateTime dateTime;
        try
        {
            dateTime = LoulanDNSUtils.toDateTimeObject(createDate);
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to set createDate to %s. value=%s", this.getClass().getName(), createDate);
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg, cause);
            throw exception;
        }

        setCreateDate(dateTime);
    }

    public void setCreateDate(ZonedDateTime createDate)
    {
        this.createDate = createDate;
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
            String msg = String.format("Failed to set updateDate to %s. value=%s", this.getClass().getName(), updateDate);
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg, cause);
            throw exception;
        }

        setUpdateDate(dateTime);
    }

    public void setUpdateDate(ZonedDateTime updateDate)
    {
        this.updateDate = updateDate;
    }


    /**
     * 日付時刻情報は文字列化して返す.
     * WebAPI呼び出し時にJSONシリアライズした際に、タイムゾーン情報が適切に付加されない(JSTやAsia/Tokyoではなく、+09000表記になる)ため.
     * 時刻情報自体はローカルタイムゾーン値(JST時刻)なのに、タイムゾーン情報が+0900だと、東京時刻から更に9時間プラスされてしまう....
     * 
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public String getCreateDate() throws LoulanDNSSystemServiceException
    {
        String dateTimeString = LoulanDNSUtils.toDateTimeString( this.createDate );
        return dateTimeString;
    }


    /**
     * 日付時刻情報は文字列化して返す.
     * WebAPI呼び出し時にJSONシリアライズした際に、タイムゾーン情報が適切に付加されない(JSTやAsia/Tokyoではなく、+09000表記になる)ため.
     * 時刻情報自体はローカルタイムゾーン値(JST時刻)なのに、タイムゾーン情報が+0900だと、東京時刻から更に9時間プラスされてしまう....
     * 
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public String getUpdateDate() throws LoulanDNSSystemServiceException
    {
        String dateTimeString = LoulanDNSUtils.toDateTimeString( this.updateDate );
        return dateTimeString;
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