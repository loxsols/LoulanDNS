package org.loxsols.net.service.dns.loulandns.server.common.data;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;

/**
 * プロパティ形式オプションのデータクラス.
 * 
 */
public class LoulanDNSOptionPropertiesData
{

    protected String key;
    protected String value;
    protected String defalultValue;


    protected void init(String key, String value, String defalultValue) throws DNSServiceCommonException
    {
        if ( key == null )
        {
            String msg = String.format("Failed to set LoulanDNSOptionPropertiesData, key is null. key=%s, value=%s, defalultValue=%s");
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        if ( value == null && defalultValue == null)
        {
            // 値もデフォルト値もnullのエントリは設定できない.
            String msg = String.format("Failed to set LoulanDNSOptionPropertiesData, value and defaultValue are null. key=%s, value=%s, defalultValue=%s");
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }


        this.key = key;
        this.value = value;
        this.defalultValue = defalultValue;
    }
    
    public LoulanDNSOptionPropertiesData(String key, String value, String defalultValue) throws DNSServiceCommonException
    {
        init(key, value, defalultValue);
    }

    public LoulanDNSOptionPropertiesData(String key, String value) throws DNSServiceCommonException
    {
        this(key, value, null);
    }


    public String getKey() throws DNSServiceCommonException
    {
        return key;
    }

    public String getValue() throws DNSServiceCommonException
    {
        return value;
    }

    public String getDefaultValue() throws DNSServiceCommonException
    {
        return defalultValue;
    }

    public boolean hasDefaultValue() throws DNSServiceCommonException
    {
        if ( getDefaultValue() == null )
        {
            return false;
        }

        return true;
    }


    public void update(String key, String value, String defaultValue) throws DNSServiceCommonException
    {
        if ( defaultValue == null )
        {
            defaultValue = getDefaultValue();
        }

        init(key, value, defaultValue);
    }




    
}