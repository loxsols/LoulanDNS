package org.loxsols.net.service.dns.loulandns.server.logical.model;


import java.util.*;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;
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


    public void setCreateDate(String createDate) throws LoulanDNSSystemServiceException
    {
        ZonedDateTime dateTime;
        try
        {
            dateTime = LoulanDNSUtils.toDateTimeObject(createDate);
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to set createDate to DNSServiceEndpointInstancePropertyInfo. value=%s", createDate);
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
            String msg = String.format("Failed to set updateDate to DNSServiceEndpointInstancePropertyInfo. value=%s", updateDate);
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg, cause);
            throw exception;
        }

        this.updateDate = dateTime;
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