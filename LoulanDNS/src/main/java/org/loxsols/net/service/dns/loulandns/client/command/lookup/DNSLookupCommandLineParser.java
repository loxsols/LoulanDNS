package org.loxsols.net.service.dns.loulandns.client.command.lookup;


import org.loxsols.net.service.dns.loulandns.client.common.DNSClientCommonException;
import org.loxsols.net.service.dns.loulandns.client.common.LoulanDNSClientConstants;
import org.loxsols.net.service.dns.loulandns.server.common.constants.DNSLookupCommandConstants;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSDebugUtils;

import android.hardware.broadcastradio.V2_0.IBroadcastRadio.openSessionCallback;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.spi.ExplicitBooleanOptionHandler;

import java.util.*;

// digコマンド互換のコマンドラインパーサークラス
// dig [@server] [-b address] [-c class] [-f filename] [-k filename] [-m] [-p port#] [-q name] [-t type] [-x addr] [-y [hmac:]name:key] [-4] [-6] [name] [type] [class] [queryopt...]
public class DNSLookupCommandLineParser 
{

    @Option(name = "-b", metaVar = "address", required = false,  usage = "source IP address")
    public String srcAddress;

    @Option(name = "-c", metaVar = "class", required = false,    usage = "DNS Class : <IN | CS | CH | HS>")
    public String dnsClass;

    @Option(name = "-f", metaVar = "filename", required = false, usage = "List file of DNS request in batch mode.")
    public String listFileName;

    @Option(name = "-k", metaVar = "filename", required = false, usage = "TSIG key file.")
    public String tsigKeyFileName;

    @Option(name = "-p", metaVar = "port#", required = false, usage = "target DNS server port")
    public String serverPort;

    @Option(name = "-q", metaVar = "name", required = false, usage = "Query Name")
    public String qname;

    @Option(name = "-t", metaVar = "type", required = false, usage = "Query Type")
    public String qtype;

    @Option(name = "-x", metaVar = "addr", required = false, usage = "Reverse lookup IP address")
    public String reverseLookupAddress;

    @Option(name = "-y", metaVar = "[hmac:]name:key", required = false, usage = "TSIG key")
    public String tsigKey;

    @Option(name = "-m", handler = ExplicitBooleanOptionHandler.class, required = false, usage = "Enables memory usage debugging.")
    public boolean enableMemoryUsageDebugging;

    @Option(name = "-4", handler = ExplicitBooleanOptionHandler.class, required = false, usage = "Enable IPv4")
    public boolean enableIPv4;

    @Option(name = "-6", handler = ExplicitBooleanOptionHandler.class, required = false, usage = "Enable IPv6")
    public boolean enableIPv6;

    // 他の引数値が入る配列
    @Argument( required = false, metaVar = "others", usage = "Others ...")
    public String[] others;


    // 以下はargs4jでは解析出来ないコマンドオプション
    // parseメソッドの中で個別に解析する.
    public String serverAddress;

    public String dnsName;
    public String dnsRRType;


    // ---- 以下はクエリオプションの設定値フィールド
    // ------------- EDNS関係のクエリオプションの設定値フィールド
    // +[no]edns (EDNS0)
    public boolean enableEDNS = true;

    // +[no]nsid Name Server Identifier (NSID)
    public boolean enableNSID = false;

    // +[no]subnet=ADDR[/PREFIX-LEN] EDNS Client Subnet (ECS) を指定
    public boolean  enableEDNSClientSubnet = false;
    public String   ednsClientSubnetAddress = "0.0.0.0";
    public int      ednsClientSubnetPrefixLen = 0;

    // +[no]expire EDNS Expire
    public boolean enableEDNSExpire = false;

    // +[no]cookie[=VALUE] DNS Cookie (クッキーの値を指定)
    public boolean enableDNSCookie = false;
    public String dnsCookieValue = null;

    // +[no]keepalive EDNS Keepalive
    public boolean enableEDNSKeepalive = false;

    // +padding=VALUE バイト数で指定したブロックサイズにパディング
    public boolean enablePadding = false;
    public int paddingSize = 0;

    // +[no]ednsopt[=CODE[:VALUE]] 指定したコードとペイロードによるEDNS オプションを付与
    public HashMap<String, String> otherEDNSProperties = new HashMap<String, String>();



    public List<String> queryOptions = new ArrayList<String>();


