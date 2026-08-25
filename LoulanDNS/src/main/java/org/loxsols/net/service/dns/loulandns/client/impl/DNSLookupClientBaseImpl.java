package org.loxsols.net.service.dns.loulandns.client.impl;


import static org.mockito.ArgumentMatchers.booleanThat;
import static org.mockito.Mockito.calls;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.loxsols.net.service.dns.loulandns.client.*;
import org.loxsols.net.service.dns.loulandns.client.common.DNSClientCommonException;
import org.loxsols.net.service.dns.loulandns.client.common.LoulanDNSClientConstants;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.IDNSAdditionalSectionFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.rr.IDNSResourceRecordFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.rr.edns.DNSRROptPseudoRRDataForECSImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSQuestionMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSAdditionalSection;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSResourceRecord;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.IDNSRROptPseudoRRData;
import org.loxsols.net.service.dns.loulandns.util.DNSUtils;
import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSDebugUtils;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSProtocolUtils;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSCommonUtils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;


/**
 * IDNSLookupClientインターフェースの基底実装クラス.
 * 
 */
public abstract class DNSLookupClientBaseImpl implements IDNSLookupClient
{

    InetAddress dnsServerAddress;
    Integer dnsServerPort;


    IDNSMessageFactory dnsMessageFactory;
    IDNSMessageTransporter messageTransporter;

    IDNSResourceRecordFactory dnsResourceRecordFactory;
    IDNSAdditionalSectionFactory dnsAdditionalSectionFactory;


    boolean isEnableEDNS = true;

    boolean isEnableEDNSClientSubnet = false;
    InetAddress ednsClientSubnetAddress = null;
    int ednsClinetSubnetPrefix = 0;


    protected LoulanDNSProtocolUtils protocolUtils = new LoulanDNSProtocolUtils();
    protected LoulanDNSCommonUtils commonUtils = new LoulanDNSCommonUtils();


    Properties properties;


    public DNSLookupClientBaseImpl() throws DNSServiceCommonException
    {
        super();
    }


    public void setProperties(Properties properteis) throws DNSServiceCommonException
    {
        this.properties = properteis;   
    }

    public Properties getProperties() throws DNSServiceCommonException
    {
        return this.properties;
    }




    // 指定した条件でDNSの名前解決を行う.
    public IDNSResponseMessage resolve(String qname, int qtype, int qclass) throws DNSClientCommonException
    {

        LoulanDNSDebugUtils.printDebug(getClass(), "resolve()", "qname=" + qname);
        LoulanDNSDebugUtils.printDebug(getClass(), "resolve()", "qtype=" + qtype);
        LoulanDNSDebugUtils.printDebug(getClass(), "resolve()", "qclass=" + qclass);

        IDNSQuestionMessage questionMessage;
        try
        {
            questionMessage = dnsMessageFactory.createQuestionDNSMesssage(qname, qtype, qclass);
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to create Question DNS Message. qname=%s, qtype=%d, qclass=%d", qname, qtype, qclass);
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }

        IDNSResponseMessage responseMessage = resolve(questionMessage);

        return responseMessage;
    }   

    public IDNSResponseMessage resolve(IDNSQuestionMessage questionMessage) throws DNSClientCommonException
    {
        // 本クラスに指定されているEDNSオプションを問い合わせメッセージに追加する.
        questionMessage = addEDNSOtpionsToDNSQuestionMessage(questionMessage);

        IDNSResponseMessage responseMessage = messageTransporter.lookup(questionMessage);
        return responseMessage;
    }



