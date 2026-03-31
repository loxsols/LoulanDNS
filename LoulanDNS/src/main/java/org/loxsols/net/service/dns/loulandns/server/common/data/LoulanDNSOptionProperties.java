package org.loxsols.net.service.dns.loulandns.server.common.data;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;

import java.util.*;


/**
 * プロパティ形式オプションの表現クラス.
 * 
 */
public class LoulanDNSOptionProperties
{

    List<LoulanDNSOptionPropertiesData> propertyList = new ArrayList<LoulanDNSOptionPropertiesData>();

    public LoulanDNSOptionProperties() throws DNSServiceCommonException
    {
        
    }


    public void put(String key, String value, String defaultValue) throws DNSServiceCommonException
    {
        if( contains(key) == true )
        {
            // 既に指定されたキーのレコードは存在するので内容を更新する.
            LoulanDNSOptionPropertiesData data = get(key);
            data.update(key, value, defaultValue);
            return ;
        }
        else
        {
            LoulanDNSOptionPropertiesData data = new LoulanDNSOptionPropertiesData(key, value, defaultValue);
            this.propertyList.add( data );
        }

    }

    public void put(String key, String value) throws DNSServiceCommonException
    {
        put(key, value, null);
    }


    public void put(Properties properties) throws DNSServiceCommonException
    {
        for( Object keyObj : properties.keySet() )
        {
            String key = (String)keyObj;
            String value = properties.getProperty(key);

            put(key, value);
        }

    }


    /**
     * 指定したキー値とデフォルト値の組み合わせを設定する.
     * 
     * @param key
     * @param defaultValue
     * @throws DNSServiceCommonException
     */
    public void putDefaultValue(String key, String defaultValue) throws DNSServiceCommonException
    {
        put(key, null, defaultValue);
    }



    public String[] getKeys() throws DNSServiceCommonException
    {
        String[] keys = new String[ propertyList.size() ];
        for( int i =0; i <  propertyList.size(); i++ )
        {
            LoulanDNSOptionPropertiesData data  = propertyList.get(i);
            keys[i] = data.getKey();
        }

        return keys;
    }

    public LoulanDNSOptionPropertiesData get(String key) throws DNSServiceCommonException
    {
        LoulanDNSOptionPropertiesData data = null;

        for( LoulanDNSOptionPropertiesData tmp : propertyList )
        {
            if ( tmp.getKey().equals(key) )
            {
                data = tmp;
                break;
            }
        }

        return data;
    }

    public boolean contains(String key) throws DNSServiceCommonException
    {
        if ( get(key) == null )
        {
            return false;
        }
        return true;
    }

    /**
     * 本クラス内のデータをPropertiesクラスに変換する.
     * なお、デフォルト値が指定されていて、かつ値がnullの場合は、デフォルト値を値に設定したエントリを設定する.
     * 
     * @return
     * @throws DNSServiceCommonException
     */
    public Properties getProperties() throws DNSServiceCommonException
    {
        Properties properties = new Properties();

        for( String key : getKeys() )
        {
            LoulanDNSOptionPropertiesData data = get(key);
            
            String value = data.getValue();
            if ( data.getValue() == null )
            {
                value = data.getDefaultValue();
            }

            properties.setProperty(key, value);
        }
        
        return properties;
    }






    
}