    public void parse(String[] args) throws DNSClientCommonException
    {

        List<String> tmpList = new ArrayList<String>();
        for( String arg : args )
        {
            tmpList.add( arg );
        }

        List<String> newList = new ArrayList<String>();

        // 以下はargs4jでは解析出来ないコマンドオプションの解析処理
        for( String arg : tmpList )
        {
            // [@server]の解析処理
            if ( arg.startsWith("@") )
            {
                serverAddress = arg.substring(1);
                continue;
            }

            // [queryopt...]の解析処理
            if ( arg.startsWith("+") )
            {
                queryOptions.add(arg);
                continue;
            }

            if ( isDNSClassValue( arg ) )
            {
                dnsClass = arg;
                continue;
            }

            if ( isDNSRRTypeValue( arg ) )
            {
                dnsRRType = arg;
                continue;
            }

            newList.add( arg );
        }


        CmdLineParser parser = new CmdLineParser(this);
        try
        {
            parser.parseArgument(newList);
        }
        catch (CmdLineException cause)
        {

            cause.printStackTrace();

            String msg = "Failed to parse dig command argments.";
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);

            throw exception;
        }


        int i = 0;
        for( String arg : args )
        {
             LoulanDNSDebugUtils.printDebug(getClass(), "parse()", String.format("args[%d]=%s", i, arg ) );
             i++;
        }

        LoulanDNSDebugUtils.printDebug(getClass(), "parse()", String.format("serverPort=%s", serverPort) );
        LoulanDNSDebugUtils.printDebug(getClass(), "parse()", String.format("dnsName=%s", dnsName) );
 

        if ( others == null )
        {
            // String msg = "Failed to parse dig command argments. others is null.";
            // DNSClientCommonException exception = new DNSClientCommonException(msg);
            // throw exception;

            this.dnsName = null;
            return ;
        }


        // クエリオプションの解析処理
        parseQueryOptions( queryOptions );