    // 本クラスの初期化処理.
    public void init(Properties properties) throws DNSClientCommonException
    {

        // 一応、与えられたパラメータはすべて本クラス内に保持しておく.
        try
        {
            setProperties(properties);
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to set DNSResolver properties.");
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }


        // 外部問い合わせ用のDNSサーバーのIPアドレス.
        String serverAddressValue = (String)properties.get( LoulanDNSClientConstants.PROP_KEY_DNS_SERVER_ADDRESS );
        if ( serverAddressValue != null )
        {
            setDNSServerAddress(serverAddressValue);
        }

        // 外部問い合わせ用のDNSサーバーのポート番号
        String serverPortValue = (String)properties.get( LoulanDNSClientConstants.PROP_KEY_DNS_SERVER_PORT );
        if ( serverPortValue != null )
        {
            setDNSServerPort(serverPortValue);
        }

        // EDNS(0)を使用するか(true/false)
        String enableEDNSValue = (String)properties.get(LoulanDNSClientConstants.PROP_KEY_DNS_EDNS_ENABLE);
        if ( enableEDNSValue != null )
        {
            setEnableEDNS(enableEDNSValue);
        }

        // EDNSオプション   :   EDNS Client Subnet (ECS) を使用するか(true/false)
        String enableECSValue = (String)properties.get(LoulanDNSClientConstants.PROP_KEY_DNS_EDNS_ECS_ENABLE);
        if ( enableECSValue != null )
        {
            setEnableEDNSClientSubnet(enableECSValue);
        }

        // EDNS Client Subnet(ECS)のアドレス情報を設定する.
        String ecsAddressValue = (String)properties.get(LoulanDNSClientConstants.PROP_KEY_DNS_EDNS_ECS_ADDRESS);
        if ( ecsAddressValue != null )
        {
            setEDNSClientSubnetAddress(ecsAddressValue);
        }

        // EDNS Client Subnet(ECS)のアドレス情報のプリフィックス部を設定する.
        String ecsAddressPrefixValue = (String)properties.get(LoulanDNSClientConstants.PROP_KEY_DNS_EDNS_ECS_PREFIX_BITS);
        if ( ecsAddressPrefixValue != null )
        {
            setEDNSClientSubnetAddressPrefix(ecsAddressPrefixValue);
        }


        // TODO : その他のEDNS拡張などのオプション指定はまだ実装していない.



        // DNSメッセージトランスポーターにプロパティ値を設定する.
        HashMap<String,String> messageTransporterProperties =new HashMap<String,String>();
        messageTransporterProperties.put(LoulanDNSClientConstants.PROP_KEY_DNS_SERVER_ADDRESS, this.getDNSServerAddress().getHostAddress() );
        messageTransporterProperties.put(LoulanDNSClientConstants.PROP_KEY_DNS_SERVER_PORT, Integer.toString( this.getDNSServerPort() ) );

        // DoHサーバーのURLを設定する.
        String dohServerFullURL = properties.getProperty( LoulanDNSClientConstants.PROP_KEY_DOH_SERVER_FULL_URL);
        if ( dohServerFullURL != null && dohServerFullURL.isEmpty() == false )
        {
            messageTransporterProperties.put(  LoulanDNSClientConstants.PROP_KEY_DOH_SERVER_FULL_URL, dohServerFullURL );
        }


        // DoHサーバーにアクセスするときに使用するHTTPメソッドタイプを設定する.
        String dohServerMethodType = properties.getProperty( LoulanDNSClientConstants.PROP_KEY_DOH_SERVER_HTTP_METHOD_TYPE );
        if ( dohServerMethodType != null && dohServerMethodType.isEmpty() == false )
        {
            messageTransporterProperties.put(  LoulanDNSClientConstants.PROP_KEY_DOH_SERVER_HTTP_METHOD_TYPE, dohServerMethodType );
        }

        // DoHサーバーにアクセスするときに使用するHTTPのCONTENTタイプ (例 : application/dns-message )を設定する.
        String dohServerContentType = properties.getProperty( LoulanDNSClientConstants.PROP_KEY_DOH_SERVER_HTTP_CONTENT_TYPE );
        if ( dohServerContentType != null && dohServerContentType.isEmpty() == false )
        {
            messageTransporterProperties.put(  LoulanDNSClientConstants.PROP_KEY_DOH_SERVER_HTTP_CONTENT_TYPE, dohServerContentType );
        }

        // DoHサーバーにアクセスするときに使用するHTTPのCONTENTタイプ (例 : application/dns-message )を設定する.
        String dohServerAcceptType = properties.getProperty( LoulanDNSClientConstants.PROP_KEY_DOH_SERVER_HTTP_ACCEPT_TYPE );
        if ( dohServerAcceptType != null && dohServerAcceptType.isEmpty() == false )
        {
            messageTransporterProperties.put(  LoulanDNSClientConstants.PROP_KEY_DOH_SERVER_HTTP_ACCEPT_TYPE, dohServerAcceptType );
        }


        messageTransporter.init(messageTransporterProperties);


    }



