package org.loxsols.net.service.dns.loulandns.client.subway.common;

import static org.mockito.ArgumentMatchers.booleanThat;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Properties;
import java.util.ArrayList;

import org.apache.commons.net.util.SubnetUtils;
import org.loxsols.net.service.dns.loulandns.client.common.DNSClientCommonException;
import org.loxsols.net.service.dns.loulandns.client.subway.common.exception.InvalidIPSubnetFormatException;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.constants.DNSProtocolConstants;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.IDNSMessageFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSQuestionMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSHeaderSection;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSResourceRecord;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.IDNSQueryPart;



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
     * CIDR表記のIPアドレス空間(Ex."127.0.0.0/24")から、IPアドレスのリスト([127.0.0.1,...127.0.0.255]を生成する.
     * 注意 : "127.0.0.0/8"のような巨大な空間を指定すると、javaのListクラスがエントリ数上限を超えて例外を生じる.
     *        大体、127.0.0.0/16"くらいまでしか使えないと考えるべき.
     * 
     * @param ipSubnetFormat
     * @return
     * @throws DNSSubwayCommonException
     */
    public List<String> getIPAddressListFromIPSubnetFormat(String ipSubnetFormatString) throws DNSSubwayCommonException
    {                
        SubnetUtils.SubnetInfo subnetInfo = getSubnetInfoFromIPSubnetFormat(ipSubnetFormatString);
        String[] allAddresses = subnetInfo.getAllAddresses();

        List<String> addressList = new ArrayList<String>();
        for( int i=0; i < allAddresses.length; i++)
        {
            addressList.add( allAddresses[i]);
        }
       
        return addressList;
    }


    /**
     * サブネット表記のIPアドレス(Ex."127.0.0.0/8")から、IPアドレス空間のサイズを生成する.
     * 例) "127.0.0.0/24" -> 254個.
     * 
     * @param ipSubnetFormatString
     * @return
     * @throws DNSSubwayCommonException
     */
    public long getIPAddressSpaceSizeFromIPSubnetFormat(String ipSubnetFormatString) throws DNSSubwayCommonException
    {
        SubnetUtils.SubnetInfo subnetInfo = getSubnetInfoFromIPSubnetFormat(ipSubnetFormatString);
        long size = subnetInfo.getAddressCountLong();
        return size;
    }


    /**
     * サブネット表記のIPアドレス(Ex."127.0.0.1/8")から、IPアドレス空間の最下位アドレス(Ex."127.0.0.1")を生成する.
     * 
     * @param ipSubnetFormat
     * @return
     * @throws DNSSubwayCommonException
     */
    public String getIPAddressSpaceLowAddressFromIPSubnetFormat(String ipSubnetFormatString) throws DNSSubwayCommonException
    {
        SubnetUtils.SubnetInfo subnetInfo = getSubnetInfoFromIPSubnetFormat(ipSubnetFormatString);
        String lowAddress = subnetInfo.getLowAddress();
        return lowAddress;
    }


    /**
     * サブネット表記のIPアドレス(Ex."127.0.0.1/8")から、IPアドレス空間の最上位アドレス(Ex."127.255.255.254")を生成する.
     * 
     * @param ipSubnetFormat
     * @return
     * @throws DNSSubwayCommonException
     */
    public String getIPAddressSpaceHighAddressFromIPSubnetFormat(String ipSubnetFormatString) throws DNSSubwayCommonException
    {
        SubnetUtils.SubnetInfo subnetInfo = getSubnetInfoFromIPSubnetFormat(ipSubnetFormatString);
        String highAddress = subnetInfo.getHighAddress();
        return highAddress;
    }



    public SubnetUtils.SubnetInfo getSubnetInfoFromIPSubnetFormat(String ipSubnetFormatString) throws DNSSubwayCommonException
    {
        checkIPSubnetFormatString(ipSubnetFormatString);
        SubnetUtils subnetUtils = new SubnetUtils( ipSubnetFormatString );
        SubnetUtils.SubnetInfo subnetInfo = subnetUtils.getInfo();

        return subnetInfo;
    }


    /**
     * 指定された文字列がサブネット表記のIPアドレス(Ex."127.0.0.1/8")かどうかを判定する.
     * 
     */
    public boolean isIPSubnetFormatString(String ipSubnetFormatString) throws DNSSubwayCommonException
    {
        try
        {
            checkIPSubnetFormatString(ipSubnetFormatString);
        }
        catch(InvalidIPSubnetFormatException exception)
        {
            return false;
        }

        return true;
    }


    /**
     * 指定された文字列がサブネット表記のIPアドレス(Ex."127.0.0.1/8")かどうかをチェックする.
     * 異常があれば例外をスローする.
     * 
     * @param ipSubnetFormatString
     * @throws InvalidIPSubnetFormatException
     */
    public void checkIPSubnetFormatString(String ipSubnetFormatString) throws InvalidIPSubnetFormatException
    {

        if ( ipSubnetFormatString == null || ipSubnetFormatString.isEmpty() )
        {
            String msg = String.format("Failed to parse IP-Subnetwork format String. ipSubnetFormatString=%s", ipSubnetFormatString );
            InvalidIPSubnetFormatException exception = new InvalidIPSubnetFormatException(msg, ipSubnetFormatString);
            throw exception;
        }

        if ( ipSubnetFormatString.contains("/") == false )
        {
            String msg = String.format("Failed to parse IP-Subnetwork format String. Not contained /. ipSubnetFormatString=%s", ipSubnetFormatString );
            InvalidIPSubnetFormatException exception = new InvalidIPSubnetFormatException(msg, ipSubnetFormatString);
            throw exception;
        }

        if ( ipSubnetFormatString.split("/").length != 2 )
        {
            String msg = String.format("Failed to parse IP-Subnetwork format String. Invalid subnetwork-format. ipSubnetFormatString=%s", ipSubnetFormatString );
            InvalidIPSubnetFormatException exception = new InvalidIPSubnetFormatException(msg, ipSubnetFormatString);
            throw exception;
        }

        String network  = ipSubnetFormatString.split("/")[0];
        String subnet   = ipSubnetFormatString.split("/")[1];

        if ( network == null || network.isEmpty() )
        {
            String msg = String.format("Failed to parse IP address from subnet-format String. Invalid network body. network=%s, subnet=%s, ipSubnetFormatString=%s", network, subnet, ipSubnetFormatString);
            InvalidIPSubnetFormatException exception = new InvalidIPSubnetFormatException(msg, ipSubnetFormatString);
            throw exception;
        }

        if ( subnet == null || subnet.isEmpty() )
        {
            String msg = String.format("Failed to parse IP address from subnet-format String. Invalid subnet body. network=%s, subnet=%s, ipSubnetFormatString=%s", network, subnet, ipSubnetFormatString);
            InvalidIPSubnetFormatException exception = new InvalidIPSubnetFormatException(msg, ipSubnetFormatString);
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
            InvalidIPSubnetFormatException exception = new InvalidIPSubnetFormatException(msg, cause, ipSubnetFormatString);
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
            InvalidIPSubnetFormatException exception = new InvalidIPSubnetFormatException(msg, cause, ipSubnetFormatString);
            throw exception;
        }

        int sizeOfNetwork = inetAddress.getAddress().length * 8;

        if ( sizeOfNetwork != 32 && sizeOfNetwork != 64 )
        {
            // IPv4でもIPv6でもない.
            String msg = String.format("Failed to parse subnet size from subnet-format String. Invalid size network:%d, network=%s, subnet=%s, subnetFormatIPAddress=%s", sizeOfNetwork, network, subnet, ipSubnetFormatString);
            InvalidIPSubnetFormatException exception = new InvalidIPSubnetFormatException(msg, ipSubnetFormatString);
            throw exception;            
        }

        if ( sizeOfSubnet < 0 || sizeOfSubnet > sizeOfNetwork )
        {
            // サブネットのサイズがネットワークサイズと整合しない.
            String msg = String.format("Failed to parse subnet size from subnet-format String. Invalid size sub-network:%d, network=%s, subnet=%s, subnetFormatIPAddress=%s", sizeOfSubnet, network, subnet, ipSubnetFormatString);
            InvalidIPSubnetFormatException exception = new InvalidIPSubnetFormatException(msg, ipSubnetFormatString);
            throw exception;                   
        }

        return ;
    }


    /**
     * IPv4のIPアドレス形式の文字列(Ex."127.0.0.1")からlong値を取得する.
     * 
     * @param ipString
     * @return
     * @throws DNSSubwayCommonException
     */
    public long getIPv4AddressLongValue(String ipAddressString) throws DNSSubwayCommonException
    {
        InetAddress inetAddress;
        try
        {
            inetAddress = InetAddress.getByName(ipAddressString);
        }
        catch(UnknownHostException cause)
        {
            String msg = String.format("Failed to parse IP addressString. ipAddressString=%s", ipAddressString);
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg, cause);
            throw exception;
        }

        long addressValue = getIPv4AddressLongValue(inetAddress);
        return addressValue;
    }


    /**
     * IPv4のInetAddressクラスからそのIPv4アドレスを表現するlong値を取得する.
     * 
     * @param inetAddress
     * @return
     * @throws DNSSubwayCommonException
     */
    public long getIPv4AddressLongValue(InetAddress inetAddress) throws DNSSubwayCommonException
    {

        byte[] addressBytes = inetAddress.getAddress();

        if ( addressBytes.length > 4 )
        {
            // IPv6アドレスは本メソッドでは変換できない.
            String msg = String.format("Failed to convert InetAddress to IPv4 address value. Specified InetAddress is not IPv4 address. bit-size=%d", addressBytes.length * 8 );
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg);
            throw exception;
        }

        long addressValue = 0;
        
        for( int i=0; i < addressBytes.length; i++)
        {
            addressValue = ( addressValue << 8 )| (0xff & addressBytes[i]);
        }

        return addressValue;
    }

    /**
     * IPアドレスを表すlong値からIPv4のInetAddressクラスを取得する.
     * 
     * @param addressValue
     * @return
     * @throws DNSSubwayCommonException
     */
    public InetAddress getIPv4AddressFromLongValue(long addressValue) throws DNSSubwayCommonException
    {
        byte[] addressBytes = new byte[4];

        addressBytes[0] = (byte)(0xff & ( addressValue >> 24 ));
        addressBytes[1] = (byte)(0xff & ( addressValue >> 16 ));
        addressBytes[2] = (byte)(0xff & ( addressValue >>  8 ));
        addressBytes[3] = (byte)(0xff & ( addressValue >>  0 ));

        InetAddress address;
        try
        {
            address = Inet4Address.getByAddress(addressBytes);
        }
        catch(UnknownHostException cause)
        {
            String msg = String.format("Failed to parse IP address Long value. addressValue=%d, addressBytes={%X, %X, %X, %X}", addressValue, addressBytes[0], addressBytes[1], addressBytes[2], addressBytes[3]);
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg, cause);
            throw exception;
        }

        return address;
    }






    /**
     * 指定したアドレス/ポートの組み合わせが使用できるかを判定する.
     * 
     * @param addr
     * @param port
     * @return
     */
    public boolean isEnableToBindTCPAddress(InetAddress addr, int port) throws DNSSubwayCommonException
    {
        // 一時的にServerSocketをbindさせて使用できるアドレスかを評価する.
        ServerSocket serverSocket;
        try
        {
            serverSocket = new ServerSocket();
       
            // 2026/09/14 追加 : bind前にSO_REUSEADDRを設定し、このTCPポートがTIME_WAIT状態で掴まれるのを防ぐ. 
            serverSocket.setReuseAddress(true);
            
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

        InetAddress addr;
        try
        {
            addr = InetAddress.getByName(ipAddress);
        }
        catch(UnknownHostException cause)
        {
            String msg = String.format("Failed to research Enable to bind TCP address. ipAddress=%s, portList.length=%d", ipAddress, portList.length);
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg, cause);
            throw exception;
        }

        boolean ret = isEnableToBindTCPAddress(ipAddress, portList);
        return ret;
    }


    /**
     * 指定したアドレス/ポート番号(複数件のリスト)の組み合わせが使用できるかを判定する.
     * 
     * @param ipAddress
     * @param port
     * @return
     * @throws DNSSubwayCommonException
     */
    public boolean isEnableToBindTCPAddress(InetAddress ipAddress, int[] portList) throws DNSSubwayCommonException
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


    /**
     * DNSSubwayGWを指すAレコードからなるDNSレスポンスメッセージを生成する.
     * 
     * @param query
     * @param gwAddress
     * @param rTTL
     * @return
     * @throws DNSSubwayCommonException
     */
    public IDNSResponseMessage buildDNSResponseForDNSSubwayGatewayARecord(IDNSMessageFactory dnsMessageFactory, IDNSQuestionMessage questionMessage, String gwAddress, int rTTL) throws DNSSubwayCommonException
    {
        // とりあえず、GWのIPアドレスを含むAレコードのみからなるレスポンスを生成して返す.
        String qname;
        int qtype;
        int qclass;
        try
        {
            IDNSQueryPart query = questionMessage.getDNSQuestionSection().getDNSQueries()[0];
            qname = query.getDNSQueryName();
            qtype = query.getDNSQueryType();
            qclass = query.getDNSQueryClass();
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to create DNS Gateway response, caused by Unable to get query information.");
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg, cause);
            throw exception;
        }

        byte[] rdata;
        try
        {
            rdata = InetAddress.getByName(gwAddress).getAddress();
        }
        catch(UnknownHostException cause)
        {
            String msg = String.format("Failed to create DNS Gateway rdata.");
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg, cause);
            throw exception;
        }

        IDNSResponseMessage response;
        try
        {

            response = dnsMessageFactory.createSimpleResponseDNSMesssage(qname, qtype, qclass, rTTL, rdata);
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to craete DNS Subway Gateway response. qname=%s, qtype=%d, qclass=%d, rTTL=%d, gwAddress=%s", qname, qtype, qclass, rTTL, gwAddress );
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg, cause);
            throw exception;
        }


        // DNSクエリIDをレスポンスメッセージに設定する.
        try
        {
            int dnsQueryID = questionMessage.getDNSHeaderSection().getID();
            response.getDNSHeaderSection().setID( dnsQueryID );
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to craete DNS Subway Gateway response, caued by setting to DNS Query ID.");
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg, cause);
            throw exception;
        }


        // ------
        // 2026/08/21 : ヘッダーセクションの各値がレスポンスに適していない不具合対応.
        try
        {
            IDNSHeaderSection headerSection = response.getDNSHeaderSection();

            // QRビットをオンにして「レスポンス」にする
            headerSection.setQR(1);

            // RAビット(再帰応答可否)を問い合わせ時のRDビット(再帰応答希望)フラグに応じて調整
            if ( questionMessage.getDNSHeaderSection().getRD() == 1)
            {
                // 再帰応答希望のクエリ(RD=1)には再帰応答可能(RA=1)を返す
                headerSection.setRD(1);
                headerSection.setRA(1);
            }
            else
            {
                // 再帰応答を希望しないクエリ(RD=0)には再帰応答不可(RA=0)を返す
                headerSection.setRD(0);
                headerSection.setRA(0);
            }

            // RCODEを 0 (NOERROR) に設定
            headerSection.setRCode(0);

            // 再度DNSレスポンスメッセージのヘッダセクションをsetする.
            response.setDNSHeaderSection(headerSection);

        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to craete DNS Subway Gateway response, caued by setting to DNS Response header values.");
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg, cause);
            throw exception;
        }  
        // ------

        return response;
    }


    /**
     * NODATA応答のDNSレスポンスを生成する.
     * DNSSubwayのGWが未対応のQType(Ex.TYPE_AAAA等)で問い合わせをした場合のDNSレスポンスとして使用する.
     * RCODE = 0 (NOERROR) かつ ANCOUNT = 0（いわゆる NODATA 応答）を返す.
     * 
     * @param dnsMessageFactory
     * @param questionMessage
     * @return
     * @throws DNSSubwayCommonException
     */
    public IDNSResponseMessage buildDNSResponseForNODATA(IDNSMessageFactory dnsMessageFactory, IDNSQuestionMessage questionMessage) throws DNSSubwayCommonException
    {
        String qname = null;
        int qtype = 0;
        int qclass = 0;
        IDNSResponseMessage response;
        try
        {
            IDNSQueryPart query = questionMessage.getDNSQuestionSection().getDNSQueries()[0];

            qname = query.getDNSQueryName();
            qtype = query.getDNSQueryType();
            qclass = query.getDNSQueryClass();

            response = dnsMessageFactory.createNODATAResponseDNSMesssage(query.getDNSQueryName(), query.getDNSQueryType(), query.getDNSQueryClass() );
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to craete DNS NODATA Response. qname=%s, qtype=%d, qclass=%d", qname, qtype, qclass  );
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg, cause);
            throw exception;
        }

        // DNSクエリIDをレスポンスメッセージに設定する.
        try
        {
            int dnsQueryID = questionMessage.getDNSHeaderSection().getID();
            response.getDNSHeaderSection().setID( dnsQueryID );
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to craete DNS Subway Gateway response, caued by setting to DNS Query ID.");
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg, cause);
            throw exception;
        }

        return response;
    }


    /**
     * DNS問い合わせメッセージの種類がAレコード問い合わせ(ANYタイプを含む)の場合はtrueを返す.
     * 
     * @param questionMessage
     * @return
     * @throws DNSSubwayCommonException
     */
    public boolean isDNSQueryTypeARecord(IDNSQuestionMessage questionMessage) throws DNSSubwayCommonException
    {
        try
        {
            if ( questionMessage.getDNSQuestionSection().getDNSQueries().length < 1 )
            {
                // DNSクエリが含まれていないメッセージが指定された.
                String msg = String.format("Failed to parse DNSQuestionMessage QType. Query size is 0. DNS header id=%d", questionMessage.getDNSHeaderSection().getID() );
                DNSSubwayCommonException exception = new DNSSubwayCommonException(msg);
                throw exception;
            }

            IDNSQueryPart query = questionMessage.getDNSQuestionSection().getDNSQueries()[0];

            int qTtype = query.getDNSQueryType();
            if ( qTtype == DNSProtocolConstants.DNS_RR_TYPE_A )
            {
                // TYPE_A問い合わせなのでtrueを返す.
                return true;
            }

            if ( qTtype == DNSProtocolConstants.DNS_RR_TYPE_ANY )
            {
                // TYPE_ANYでもAレコード問い合わせとみなしてtrueで復帰する.
                return true;
            }
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to parse DNSQuestionMessage QType." );
            DNSSubwayCommonException exception = new DNSSubwayCommonException(msg);
            throw exception;
        }

        return false;
    }


}