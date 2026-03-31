package org.loxsols.net.service.dns.loulandns.server.common.util;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import org.loxsols.net.service.dns.loulandns.client.common.DNSClientCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.LoulanCommonUtilityException;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSProtocolUtils;
import org.loxsols.net.service.dns.loulandns.util.DNSUtils;




/**
 * LoulanDNSの共通ユーティリティクラス.
 * プロジェクト全体で共通する簡易なユーティリティ処理を実装する.
 * LoulanDNS固有の処理については、別途LoulanDNSUtilsクラスに実装する.
 */
public class LoulanDNSCommonUtils
{

    DNSUtils dnsUtils = new DNSUtils();

    /**
     * java.util.Propertiesクラスのキーの一覧を取得する.
     * @param properties
     * @return プロパティのキー値のリスト
     */
    public List<String> getPropertiesKeys(Properties properties) throws LoulanCommonUtilityException
    {
        List<String> keys = new ArrayList<String>();

        if ( properties == null )
        {
            String msg = String.format("Failed to parse keys from Properties. Specified properties object is null.");
            LoulanCommonUtilityException exception = new LoulanCommonUtilityException(msg);
            throw exception;
        }

        for( Object key : properties.keySet() )
        {
            if ( (key instanceof String) == false )
            {
                // プロパティのキー値がString型ではない.
                String msg = String.format("Failed to parse keys from Properties. The key is not String Object. key=%s, class=%s", key, key.getClass().toString() );
                LoulanCommonUtilityException exception = new LoulanCommonUtilityException(msg);
                throw exception;    
            }

            keys.add( (String)key );
        }

        return keys;

    }


    /**
     * InetAddress型をIPアドレスの文字列に変換する.
     * @param inetAddress
     * @return
     * @throws DNSServiceCommonException
     */
    public String toIPAddressString(InetAddress inetAddress) throws DNSServiceCommonException
    {
        // DNSUtilsクラスのメソッドを使用する.
        String addressString = dnsUtils.toIPAddressString(inetAddress);
        return addressString;
    }

    /**
     * 指定したバイトデータをIPアドレスとして解析して文字列(Ex."1.1.1.1")として返す.
     * @param ipv4AddrBytes
     * @return
     * @throws DNSServiceCommonException
     */
    public String toIPAddressString(byte[] ipv4AddrBytes) throws DNSServiceCommonException
    {
        // DNSUtilsクラスのメソッドを使用する.
        String addressString = dnsUtils.toIPAddressString(ipv4AddrBytes);
        return addressString;
    }

    /**
     * IPアドレス形式の文字列をInetAddress型に変換する.
     * 
     * @param ipAddressString
     * @return
     * @throws DNSServiceCommonException
     */
    public InetAddress toInetAddressFromIPAddressString(String ipAddressString) throws DNSServiceCommonException
    {
        InetAddress inetAddress;
        
        try
        {
            inetAddress = InetAddress.getByName(ipAddressString);
        }
        catch(UnknownHostException cause)
        {
            String msg = String.format("Failed to convert to InetAddress from IPAddress String : %s", ipAddressString );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg, cause);
            throw exception;
        }

        return inetAddress;
    }

    /**
     * 指定された文字列が妥当なIPアドレス形式かを判定する.
     * 
     * @param ipAddressString
     * @return
     * @throws DNSServiceCommonException
     */
    public boolean isValidIPAddressString(String ipAddressString)
    {
        InetAddress inetAddress;
        try
        {
            inetAddress = toInetAddressFromIPAddressString(ipAddressString);
        }
        catch(DNSServiceCommonException exception)
        {
            return false;
        }

        if ( inetAddress.getAddress() == null )
        {
            return false;
        }

        return true;
    }


        /**
     * 引数に与えた文字列を(true/false)を解析してboolean型に変換する.
     * JDKランタイムのBoolean.parseBoolean()関数は、解析不能な文字列について例外をスローしないため、独自にラップ実装する.
     * 
     * @param valueString
     * @return
     * @throws DNSClientCommonException
     */
    public boolean parseBoolean(String valueString) throws DNSServiceCommonException
    {
        if ( valueString == null )
        {
            String msg = String.format("Failed to convert String to boolean, due to null. value=%s", valueString);
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        if ( valueString.equals("true") || valueString.equals("false") )
        {
            boolean value = Boolean.parseBoolean(valueString);
            return value;
        }

        String msg = String.format("Failed to convert String to boolean, due to Invalid Value. value=%s", valueString);
        DNSServiceCommonException exception = new DNSServiceCommonException(msg);
        throw exception;
    }
    



}