    public void setDNSMessageFacotry(IDNSMessageFactory instance)
    {
        this.dnsMessageFactory = instance;
    }

    public IDNSMessageFactory getDNSMessageFactory()
    {
        return this.dnsMessageFactory;
    }

    public void setDNSMessageTransporter(IDNSMessageTransporter instance)
    {
        this.messageTransporter = instance;
    }

    public IDNSMessageTransporter getDNSMessageTransporter()
    {
        return this.messageTransporter;
    }


    public void setDNSAdditionalSectionFactory(IDNSAdditionalSectionFactory instance)
    {
        this.dnsAdditionalSectionFactory = instance;
    }

    public IDNSAdditionalSectionFactory getDNSAdditionalSectionFactory()
    {
        return this.dnsAdditionalSectionFactory;
    }

    public void setDNSResourceRecordFactory(IDNSResourceRecordFactory instance)
    {
        this.dnsResourceRecordFactory = instance;
    }

    public IDNSResourceRecordFactory getDNSResourceRecordFactory()
    {
        return this.dnsResourceRecordFactory;
    }


    public InetAddress getDNSServerAddress()
    {
        return this.dnsServerAddress;
    }

    public String getDNSServerAddressString()
    {
        InetAddress address = getDNSServerAddress();
        String value = address.getHostAddress();
        return value;
    }

    public void setDNSServerAddress(InetAddress address) throws DNSClientCommonException
    {
        this.dnsServerAddress = address;

        String value = getDNSServerAddressString();

        if ( getDNSMessageTransporter() != null )
        {
            try
            {
                setMessageTransporterParameter(LoulanDNSClientConstants.PROP_KEY_DNS_SERVER_ADDRESS, value );
            }
            catch(DNSServiceCommonException cause)
            {
                String msg = String.format("Failed to set DNS Server Address. address=%s, value=%s",  address, value);
                DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
                throw exception;
            }
        }  

    }

    public void setDNSServerAddress(String addressString) throws DNSClientCommonException
    {
        InetAddress address;
        
        try
        {
            address = InetAddress.getByName(addressString);
        }
        catch(UnknownHostException cause)
        {
            String msg = String.format("Failed to set DNSServerAddress. value=%s", addressString);
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }

        setDNSServerAddress(address);
    }


    public Integer getDNSServerPort() throws DNSClientCommonException
    {
        return this.dnsServerPort;
    }

    public String getDNSServerPortString() throws DNSClientCommonException
    {
        Integer port = getDNSServerPort();
        if ( port == null )
        {
            return null;
        }

        String value = port.toString();
        return value;
    }

    public void setDNSServerPort(int port) throws DNSClientCommonException
    {
        if ( port < 0 || port > 65535 )
        {
            String msg = String.format("Failed to set DNSServerPort, due to Invalid port range. value=%d", port);
            DNSClientCommonException exception = new DNSClientCommonException(msg);
            throw exception;
        }

        this.dnsServerPort = port;

        if ( getDNSMessageTransporter() != null )
        {
            String value = getDNSServerPortString();
            try
            {
                setMessageTransporterParameter(LoulanDNSClientConstants.PROP_KEY_DNS_SERVER_PORT, value);
            }
            catch(DNSServiceCommonException cause)
            {
                String msg = String.format("Failed to set DNS Server port. port=%d, value=%s",  port, value);
                DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
                throw exception;
            }
        }


    }

