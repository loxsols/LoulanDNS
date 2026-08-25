package org.loxsols.net.service.dns.loulandns.client.subway.common;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Properties;
import java.util.ArrayList;

import org.apache.commons.net.util.SubnetUtils;



public class DNSSubwayUtils
{

    /**
     * DNSSubwayのゲートウェイ情報を一意に識別するためのキー値を生成する.
     * 
     */
    public String buildGatewayDescriptorKey(String subwayLineType, String dname, String protocolType, int[] portList, Properties options) throws DNSSubwayCommonException
    {
        String key;

        String optionsString = "";
        for( Object optKey : options.keySet() )
        {
            String optVal = options.getProperty( optKey.toString() );
            optionsString += String.format("%s=%s;", optKey, optVal);
        }

        String portListString = getPortListString(portList);

        key = String.format("%s/%s/%s/%s/%s", subwayLineType, dname, protocolType, portListString, optionsString);
        return key;
    }



    /**
     * サブネット表記のIPアドレス(Ex."127.0.0.1/8")から、IPアドレスのリスト([127.0.0.1,...127.0.0.255]を生成する.
     * 
     * @param ipSubnetFormat
     * @return
     * @throws DNSSubwayCommonException
     */
    public List<String> getIPAddressListFromIPSubnetFormat(String ipSubnetFormatString) throws DNSSubwayCommonException
    {
        if ( ipSubnetFormatString == null || ipSubnetFormatString.isEmpty() )
        {
            String msg = String.format("Failed to parse IP-Subnetwork format String. ipSubnetFormatString=%s", ipSubnetFormatString );
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg);
            throw exception;
        }