        LoulanDNSDebugUtils.printDebug(getClass(), "parse()", String.format("others.length=%d", others.length) );
        this.dnsName = others[0];


    }


    public HashMap<String, String> getProperties() throws DNSClientCommonException
    {
        HashMap<String, String> properties = new HashMap<String, String>();

        properties.put(LoulanDNSClientConstants.PROP_KEY_DNS_SERVER_ADDRESS, serverAddress);
        properties.put(LoulanDNSClientConstants.PROP_KEY_DNS_SERVER_PORT, serverPort);
        properties.put(LoulanDNSClientConstants.PROP_KEY_DNS_DOMAIN_CLASS, dnsClass);
        properties.put(LoulanDNSClientConstants.PROP_KEY_DNS_DOMAIN_RR_TYPE, dnsRRType);
        properties.put(LoulanDNSClientConstants.PROP_KEY_DNS_DOMAIN_NAME, dnsName);
        
        
        // EDNSオプションの解析結果を格納する.
        properties.put(LoulanDNSClientConstants.PROP_KEY_DNS_EDNS_ENABLE, Boolean.toString( enableEDNS ) );

        properties.put(LoulanDNSClientConstants.PROP_KEY_DNS_EDNS_NSID_ENABLE, Boolean.toString( enableNSID ) );

        properties.put(LoulanDNSClientConstants.PROP_KEY_DNS_EDNS_ECS_ADDRESS, ednsClientSubnetAddress );
        properties.put(LoulanDNSClientConstants.PROP_KEY_DNS_EDNS_ECS_PREFIX_BITS, Integer.toString(ednsClientSubnetPrefixLen) );

        properties.put(LoulanDNSClientConstants.PROP_KEY_DNS_EDNS_EXPIRE_ENABLE, Boolean.toString( enableEDNSExpire) );

        properties.put(LoulanDNSClientConstants.PROP_KEY_DNS_EDNS_DNSCOOKIE_ENABLE, Boolean.toString( enableDNSCookie ) );

        properties.put(LoulanDNSClientConstants.PROP_KEY_DNS_EDNS_KEEPALIVE_ENABLE, Boolean.toString( enableEDNSKeepalive ) );

        properties.put(LoulanDNSClientConstants.PROP_KEY_DNS_EDNS_PADDING_SIZE, Integer.toString( paddingSize ) );

        
        for( String key : otherEDNSProperties.keySet() )
        {
            String value = otherEDNSProperties.get(key);
            String propKeyEDNSOptX = String.format(LoulanDNSClientConstants.PROP_KEY_DNS_EDNS_ENDSPOPT_CODEX, key);

            properties.put(propKeyEDNSOptX,  value );
        }



        return properties;
    }

    // 指定したプロパティキーに対応する値を返す.
    public String get(String key) throws DNSClientCommonException
    {
        HashMap<String, String> properties = getProperties();

        if ( properties.containsKey(key) )
        {
            String value = properties.get(key);
            return value;
        }
    
        String msg = String.format("Unknown property key : %s", key );
        DNSClientCommonException exception = new DNSClientCommonException(msg);
        throw exception;

    }

    // 指定した文字列がDNSクラス値の場合はtrueを返す
    public boolean isDNSClassValue(String value)
    {
        // 1 the Internet
        if ( value.equals("IN") || value.equals("in") )
        {
            return true;
        }

        // 2 the CSNET class (Obsolete - used only for examples in some obsolete RFCs)
        if ( value.equals("CS") || value.equals("cs") )
        {
            return true;
        }

        // 3 the CHAOS class
        if ( value.equals("CH") || value.equals("ch") )
        {
            return true;
        }

        // 4 Hesiod [Dyer 87]
        if ( value.equals("HS") || value.equals("hs") )
        {
            return true;
        }

        return false;
    }


    // 指定した文字列がDNS RRタイプ値の場合はtrueを返す
    public boolean isDNSRRTypeValue(String value)
    {

        /* 
            A 1 a host address
            NS 2 an authoritative name server
            MD 3 a mail destination (Obsolete - use MX)
            MF 4 a mail forwarder (Obsolete - use MX)
            CNAME 5 the canonical name for an alias
            SOA 6 marks the start of a zone of authority
            MB 7 a mailbox domain name (EXPERIMENTAL)
            MG 8 a mail group member (EXPERIMENTAL)
            MR 9 a mail rename domain name (EXPERIMENTAL)
            NULL 10 a null RR (EXPERIMENTAL)
            WKS 11 a well known service description
            PTR 12 a domain name pointer
            HINFO 13 host information
            MINFO 14 mailbox or mail list information
            MX 15 mail exchange
            TXT 16 text strings
        */

        if ( value.equals("A") || value.equals("a") )
        {
            return true;
        }

        if ( value.equals("NS") || value.equals("ns") )
        {
            return true;
        }

        if ( value.equals("MD") || value.equals("md") )
        {
            return true;
        }

        if ( value.equals("MF") || value.equals("mf") )
        {
            return true;
        }

        if ( value.equals("CNAME") || value.equals("cname") )
        {
            return true;
        }

        if ( value.equals("SOA") || value.equals("soa") )
        {
            return true;
        }

        if ( value.equals("MB") || value.equals("mb") )
        {
            return true;
        }

        if ( value.equals("MR") || value.equals("mr") )
        {
            return true;
        }

        if ( value.equals("NULL") || value.equals("null") )
        {
            return true;
        }

        if ( value.equals("WKS") || value.equals("wks") )
        {
            return true;
        }

        if ( value.equals("PTR") || value.equals("ptr") )
        {
            return true;
        }

        if ( value.equals("HINFO") || value.equals("hinfo") )
        {
            return true;
        }

        if ( value.equals("MINFO") || value.equals("minfo") )
        {
            return true;
        }

        if ( value.equals("MX") || value.equals("mx") )
        {
            return true;
        }

        if ( value.equals("TXT") || value.equals("txt") )
        {
            return true;
        }   




        return false;

    }



    // digコマンドのクエリオプション(+XXXX/+XXXX=VALUE/+[no]XXXX形式)を解析する.
    public void parseQueryOptions(List<String> queryOptions) throws DNSClientCommonException
    {
        for( String otpionString : queryOptions )
        {

            QueryOptionModel queryOption = buildQueryOptionModel(otpionString);

            String code = queryOption.getOptionCode();

            // ------ EDNSオプションの解析
            if ( code.equals(DNSLookupCommandConstants.QUERY_OPTION_CODE_EDNS) )
            {
                // +[no]edns (Enable/Disalbe EDNS0)
                this.enableEDNS = !queryOption.isNegativeOption();
            }
            else if ( code.equals(DNSLookupCommandConstants.QUERY_OPTION_CODE_NSID) )
            {
                // +[no]nsid Name Server Identifier (NSID)
                this.enableNSID = !queryOption.isNegativeOption();

                continue;
            }
            else if ( code.equals(DNSLookupCommandConstants.QUERY_OPTION_CODE_SUBNET) )
            {
                // +[no]subnet=ADDR[/PREFIX-LEN] EDNS Client Subnet (ECS)
                this.enableEDNSClientSubnet = !queryOption.isNegativeOption();

                String address = queryOption.getOptionValue();
                String prefix = null;
                if ( queryOption.value.contains("/") )
                {
                    address = queryOption.value.split("/")[0];
                    prefix = queryOption.value.split("/")[1];
                }

                this.ednsClientSubnetAddress = address;

                if ( prefix != null )
                {
                    this.ednsClientSubnetPrefixLen = Integer.parseInt(prefix);
                }

                continue;
            }
            else if ( code.equals(DNSLookupCommandConstants.QUERY_OPTION_CODE_EXPIRE) )
            {
                // +[no]expire EDNS Expire no
                this.enableEDNSExpire = !queryOption.isNegativeOption();
                
                continue;
            }
            else if ( code.equals(DNSLookupCommandConstants.QUERY_OPTION_CODE_COOKIE) )
            {
            // +[no]cookie[=VALUE] DNS Cookie (クッキーの値を指定) yes VALUE 省略時ランダム
                this.enableDNSCookie = !queryOption.isNegativeOption();
                this.dnsCookieValue = queryOption.getOptionValue();
                
                continue;
            }
            else if ( code.equals(DNSLookupCommandConstants.QUERY_OPTION_CODE_KEEPALIVE) )
            {
                // +[no]keepalive EDNS Keepalive
                this.enableEDNSKeepalive = !queryOption.isNegativeOption();
                
                continue;
            }
            else if ( code.equals(DNSLookupCommandConstants.QUERY_OPTION_CODE_PADDING) )
            {
                // +padding=VALUE バイト数で指定したブロックサイズにパディングno
                this.enablePadding = !queryOption.isNegativeOption();
                this.paddingSize = Integer.parseInt( queryOption.getOptionValue() );
                
                continue;
            }
            else if ( code.equals(DNSLookupCommandConstants.QUERY_OPTION_CODE_EDNSOPT) )
            {
                // +[no]ednsopt[=CODE[:VALUE]] 指定したコードとペイロードによる任意のEDNS オプションを付与
                otherEDNSProperties.put( queryOption.getOptionCode(), queryOption.getOptionValue() );
                
                continue;
            }

            // TODO : 上記以外のクエリオプションの解析部分は未実装


            // 未知のクエリオプションを検出したので例外をスローする.
            String msg = String.format("Failed to parse DNS Lookup Query Option : %s", otpionString );
            DNSClientCommonException exception = new DNSClientCommonException(msg);
            throw exception;
        }

    }


    // digコマンドのクエリオプション(+XXXX/+XXXX=VALUE/+[no]XXXX形式)を表すモデルクラス.
    class QueryOptionModel
    {
        String optionCode;
        boolean noFlag;
        String value;

        public QueryOptionModel(String optionCode, boolean noFlag, String value) throws DNSClientCommonException
        {
            this.optionCode = optionCode;
            this.noFlag = noFlag;
            this.value = value;
        }


        public String getOptionCode()
        {
            return optionCode;
        }

        public boolean isNegativeOption()
        {
            return noFlag;
        }

        public boolean hasOptionValue()
        {
            if ( value != null )
            {
                return true;
            }

            return false;
        }

        public String getOptionValue()
        {
            return value;
        }

    }


    // digコマンドのクエリオプション(+XXXX/+XXXX=VALUE/+[no]XXXX形式)を解析する.
    public QueryOptionModel buildQueryOptionModel(String queryOption) throws DNSClientCommonException
    {
        if ( queryOption.startsWith("+") == false )
        {
            // 指定されたクエリオプションは"+"記号から開始されていない.
            String msg = String.format("Invalid DNS Query Option, because of ahead word is not PLUS(+). option=%s", queryOption );
            DNSClientCommonException exception = new DNSClientCommonException(msg);
            throw exception;
        }

        // クエリオプションのオプションコード部分(+key/+key=value/+[no]key/+[no]key=value形式のkey部分)
        String optionCode = queryOption.substring(1);

        // +[no]key=value/+[no]key形式で[no]が入っている場合のフラグ(TRUEならnoが設定されている.)
        boolean noFlag = false;
        if( optionCode.startsWith("no") )
        {
            noFlag = true;
            optionCode = optionCode.substring(2);
        }

        // +key=value/+[no]key=value形式の場合のvalue部分(無ければnullに設定する)
        String value = null;
        if ( optionCode.contains("=") )
        {
            value = optionCode.split("=")[1];
            optionCode = optionCode.split("=")[0];
        }


        QueryOptionModel model = new QueryOptionModel(optionCode, noFlag, value);

        return model;
    }


}