    public void setDNSServerPort(String portString) throws DNSClientCommonException
    {
        int port = parseInt(portString);
        setDNSServerPort(port);
    }


    // EDNS(0)を使用するかを設定する.
    public void setEnableEDNS(boolean value) throws DNSClientCommonException
    {
        this.isEnableEDNS = value;
    }

    // EDNS(0)を使用するかを設定する.
    public void setEnableEDNS(String valueString) throws DNSClientCommonException
    {
        boolean value = parseBoolean(valueString);
        setEnableEDNS(value);
    }


    public boolean getEnableEDNS() throws DNSClientCommonException
    {
        return this.isEnableEDNS;
    }

    // EDNS Client Subnet(ECS)を使用するかを設定する.
    public void setEnableEDNSClientSubnet(boolean value) throws DNSClientCommonException
    {
        this.isEnableEDNSClientSubnet = value;

        if ( value == true )
        {
            // ECS仕様フラグがONの場合は、EDNS(0)の仕様フラグも同時にONにする.
            setEnableEDNS(true);
        }
    }

    // EDNS Client Subnet(ECS)を使用するかを設定する.
    public void setEnableEDNSClientSubnet(String valueString) throws DNSClientCommonException
    {
        boolean value = parseBoolean(valueString);
        setEnableEDNSClientSubnet(value);
    }


    public boolean getEnableEDNSClientSubnet() throws DNSClientCommonException
    {
        return this.isEnableEDNSClientSubnet;
    }

    // EDNS Client Subnet(ECS)のアドレス情報を設定する.
    public void setEDNSClientSubnetAddress(String address, int prefix) throws DNSClientCommonException
    {
        // ECSアドレスとプリフィックスをそれぞれ設定する.
        setEDNSClientSubnetAddress(address);
        setEDNSClientSubnetAddressPrefix(prefix);

        InetAddress ecsInetAddress = getEDNSClientSubnetInetAddress();

        try
        {
            if ( protocolUtils.isValidPairOfEDNSClientSubnetAddressAndPrefix(ecsInetAddress, prefix) == false )
            {
                String msg = String.format("ECS address and prefix is invalid pair value. address=%s, prefix=%d", address, prefix);
                DNSClientCommonException exception = new DNSClientCommonException(msg);
                throw exception;
            }
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to check ECS addreess and prefix value pair.");
            DNSClientCommonException exception = new DNSClientCommonException(msg);
            throw exception;
        }


        // ECS仕様フラグも同時にONにする.
        setEnableEDNSClientSubnet(true);
    }


    // EDNS Client Subnet(ECS)のアドレス情報のアドレス部を設定する.
    public void setEDNSClientSubnetAddress(String address) throws DNSClientCommonException
    {
        InetAddress inetAddress;
        try
        {
            inetAddress = InetAddress.getByName(address);
        }
        catch(UnknownHostException cause)
        {
            String msg = String.format("Failed to parse EDNS Client Subnet(ECS) address from String. address=%s", address);
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }

        setEDNSClientSubnetInetAddress(inetAddress);
    }

    // EDNS Client Subnet(ECS)のアドレス情報のアドレス部をInetAddress形式で設定する.
    public void setEDNSClientSubnetInetAddress(InetAddress address) throws DNSClientCommonException
    {
        this.ednsClientSubnetAddress = address;
    }


    // EDNS Client Subnet(ECS)のアドレス情報のアドレス部をInetAddress形式で取得する.
    public InetAddress getEDNSClientSubnetInetAddress() throws DNSClientCommonException
    {
        InetAddress address = this.ednsClientSubnetAddress;
        return address;
    }