        if ( ipSubnetFormatString.contains("/") == false )
        {
            String msg = String.format("Failed to parse IP-Subnetwork format String. Not contained /. ipSubnetFormatString=%s", ipSubnetFormatString );
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg);
            throw exception;
        }

        if ( ipSubnetFormatString.split("/").length != 2 )
        {
            String msg = String.format("Failed to parse IP-Subnetwork format String. Invalid subnetwork-format. ipSubnetFormatString=%s", ipSubnetFormatString );
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg);
            throw exception;
        }

        String network  = ipSubnetFormatString.split("/")[0];
        String subnet   = ipSubnetFormatString.split("/")[1];

        if ( network == null || network.isEmpty() )
        {
            String msg = String.format("Failed to parse IP address from subnet-format String. Invalid network body. network=%s, subnet=%s, ipSubnetFormatString=%s", network, subnet, ipSubnetFormatString);
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg);
            throw exception;
        }

        if ( subnet == null || subnet.isEmpty() )
        {
            String msg = String.format("Failed to parse IP address from subnet-format String. Invalid subnet body. network=%s, subnet=%s, ipSubnetFormatString=%s", network, subnet, ipSubnetFormatString);
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg);
            throw exception;
        }

        InetAddress inetAddress;
        try
        {
            inetAddress = InetAddress.getByName(network);
        }
        catch(UnknownHostException cause)
        {
            String msg = String.format("Failed to parse IP address from subnet-format String. network=%s, subnet=%s, ipSubnetFormatString=%s", network, subnet, ipSubnetFormatString);
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg, cause);
            throw exception;
        }

        int sizeOfSubnet;
        try
        {
            sizeOfSubnet = Integer.parseInt(subnet);
        }
        catch(NumberFormatException cause)
        {
            String msg = String.format("Failed to parse subnet size from subnet-format String. network=%s, subnet=%s, subnetFormatIPAddress=%s", network, subnet, ipSubnetFormatString);
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg, cause);
            throw exception;
        }

        int sizeOfNetwork = inetAddress.getAddress().length * 8;

        if ( sizeOfNetwork != 32 && sizeOfNetwork != 64 )
        {
            // IPv4でもIPv6でもない.
            String msg = String.format("Failed to parse subnet size from subnet-format String. Invalid size network:%d, network=%s, subnet=%s, subnetFormatIPAddress=%s", sizeOfNetwork, network, subnet, ipSubnetFormatString);
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg);
            throw exception;            
        }

        if ( sizeOfSubnet < 0 || sizeOfSubnet > sizeOfNetwork )
        {
            // サブネットのサイズがネットワークサイズと整合しない.
            String msg = String.format("Failed to parse subnet size from subnet-format String. Invalid size sub-network:%d, network=%s, subnet=%s, subnetFormatIPAddress=%s", sizeOfSubnet, network, subnet, ipSubnetFormatString);
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg);
            throw exception;                   
        }

        SubnetUtils subnetUtils = new SubnetUtils( ipSubnetFormatString );
        SubnetUtils.SubnetInfo subnetInfo = subnetUtils.getInfo();

        String[] allAddresses = subnetInfo.getAllAddresses();

        List<String> addressList = new ArrayList<String>();
        for( int i=0; i < allAddresses.length; i++)
        {
            addressList.add( allAddresses[i]);
        }
       
        return addressList;
    }

    /**
     * 指定された文字列がサブネット表記のIPアドレス(Ex."127.0.0.1/8")かどうかを判定する.
     * 
     */
    public boolean isIPSubnetFormatString(String ipSubnetFormatString) throws DNSSubwayCommonException
    {
        try
        {
            getIPAddressListFromIPSubnetFormat(ipSubnetFormatString);
            return true;
        }
        catch(DNSSubwayCommonException exception)
        {
            // For DEBUG.
            // exception.printStackTrace();

            return false;
        }

    }


    /**
     * 指定したアドレス/ポートの組み合わせが使用できるかを判定する.
     * 
     * @param ipAddress
     * @param port
     * @return
     */
    public boolean isEnableToBindTCPAddress(String ipAddress, int port) throws DNSSubwayCommonException
    {
        // 一時的にServerSocketをbindさせて使用できるアドレスかを評価する.
        ServerSocket serverSocket;
        try
        {
            serverSocket = new ServerSocket();
            InetAddress addr = InetAddress.getByName(ipAddress);
            serverSocket.bind(new InetSocketAddress(addr, port));
            serverSocket.close();
        }
        catch (IOException exception)
        {
            return false;
        }

        return true;
    }

    /**
     * 指定したアドレス/ポート番号(複数件のリスト)の組み合わせが使用できるかを判定する.
     * 
     * @param ipAddress
     * @param port
     * @return
     * @throws DNSSubwayCommonException
     */
    public boolean isEnableToBindTCPAddress(String ipAddress, int[] portList) throws DNSSubwayCommonException
    {
        for( int port : portList)
        {
            boolean flg = isEnableToBindTCPAddress(ipAddress, port);
            if ( flg == false )
            {
                return false;
            }
        }

        return true;
    }



    /**
     * 指定したドメインが登録済みのDNS-Subway対象ドメインのリストに含まれるかを確認する.
     * なお、リスト内の事前定義ドメインには、ワイルドカード指定(Ex."*.hoge.com")のような設定を許容する.
     * 
     * @param regsistDomainList
     * @param dname
     * @return
     * @throws DNSSubwayCommonException
     */
    public boolean isDNSSubwayTargetDomain(List<String> regsistDomainList, String dname) throws DNSSubwayCommonException
    {
        for( String registered : regsistDomainList )
        {
            if ( compareDNSSubwayTargetDomain(registered, dname) )
            {
                return true;
            }
        }

        return false;
    }


    /**
     * 指定したドメイン(dname)が、事前定義したDNS-Subway対象ドメイン(registered)に一致するかを判定する.
     * なお、DNS-Subway対象ドメイン(registered)は一部ワイルドカード指定(Ex."*.hoge.com")が可能.
     * 
     * @param registered
     * @param dname
     * @throws DNSSubwayCommonException
     */
    protected boolean compareDNSSubwayTargetDomain(String registered, String dname) throws DNSSubwayCommonException
    {
        if ( registered.endsWith(".") )
        {
            // 指定されたドメイン名がFQDN形式で末尾がドット記号(.)で終端する場合はそれを取り除く.
            registered = registered.substring(0, registered.length() - 1 );
        }

        if ( dname.endsWith(".") )
        {
            // 指定されたドメイン名がFQDN形式で末尾がドット記号(.)で終端する場合はそれを取り除く.
            dname = dname.substring(0, dname.length() - 1 );
        }

        if ( registered.equals(dname) )
        {
            // 完全一致する.
            return true;
        }

        if ( registered.equals("*") )
        {
            // DNS-Subway対象ドメイン(registered)は完全ワイルドカード("*")なので全ドメインに一致する.
            return true;
        }

        if ( registered.startsWith("*") )
        {
            // DNS-Subway対象ドメイン(registered)は一部ワイルドカード指定なので、後半部分が一致するかを判定する.
            // 例) DNS-Subway対象ドメイン(registered)が"*.hoge.com"の場合、"fuga.hoge.com"や"fuga1.fuga2.hoge.com"を一致ドメインとする.

            String subRegistered = registered.substring(1);
            if ( dname.endsWith(subRegistered) )
            {
                return true;
            }
        }

        return false;
    }


    // 指定したポート番号の配列から、","区切りテキストのポート番号のリストの文字列を生成する.
    // 例) [80, 443] => "80,443"
    public String getPortListString(int[] portList) throws DNSSubwayCommonException
    {
        String portListString = "";
        for( int port : portList )
        {
            if ( portListString.isEmpty() == false )
            {
                portListString += ",";
            }

            portListString += Integer.toString(port);
        }

        return portListString;
    }

    // 指定したポート番号のリストから、","区切りテキストのポート番号のリストの文字列を生成する.
    // 例) {80, 443} => "80,443"
    public String getPortListString(List<Integer> portList) throws DNSSubwayCommonException
    {
        int[] portArray = getPortArrayFromPortList(portList);
        String ret = getPortListString(portArray);
        return ret;
    }

    // 指定した","区切りテキストのポート番号のリストの文字列から、ポート番号の配列を生成する.
    public int[] getPortListFromString(String portListString) throws DNSSubwayCommonException
    {
        String[] array = portListString.split(",");
        int[] portList = new int[ array.length ];
        for( int i=0; i < array.length; i++ )
        {
            portList[i] = Integer.parseInt( array[i] );
        }

        return portList;
    }


    public int[] getPortArrayFromPortList(List<Integer> portList) throws DNSSubwayCommonException
    {
        int[] portArray = new int[ portList.size() ];
        for( int i=0; i < portArray.length; i++)
        {
            portArray[i] = portList.get(i);
        }

        return portArray;
    }

}