    // EDNS Client Subnet(ECS)のアドレス情報のアドレス部を取得する.
    public String getEDNSClientSubnetAddress() throws DNSClientCommonException
    {
        InetAddress ecsInetAddress = getEDNSClientSubnetInetAddress();

        String addressString;
        
        try
        {
            addressString = protocolUtils.inetAddressToIPAddressString(ecsInetAddress);
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to convert InetAddress to String. InetAddress=%s", ecsInetAddress);
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }

        return addressString;
    }


    // EDNS Client Subnet(ECS)のアドレス情報のプリフィックス部を設定する.
    public void setEDNSClientSubnetAddressPrefix(int prefix) throws DNSClientCommonException
    {
        if ( prefix < 0 )
        {
            // ECSプリフィックスが負数はあり得ない.
            String msg = String.format("Invalid EDNS Client Subnet(ECS) prefix value. Specified prefix value is negative. value=%d", prefix);
            DNSClientCommonException exception = new DNSClientCommonException(msg);
            throw exception;
        }

        if ( prefix > 128 )
        {
            // ECSプリフィックスがIPv6のサイズ(128bits)より大きいことはあり得ない.
            String msg = String.format("Invalid EDNS Client Subnet(ECS) prefix value. Specified prefix value is over IPv6 bitsize(128). value=%d", prefix);
            DNSClientCommonException exception = new DNSClientCommonException(msg);
            throw exception;
        }

        this.ednsClinetSubnetPrefix = prefix;
    }


    // EDNS Client Subnet(ECS)のアドレス情報のプリフィックス部を設定する.
    public void setEDNSClientSubnetAddressPrefix(String prefixValue) throws DNSClientCommonException
    {
        int prefix = parseInt(prefixValue);
        setEDNSClientSubnetAddressPrefix(prefix);
    }



    // EDNS Client Subnet(ECS)のアドレス情報のプリフィックス部を取得する.
    public int getEDNSClientSubnetAddressPrefix() throws DNSClientCommonException
    {
        int ecsPrefix = this.ednsClinetSubnetPrefix;
        return ecsPrefix;
    }




    // 問い合わせ先のDNSサーバーのアドレスとポートを設定する.
    public void setDNSServerAddressInfo(InetAddress dnsServerAddress, int dnsServerPort) throws DNSServiceCommonException
    {
        setDNSServerAddress(dnsServerAddress);
        setDNSServerPort(dnsServerPort);
    }

    // 問い合わせ先のDNSサーバーのアドレスとポートを設定する.
    public void setDNSServerAddressInfo(String dnsServerAddress, int dnsServerPort) throws DNSServiceCommonException
    {
        InetAddress address;
        try
        {
            address = InetAddress.getByName(dnsServerAddress);
        }
        catch(UnknownHostException cause)
        {
            String msg = String.format("Failed to build InetAddress object from %s", dnsServerPort);
            DNSServiceCommonException exception = new DNSServiceCommonException(msg, cause);
            throw exception;
        }

        setDNSServerAddressInfo(address, dnsServerPort);
    }





    // システムのデフォルトのDNSサーバーの情報を取得する.
    public InetSocketAddress getSystemDefaultDNSServerInfo() throws DNSServiceCommonException
    {
        DNSUtils dnsUtils = new DNSUtils();
        List<InetSocketAddress> list = dnsUtils.getSystemDNSServerAddressList();
        
        if ( list == null || list.size() < 1 )
        {
            return null;
        }

        InetSocketAddress address = list.get(0);
        return address;
    }



    protected void setMessageTransporterParameter(String key, String value) throws DNSServiceCommonException
    {
        IDNSMessageTransporter  transporter = this.getDNSMessageTransporter();
        if ( transporter == null )
        {
            String msg = String.format("Failed to set DNSMessageTransporter parameter, DNSMessageTransporter is null. key=%s, value=%s", key, value);
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        transporter.setParamerter(key, value);     
    }


    // 本クラスに設定されたEDNSオプションなどをDNS問い合わせメッセージに付加する.
    public IDNSQuestionMessage addEDNSOtpionsToDNSQuestionMessage(IDNSQuestionMessage questionMessage) throws DNSClientCommonException
    {
        try
        {
            List<IDNSResourceRecord> addRRList = new ArrayList<IDNSResourceRecord>();

            // DNS問い合わせメッセージ中の既存のAdditionalセクションを取得する.
            if ( questionMessage.getDNSAdditionalSection() != null )
            {
                // Additionalセクション中の既存のRRを全て取得する.
                IDNSResourceRecord[] rrSet = questionMessage.getDNSAdditionalSection().getDNSResourceRecords();
                for( IDNSResourceRecord rr : rrSet )
                {
                    addRRList.add( rr );
                }
            }


            if ( getEnableEDNS() == true )
            {
                // DNS問い合わせにおいてEDNSを使用する.
                // OPT-RRとしてEDNS0データを挿入する.
                int ednsPayloadSize = 4192;
                int ednsExtendedRCode = 0;
                int doFlag = 0;
                int reservedZ = 0;

                List<IDNSRROptPseudoRRData> pseudoRRSetList = new ArrayList<IDNSRROptPseudoRRData>();

                if ( getEnableEDNSClientSubnet() == true )
                {
                    // EDNS Client Subnet (ECS)を付加する.
                    InetAddress ecsAddress = getEDNSClientSubnetInetAddress();
                    int sourcePrefix = getEDNSClientSubnetAddressPrefix();
                    int scopePrefix = 0;

                    // For Debug.
                    LoulanDNSDebugUtils.printDebug(getClass(), "addEDNSOtpionsToDNSQuestionMessage", String.format("ecsAddress=%s, sourcePrefix=%d, scopePrefix=%d", ecsAddress, sourcePrefix, scopePrefix) );

                    DNSRROptPseudoRRDataForECSImpl ecsPseudoRR = new DNSRROptPseudoRRDataForECSImpl();
                    ecsPseudoRR.setEDNSClientSubnetInfo(ecsAddress, sourcePrefix, scopePrefix);

                    pseudoRRSetList.add( ecsPseudoRR );
                }


                // EDNSのRRを追加.
                IDNSResourceRecordFactory rrFactory = getDNSResourceRecordFactory();
                if ( rrFactory == null )
                {
                    String msg = String.format("Failed to add EDNS option to DNS message, caued ResourceRecordFactory  is not setup." );
                    DNSClientCommonException exception = new DNSClientCommonException(msg);
                    throw exception;
                }

                IDNSResourceRecord rr = rrFactory.createEDNSResourceRecord(ednsPayloadSize, ednsExtendedRCode, doFlag, reservedZ, pseudoRRSetList);
                addRRList.add( rr );
            }


            IDNSAdditionalSection additionalSection = dnsAdditionalSectionFactory.createAdditionalSection(addRRList);
            questionMessage.setDNSAdditionalSection(additionalSection);
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to add EDNS options to DNS Question Message. message=%s", questionMessage.toString() );
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }

        return questionMessage;
    }


    // 引数に与えらえたBoolean文字列(true/false)を解析する.
    // Boolean.parseBoolean()関数は、解析不能な文字列について例外をスローしないため、独自にラップ実装する.
    protected boolean parseBoolean(String valueString) throws DNSClientCommonException
    {
        try
        {
            boolean value = commonUtils.parseBoolean(valueString);
            return value;
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to parse boolean String. value=%s", valueString );
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }
    }

    // 引数に与えらえた整数文字列を解析する.
    protected int parseInt(String valueString) throws DNSClientCommonException
    {
        int value;
        try
        {
            value = Integer.parseInt(valueString);
        }
        catch(NumberFormatException cause)
        {
            String msg = String.format("Failed to parse Integer String. value=%s", valueString);
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }

        return value;
    }

}