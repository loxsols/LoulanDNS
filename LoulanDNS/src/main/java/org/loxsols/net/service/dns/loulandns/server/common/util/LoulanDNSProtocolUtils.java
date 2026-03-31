package org.loxsols.net.service.dns.loulandns.server.common.util;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.regex.Matcher;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.common.constants.DNSProtocolConstants;
import org.loxsols.net.service.dns.loulandns.server.logical.model.UserInfo;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.model.SimpleDNSProtocolModelInstanceFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.DNSHeaderSectionImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.DNSMessageImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.DNSQuestionMessageImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.DNSResponseMessageImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSQuestionMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSHeaderSection;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSResourceRecord;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.IDNSQueryPart;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.IDNSRROptPseudoRRData;
import org.loxsols.net.service.dns.loulandns.server.logical.model.UserGroupInfo;
import org.loxsols.net.service.dns.loulandns.server.logical.model.UserGroupMappingInfo;
import org.loxsols.net.service.dns.loulandns.server.logical.model.UserGroupPrivilegeInfo;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.*;

public class LoulanDNSProtocolUtils
{

    // DNSメッセージを人間が読める文字列に変換する.
    public String toDNSDebugMessage(byte[] dnsMessageBytes) throws DNSServiceCommonException
    {

        IDNSMessage message = parseDNSMessageBytes( dnsMessageBytes );

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);


        // ----- Headerセクションの出力
        if ( message.getDNSHeaderSection() != null )
        {
            // ;; ->>HEADER<<- opcode: QUERY, status: NOERROR, id: 61328
            String opcodeString = message.getDNSHeaderSection().getOPCodeString();
            String rcodeString = message.getDNSHeaderSection().getRCodeString();
            int id = (int)message.getDNSHeaderSection().getID();
            ps.println( String.format(";; ->>HEADER<<- opcode: %s, status: %s, id: %d", opcodeString, rcodeString, id));

            // ;; flags: qr rd ra; QUERY: 1, ANSWER: 1, AUTHORITY: 0, ADDITIONAL: 1
            boolean qr = message.getDNSHeaderSection().getBooleanQR();
            boolean aa = message.getDNSHeaderSection().getBooleanAA();
            boolean tc = message.getDNSHeaderSection().getBooleanTC();
            boolean rd = message.getDNSHeaderSection().getBooleanRD();
            boolean ra = message.getDNSHeaderSection().getBooleanRA();
            String flagsString = String.format("%s %s %s %s %s", 
                                                (qr ? "QR" : ""),
                                                (aa ? "AA" : ""),
                                                (tc ? "TC" : ""),
                                                (rd ? "RD" : ""),
                                                (ra ? "RA" : "") );
            ps.println( String.format(";; flags: %s; QUERY: %d, ANSWER: %d, AUTHORITY: %d, ADDITIONAL: %d", 
                                                flagsString,
                                                message.getDNSHeaderSection().getQDCOUNT(),
                                                message.getDNSHeaderSection().getANCOUNT(),
                                                message.getDNSHeaderSection().getNSCOUNT(),
                                                message.getDNSHeaderSection().getARCOUNT()));

            if  (message.isEDNS0Message() == true )
            {
                // ;; OPT PSEUDOSECTION:
                ps.println( String.format(";; OPT PSEUDOSECTION:"));
            
                // ; EDNS: version: 0, flags:; udp: 1232
                int ednsVersion = message.getEDNS0OPTResourceRecord().getEDNS0Version();
                int ednsUDPPayloadSize = message.getEDNS0OPTResourceRecord().getEDNS0UDPPayloadSize();
                ps.println( String.format(" EDNS: version: %d, flags:; udp: %d", ednsVersion, ednsUDPPayloadSize));
            }

        }

        // ----- Questionセクションの出力
        if ( message.getDNSQuestionSection() != null )
        {
            //  ;; QUESTION SECTION:
            ps.println( String.format(";; QUESTION SECTION:"));
            
            //  ;www.google.co.jp.              IN      A
            for( IDNSQueryPart query : message.getDNSQuestionSection().getDNSQueries() )
            {
                String dnsClassString = toDNSClassValueString(query.getDNSQueryClass() );

                String dnsRRTypeString;
                if ( isValidRangeDNSRRTypeValue( query.getDNSQueryType() ) )
                {
                    dnsRRTypeString = toDNSRRTypeString( query.getDNSQueryType() );
                }
                else
                {
                    dnsRRTypeString = "Invalid DNS RR Type : " + query.getDNSQueryType();
                }

                ps.println( String.format(";%s              %s      %s", query.getDNSQueryName(), dnsClassString, dnsRRTypeString));
            }

        }

        //Answerセクションの文字列化
        if ( message != null && message.getDNSAnswerSection() != null )
        {
            // ;; ANSWER SECTION:
             ps.println( String.format(";; ANSWER SECTION:"));

            //  www.google.co.jp.       148     IN      A       142.250.207.99
            for( IDNSResourceRecord rr : message.getDNSAnswerSection().getDNSResourceRecords() )
            {
                String dnsRName = rr.getDNSResourceName();

                String dnsRTTL = String.format("%d", rr.getResourceTTL() );

                String dnsRClassString = toDNSClassValueString( rr.getResourceClass() );

                String dnsRTypeString;
                if ( isValidRangeDNSRRTypeValue( rr.getResourceType() ) )
                {
                    dnsRTypeString = toDNSRRTypeString( rr.getResourceType() );
                }
                else
                {
                    dnsRTypeString = "Invalid DNS RR Type : " + rr.getResourceType();
                }

                String dnsRDataString = rr.getResourceRDataString();

                ps.println( String.format("%s       %s     %s      %s       %s", dnsRName, dnsRTTL, dnsRClassString, dnsRTypeString, dnsRDataString));
            }

        }

        // PrintStreamに出力したデータを文字列として回収する.
        ps.flush();
        String outputText = os.toString();

        return outputText;
        
    }


    // byte配列を16進数文字列に変換する.
    public String toHexString(byte[] bytes)
    {
        StringBuffer buf = new StringBuffer();
        
        for( byte b : bytes )
        {
            HexFormat hexFormat = HexFormat.of();

            // String hexPart = hexFormat.toHexDigits(b);
            String hexPart = String.valueOf( hexFormat.toHighHexDigit(b) ) + String.valueOf( hexFormat.toLowHexDigit(b) );

            buf.append(hexPart);

            
            // System.out.println( String.format("[DEBUG] LoulanDNSProtocolUtils.toDNSDebugMessage() : hexPart=%s", hexPart) );

        }
        
        String hexString = buf.toString();
        return hexString;
    }

    // byte配列をデバッグ用の16進数文字列に変換する.
    //  16進数を見やすいように加工する.
    public String toDebugHexString(byte[] bytes) 
    {

        System.out.println("[DEBUG] LoulanDNSProtocolUtils.toDebugHexString() : called.");
        System.out.println( String.format("[DEBUG] LoulanDNSProtocolUtils.toDebugHexString() : bytes.length=%d", bytes.length) );

        String hexString = toHexString(bytes);

        System.out.println( String.format("[DEBUG] LoulanDNSProtocolUtils.toDebugHexString() : hexString.length()=%d", hexString.length() ) );

        StringBuffer buf = new StringBuffer();
        if ( hexString.length() >= 32 )
        {
            for( int i=0; i < ( hexString.length() / 32 ) + 1; i++ )
            {
                String line = "";

                int start = i * 32;
                for( int j= start; j < start + 32 && j < hexString.length(); j+=2)
                {
                    line += hexString.substring(j, j+2) + " ";
                }

                line += "\n";

                buf.append(line);

                // System.out.println( String.format("[DEBUG] LoulanDNSProtocolUtils.toDebugHexString() : line=%s", line) );
            }
        }
        else
        {
            String line = "";
            for( int j=0; j < hexString.length(); j++)
            {
                line += hexString.substring(j, j+1) + " ";
            }
            line += "\n";

            buf.append(line);

            // System.out.println( String.format("[DEBUG] LoulanDNSProtocolUtils.toDebugHexString() : line=%s", line) );
        }

        String debugHexString = buf.toString();

        System.out.println( String.format("[DEBUG] LoulanDNSProtocolUtils.toDebugHexString() : debugHexString is bellow.") );
        System.out.println( "******************************");
        System.out.print( debugHexString );
        System.out.println( "******************************");

        return debugHexString;
    }



    // ドメイン名が圧縮されているかを判定する.
    // (ドメイン名圧縮とは複数のリソースレコードで繰り返し出現するドメイン名を一か所に集約して表現する方法)
    public boolean isCompressedDomainName(byte[] dname) throws MalformedDomainNameException
    {

        if ( dname.length < 1 )
        {
            String msg = String.format("Invalid dname length. dname.length=%d", dname.length);
            MalformedDomainNameException exception = new MalformedDomainNameException(msg);
            throw exception;
        }

        boolean ret = false;
        // ドメイン名の先頭2bitが11の場合、ドメイン名は圧縮されている.
        if ( ( dname[0] & 0b11000000) == 0b11000000 )
        {
            ret = true;
        }

        // LoulanDNSDebugUtils.printDebug( this.getClass(), "isCompressedDomainName()", String.format("dname[0]=0x%X, ret=%b", dname[0], ret));

        return ret;
    }


    // ドメイン名圧縮のオフセットを計算する.
    // 戻り値のオフセット値はDNSメッセージ(DNSヘッダセクション含む)の先頭からの値として使用する.
    public int calcCompressedDomainNameOffset(byte[] dname) throws MalformedDomainNameException
    {
        if ( isCompressedDomainName( dname ) == false )
        {
            String msg = String.format("Not compressed DomainName. dname[0] = %X", dname[0] );
            MalformedDomainNameException exception = new MalformedDomainNameException(msg);
            throw exception;
        }


        // dnameの先頭2byteのうち、下位14bitがオフセット値.
        //   オフセット値の最大値は理論上は16383(2^14 - 1)だが、この値を信用するとおそらく領域外読み込みの脆弱性の原因となるだろう.
        int offset = ( ( dname[0] & 0b00111111 ) << 8 ) | dname[1];


        // オフセット値はDNSメッセージの先頭からの値なので、少なくともヘッダーセクションのサイズは越えなければならない.
        if ( offset < DNSProtocolConstants.SIZE_OF_DNS_HEADER_SECION )
        {
            String msg = String.format("Invalid Compressed DomainName offset. Offset value is too minimum. offset=%d", offset);
            MalformedDomainNameException exception = new MalformedDomainNameException(msg);
            throw exception;
        }

        return offset;
    }


    // ドメイン名のbyte長を計算する.
    public int calcDomainNameByteSize(byte[] dname) throws MalformedDomainNameException
    {

        // ドメイン名圧縮が適用されているかを判定する.
        if ( isCompressedDomainName( dname ) )
        {
            // ドメイン名圧縮が適用されている場合は一律に2byteで返す.
            return 2;
        }

        if ( dname.length == 1 )
        {
            // ドメイン名が空文字の場合は解析せずに1(文字列長の格納バイト部分の長さ)を返す.
            return 1;
        }

        String dnameString = parseDomainName( dname );

        if ( dnameString == null )
        {
            String msg = String.format("Failed to parse DomainName bytes.");
            MalformedDomainNameException exception = new MalformedDomainNameException(msg);
            throw exception;
        }

        // TODO : マルチバイト文字を含むドメイン名の場合の処理が未了.

        // ドメイン名のバイト長はASCII文字列長に終端のNULL文字を加えた長さ.
        int size = dnameString.length() + 1;

        return size;
    }


    // DNSクエリ文字列を解析する.
    //  TODO : 日本語ドメイン名(マルチバイト文字)に非対応.
    public String parseDomainName(byte[] dname) throws MalformedDomainNameException
    {
        //  ドメイン名の構造 | 長さ(1byte) | ASCII文字列(.を含まない) | 長さ(1byte) | ASCII文字列(.を含まない)  | ... | 終端NULL文字 |

        StringBuffer buffer = new StringBuffer();


        // ++++++++++++++++++++++++++++
        // For DEBUG.
        // ++++++++++++++++++++++++++++
        String debugHexString = toDebugHexString( dname );
        System.out.println("[DEBUG] parseDomainName() ++++++++++++++++++++++++++++ ");
        System.out.println( debugHexString );
        System.out.println("[DEBUG] parseDomainName() ---------------------------- ");
        // ----------------------------
        // For DEBUG.
        // ----------------------------

        int i = 0;
        boolean isTerminated = false;
        while(i < dname.length )
        {
            int len = 0x00ff & ( dname[i] );

            if ( len == DNSProtocolConstants.ASCII_CODE_NULL )
            {
                // ドメイン名の終端(NULL文字)に達した.
                isTerminated = true;

                // FQDN名なので末尾に.を付記する.
                // buffer.append(".");

                break;
            }

            if ( len > DNSProtocolConstants.MAX_DOMAIN_NAME_LABEL_LENGTH )
            {
                // ドメイン名中のラベル文字列の最大長(63文字)を超過している.
                String msg = String.format("Excess size of Domain Name Label : length=%d", len);
                MalformedDomainNameException execption = new MalformedDomainNameException(msg);
                throw execption; 
            }

            if ( i + len + 1 > DNSProtocolConstants.MAX_DOMAIN_NAME_LENGTH )
            {
                // FQDN名の最大長(254文字)を超過している.
                String msg = String.format("Excess size of FQDN : length=%d", i + len + 1);
                MalformedDomainNameException execption = new MalformedDomainNameException(msg);
                throw execption; 
            }


            byte[] labelBytes = new byte[len];
            System.arraycopy(dname, i+1, labelBytes, 0, len);

            Charset charset = getCharsetForParseDomainName();
            String label = new String(labelBytes, charset );

            buffer.append(label);
            buffer.append(".");


            i += len + 1;
        }

        String dnameString = buffer.toString();


        LoulanDNSDebugUtils.printDebug( this.getClass(), "parseDomainName()", String.format("dnameString=%s", dnameString) );



        if (isTerminated == false)
        {
            // NULL終端していない.
            String msg = String.format("Specified Domain Name is not NULL terminated. dname=%s", dnameString );
            MalformedDomainNameException execption = new MalformedDomainNameException(msg);
            throw execption; 
        }


        // TODO : 日本語ドメイン(マルチバイト文字)の可能性があるため、以下の実装はコメントアウトする.
        // 正規表現でドメイン名を判定する.
        // Matcher matcher = DNSProtocolConstants.REGULAR_EXPRESSION_PATTERN_OF_DOMAIN_NAME.matcher( dnameString );
        // if ( matcher.matches() == false )
        // {
        //    // ドメイン名として使用できない文字が含まれている.
        //    String msg = String.format("Invalid Domain Name. dname=%s", dnameString );
        //    MalformedDomainNameException execption = new MalformedDomainNameException(msg);
        //    throw execption; 
        // }


        return dnameString;
    }


    // ドメイン名文字列からDNSメッセージ中で使用するbyte配列を生成する.
    public byte[] toDomainNameBytes(String dnameString) throws MalformedDomainNameException
    {
        //  ドメイン名の構造 | 長さ(1byte) | ASCII文字列(.を含まない) | 長さ(1byte) | ASCII文字列(.を含まない)  | ... | 終端NULL文字 |

        if ( dnameString.length() == 0 )
        {
            // DNSラベル文字列が空文字の場合は以下のbyte[]{0}を返す.
            byte[] emptyLabelBytes = new byte[]{0};
            return emptyLabelBytes;
        }

        String[] labels = dnameString.split("\\.");

        // ドメイン名のbyte配列はNULL文字を含めて最長255文字.
        ByteArrayOutputStream stream = new ByteArrayOutputStream();


        for( int i=0; i < labels.length; i++)
        {
            String label = labels[i];

            int len = label.length();
            byte[] labelBytes = label.getBytes();

            if ( len > DNSProtocolConstants.MAX_DOMAIN_NAME_LABEL_LENGTH )
            {
                // ドメイン名中のラベル文字列の最大長(63文字)を超過している.
                String msg = String.format("Excess size of Domain Name Label : length=%d, label=%s", len, label );
                MalformedDomainNameException execption = new MalformedDomainNameException(msg);
                throw execption; 
            }

            stream.writeBytes( new byte[]{ (byte)len } );
            stream.writeBytes(labelBytes);
        }

        stream.writeBytes(new byte[]{(byte)DNSProtocolConstants.ASCII_CODE_NULL});

        byte[] dname = stream.toByteArray();

        // NULL文字を含まないドメイン名のバイト配列長の長さが254文字を超過する場合.
        if ( dname.length - 1 > DNSProtocolConstants.MAX_DOMAIN_NAME_LENGTH )
        {
            // FQDN名の最大長(254文字)を超過している.
            String msg = String.format("Excess size of FQDN : length=%d, domainName=%s", dname.length - 1, dnameString);
            MalformedDomainNameException execption = new MalformedDomainNameException(msg);
            throw execption; 
        }


        return dname;
    }



    // ドメイン名を解析する際に使用する文字コードを取得する.
    public Charset getCharsetForParseDomainName()
    {
        Charset charset = LoulanDNSConstants.CONST_DOMAIN_NAME_CHARSET;
        return charset;
    }


    // 1byteの整数をbyte配列に変換する.
    public byte[] int8ToBytes(int value)
    {
        byte[] bytes = new byte[1];

        bytes[0] = (byte)( ( value >> 0 ) & 0xff );
        return bytes;
    }



    // 2byteのshort型をbyte配列に変換する.
    public byte[] shortToBytes(short value)
    {

        int i16 = 0xffff & value;

        byte[] bytes = int16ToBytes(i16);
        return bytes;
    }


    // 2byteの整数をbyte配列に変換する.
    public byte[] int16ToBytes(int value)
    {
        byte[] bytes = new byte[2];

        bytes[0] = (byte)( ( value >> 8 ) & 0xff );
        bytes[1] = (byte)( ( value >> 0 ) & 0xff );

        return bytes;
    }


    // 4byteのint型をbyte配列に変換する.
    public byte[] intToBytes(int value)
    {
        byte[] bytes = int32ToBytes(value);
        return bytes;
    }

    // 4byteの整数をbyte配列に変換する.
    public byte[] int32ToBytes(int value)
    {
        byte[] bytes = new byte[4];

        bytes[0] = (byte)( ( value >> 24 ) & 0xff );
        bytes[1] = (byte)( ( value >> 16 ) & 0xff );
        bytes[2] = (byte)( ( value >>  8 ) & 0xff );
        bytes[3] = (byte)( ( value >>  0 ) & 0xff );

        return bytes;
    }


    // byte型配列の先頭から1byteを8bit符号なし整数型として解析してlong型整数値として返す.
    public long bytesToUInt8(byte[] bytes)
    {
        long value8 = (0xff & bytes[0]);
        value8 = value8 & 0xff;

        return value8;
    }



    // byte型配列の指定位置から1byteを8bit符号なし整数型として解析してlong型整数値として返す.
    public long bytesToUInt8(byte[] bytes, int srcIndex)
    {
        byte[] oneBytes = new byte[1];
        oneBytes[0] = bytes[srcIndex];

        long value = bytesToUInt8(oneBytes);
        return value;
    }



    // byte型配列の先頭から2byteを16bit符号なし整数型として解析してlong型整数値として返す.
    public long bytesToUInt16(byte[] bytes)
    {
        long value16 = ( (0x00ff & bytes[0] ) << 8) | ( 0xff & bytes[1] );
        value16 = value16 & 0xffff;

        return value16;
    }



    // byte型配列の指定位置から2byteを16bit符号なし整数型として解析してlong型整数値として返す.
    public long bytesToUInt16(byte[] bytes, int srcIndex)
    {
        byte[] shortBytes = new byte[2];
        shortBytes[0] = bytes[srcIndex];
        shortBytes[1] = bytes[srcIndex + 1];

        long value = bytesToUInt16(shortBytes);
        return value;
    }

    
    // byte型配列の先頭から4byteを32bit符号なし整数型として解析してlong型整数値として返す.
    public long bytesToUInt32(byte[] bytes)
    {
        long value32 = ( ( 0xff & bytes[0] << 24 ) | ( 0xff & bytes[1] << 16) | ( 0xff & bytes[2] << 8) | (0xff & bytes[3] ) );
        value32 = value32 & 0xffffffff;

        return value32;
    }


    // byte型配列の指定位置から4byteを32bit符号なし整数型として解析してlong型整数値として返す.
    public long bytesToInt(byte[] bytes, int srcIndex)
    {
        byte[] intBytes = new byte[4];
        intBytes[0] = bytes[srcIndex];
        intBytes[1] = bytes[srcIndex + 1];
        intBytes[2] = bytes[srcIndex + 2];
        intBytes[3] = bytes[srcIndex + 3];  

        long value32 = bytesToUInt32(intBytes);
        return value32;
    }



    // 32bitのランダム値を生成する.
    public int getRandom32bitValue()
    {
        Random random = new Random();
        int val32 = random.nextInt();

        return val32;
    }


    // 16bitのランダム値を生成する.
    public short getRandom16bitValue()
    {
        int val32 = getRandom32bitValue();
        short val16 = (short)val32;

        return val16;
    }

    // BOOLEAN型の値をint型に変換する.
    public int booleanToInt(boolean bValue)
    {
        int value = 0;
        if ( bValue == true )
        {
            value = 1;
        }

        return value;

    }

    
    public String toBase64String(byte[] bytes) throws DNSServiceCommonException
    {
        Base64.Encoder encoder = Base64.getEncoder();
        String base64String = encoder.encodeToString( bytes );

        return base64String;
    }


    public byte[] base64StringToBytes(String base64String) throws DNSServiceCommonException
    {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] bytes = decoder.decode( base64String );
        return bytes;
    }


    // DNSのCLASSの値を文字列に変換する. (例 : 1 -> "IN" )
    public String toDNSClassValueString(int dnsClass) throws DNSServiceCommonException
    {
        if ( dnsClass == DNSProtocolConstants.DNS_CLASS_IN )
        {
            return "IN";
        }
        else if ( dnsClass == DNSProtocolConstants.DNS_CLASS_CS )
        {
            return "CS";
        }
        else if ( dnsClass == DNSProtocolConstants.DNS_CLASS_CH )
        {
            return "CH";
        }
        else if ( dnsClass == DNSProtocolConstants.DNS_CLASS_HS )
        {
            return "HS";
        }
        else
        {
            return "UNKNOWN";
        }

    }


    // DNSのリソースレコード(RR)のタイプ値として妥当な範囲の値かを判定する.
    public boolean isValidRangeDNSRRTypeValue(int rrType)
    {
        if( rrType < 0 )
        {
            // 負数のDNS RRタイプはおかしい.
            return false;
        }
        else if( rrType > 65535 )
        {
            // DNS RRタイプは16bit値なので上限越え.
            return false;
        }

        return true;
    }


    // DNSのリソースレコード(RR)のタイプ値を文字列に変換する.
    public String toDNSRRTypeString(int rrType) throws DNSServiceCommonException
    {
        if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_A)
        {
            //  a host address 	[RFC1035]
            return "A";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_NS)
        {
            //  an authoritative name server 	[RFC1035] 
            return "NS";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_MD)
        {
            //  a mail destination (OBSOLETE - use MX)
            return "MD";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_MF)
        {
            //  a mail forwarder (OBSOLETE - use MX) 	[RFC1035] 	 
            return "MF";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_CNAME)
        {
            //  the canonical name for an alias 	[RFC1035] 	 
            return "CNAME";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_SOA)
        {
            //  marks the start of a zone of authority 	[RFC1035]
            return "SOA";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_MB)
        {
            //  a mailbox domain name (EXPERIMENTAL) 	[RFC1035] 	
            return "MB";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_MG)
        {
            //  a mail group member (EXPERIMENTAL) 	[RFC1035]	
            return "MG";
        }   
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_MR)
        {
            ///  a mail rename domain name (EXPERIMENTAL) 	[RFC1035] 
            return "MR";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_NULL)
        {
            //  a null RR (EXPERIMENTAL) 	[RFC1035]
            return "NULL";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_WKS)
        {
            ///  a well known service description 	[RFC1035] 	
            return "WKS";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_PTR)
        {
            //  a domain name pointer 	[RFC1035]
            return "PTR";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_HINFO)
        {
            //  a domain name pointer 	[RFC1035]
            return "HINFO";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_MX)
        {
            //  mail exchange 	[RFC1035]
            return "MX";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_TXT)
        {
            //  text strings
            return "TXT";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_RP)
        {
            //  for Responsible Person 	[RFC1183] 
            return "RP";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_AFSDB)
        {
            //  for AFS Data Base location 	[RFC1183][RFC5864]
            return "AFSDB";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_X25)
        {
            //  for X.25 PSDN address 	[RFC1183]
            return "X25";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_ISDN)
        {
            //  for ISDN address 	[RFC1183]
            return "ISDN";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_RT)
        {
            //  for Route Through 	[RFC1183] 	
            return "RT";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_NSAP)
        {
            //  for NSAP address, NSAP style A record (DEPRECATED) 	[RFC1706][Moving TPC.INT and NSAP.INT infrastructure domains to historic] 		
            return "NSAP";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_NSAP_PTR)
        {
            //  for domain name pointer, NSAP style (DEPRECATED) 	[RFC1706][Moving TPC.INT and NSAP.INT infrastructure domains to historic] 	
            return "NSAP_PTR";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_SIG)
        {
           //  for security signature 	[RFC2536][RFC2931][RFC3110][RFC4034]
           return "SIG";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_KEY)
        {
           //  for security key 	[RFC2536][RFC2539][RFC3110][RFC4034]
           return "KEY";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_PX)
        {
           //  X.400 mail mapping information 	[RFC2163] 	
           return "PX";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_GPOS)
        {
           //  Geographical Position 	[RFC1712] 
           return "GPOS";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_AAAA)
        {
           //  Geographical Position 	[RFC1712] 
           return "AAAA";
        } 
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_LOC)
        {
           //  Location Information 	[RFC1876] 	
           return "LOC";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_NXT)
        {
           //  Next Domain (OBSOLETE) 	[RFC2535]	
           return "NXT";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_EID)
        {
           //  Endpoint Identifier 	[Michael_Patton][http://ana-3.lcs.mit.edu/~jnc/nimrod/dns.txt] 		
           return "EID";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_NIMLOC)
        {
           //  Nimrod Locator 	[1][Michael_Patton][http://ana-3.lcs.mit.edu/~jnc/nimrod/dns.txt] 		
           return "NIMLOC";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_SRV)
        {
            //  Server Selection 	[1][RFC2782] 		
           return "SRV";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_ATMA)
        {
            //  34
            //  ATM Address 	[ ATM Forum Technical Committee, "ATM Name System, V2.0", Doc ID: AF-DANS-0152.000, July 2000. Available from and held in escrow by IANA.] 	
           return "SRV";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_NAPTR)
        {
            //  35
            //  Naming Authority Pointer 	[RFC3403]         
            return "NAPTR";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_KX)
        {
            //  36
            //  Key Exchanger 	[RFC2230]    
            return "KX";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_CERT)
        {
            //  37
            //  CERT 	[RFC4398]
            return "CERT";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_A6)
        {
            //  38
            //  A6 (OBSOLETE - use AAAA) 	[RFC2874][RFC3226][RFC6563] 	
            return "A6";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_DNAME)
        {
            //  39
            //  DNAME 	[RFC6672] 	
            return "DNAME";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_SINK)
        {
            //  40
            //  SINK 	[Donald_E_Eastlake][draft-eastlake-kitchen-sink-02] 
            return "SINK";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_OPT)
        {
            //  41
            //  OPT 	[RFC3225][RFC6891]
            return "SINK";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_APL)
        {
            //  42
            //  APL 	[RFC3123] 	
            return "APL";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_DS)
        {
            //  43
            //  Delegation Signer 	[RFC4034]
            return "DS";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_SSHFP)
        {
            //  44
            //  SSH Key Fingerprint 	[RFC4255] 	
            return "SSHFP";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_IPSECKEY)
        {
            //  45
            //  IPSECKEY 	[RFC4025] 	
            return "IPSECKEY";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_RRSIG)
        {
            //  46
            //  RRSIG 	[RFC4034] 
            return "RRSIG";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_NSEC)
        {
            //  47
            //  NSEC 	[RFC4034][RFC9077] 	
            return "RRSIG";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_DNSKEY)
        {
            //  48
            //  DNSKEY 	[RFC4034]
            return "DNSKEY";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_DHCID)
        {
            //  49
            //  DHCID 	[RFC4701] 	
            return "DHCID";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_NSEC3)
        {
            //  50
            //  NSEC3 	[RFC5155][RFC9077]
            return "NSEC3";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_NSEC3PARAM)
        {
            //  51
            //  NSEC3PARAM 	[RFC5155] 	
            return "NSEC3PARAM";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_TLSA)
        {
            //  52
            /// TLSA 	[RFC6698]	
            return "TLSA";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_SMIMEA)
        {
            //  53
            //  S/MIME cert association 	[RFC8162]
            return "SMIMEA";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_UNASSIGNED_54)
        {
            //  54
            /// Unassigned
            return "UNASSIGNED(54)";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_HIP)
        {
            //  55
            //  Host Identity Protocol 	[RFC8005] 	
            return "HIP";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_NINFO)
        {
            //  56
            //  NINFO 	[Jim_Reid]
            return "NINFO";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_RKEY)
        {
            //  57
            //  RKEY 	[Jim_Reid]
            return "RKEY";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_TALINK)
        {
            //  58
            //  Trust Anchor LINK 	[Wouter_Wijngaards]
            return "TALINK";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_CDS)
        {
            //  59
            //  Child DS 	[RFC7344]
            return "CDSNK";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_CDNSKEY)
        {
            //  60
            //  DNSKEY(s) the Child wants reflected in DS 	[RFC7344]
            return "CDNSKEY";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_OPENPGPKEY)
        {
            //  61
            //  OpenPGP Key 	[RFC7929]
            return "OPENPGPKEY";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_CSYNC)
        {
            //  62
            //  Child-To-Parent Synchronization 	[RFC7477]
            return "CSYNC";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_ZONEMD)
        {
            //  63
            //  Message Digest Over Zone Data 	[RFC8976]
            return "ZONEMD";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_SVCB)
        {
            //  64
            //   General-purpose service binding 	[RFC9460]
            return "SVCB";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_HTTPS)
        {
            //  65
            //  SVCB-compatible type for use with HTTP 	[RFC9460]
            return "HTTPS";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_DSYNC)
        {
            //  66
            //  Endpoint discovery for delegation synchronization 	[RFC-ietf-dnsop-generalized-notify-09]
            return "DSYNC";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_HHIT)
        {
            //  67
            //   Hierarchical Host Identity Tag 	[draft-ietf-drip-registries-28]
            return "HHIT";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_BRID)
        {
            //  68
            //   UAS Broadcast Remote Identification
            return "BRID";
        }
        else if (   rrType >= DNSProtocolConstants.DNS_RR_TYPE_UNASSIGNED_69 && 
                    rrType <= DNSProtocolConstants.DNS_RR_TYPE_UNASSIGNED_98   )
        {
            //  69 - 98
            //  Unassigned
            return "UNASSIGNED(69-98)";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_SPF)
        {
            //  99
            //  [RFC7208] 	
            return "SPF";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_UINFO)
        {
            //  100
            //  [IANA-Reserved]
            return "UINFO";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_UID)
        {
            //  101
            //  [IANA-Reserved] 
            return "UID";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_GID)
        {
            //  102
            //   [IANA-Reserved]
            return "GID";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_UNSPEC)
        {
            //  103
            //   [IANA-Reserved]
            return "UNSPEC";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_NID)
        {
            //  104
            //   [RFC6742]
            return "NID";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_L32)
        {
            //  105
            //  [RFC6742]
            return "L32";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_L64)
        {
            //  106
            //  [RFC6742]
            return "L64";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_LP)
        {
            //  107
            //  [RFC6742]
            return "LP";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_EUI48)
        {
            //  108
            //  an EUI-48 address 	[RFC7043]
            return "EUI48";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_EUI64)
        {
            //  109
            //  an EUI-64 address 	[RFC7043]
            return "EUI48";
        }
        else if ( rrType >= DNSProtocolConstants.DNS_RR_TYPE_UNASSIGNED_110 && 
                   rrType <= DNSProtocolConstants.DNS_RR_TYPE_UNASSIGNED_127   )
        {
            //  110 - 127 
            //  UNASSIGNED 
            return "UNASSIGNED(110-127)";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_NXNAME)
        {
            //  128
            //  NXDOMAIN indicator for Compact Denial of Existence 	[RFC-ietf-dnsop-compact-denial-of-existence-07]
            return "NXNAME";
        }
        else if ( rrType >= DNSProtocolConstants.DNS_RR_TYPE_UNASSIGNED_129 && 
                    rrType <= DNSProtocolConstants.DNS_RR_TYPE_UNASSIGNED_248 )
        {
            //  129 - 248 
            //  Unassigned
            return "UNASSIGNED(129-248)";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_TKEY)
        {
            //  249
            /// Transaction Key 	[RFC2930]
            return "TKEY";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_TSIG)
        {
            //  250
            //   Transaction Signature 	[RFC8945]
            return "TSIG";
        } 
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_IXFR)
        {
            //  251
            //   incremental transfer 	[RFC1995] 
            return "IXFR";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_AXFR)
        {
            //  252
            //   transfer of an entire zone 	[RFC1035][RFC5936] 
            return "AXFR";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_MAILB)
        {
            //  253
            //   mailbox-related RRs (MB, MG or MR) 	[RFC1035]
            return "MAILB";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_MAILA)
        {
            //  254
            //   mail agent RRs (OBSOLETE - see MX) 	[RFC1035]
            return "MAILA";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_ANY)
        {
            //  255
            ///   A request for some or all records the server has available 	[RFC1035][RFC6895][RFC8482]
            return "ANY";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_URI)
        {
            // 256
            // URI 	[RFC7553]
            return "ANY";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_CAA)
        {
            // 257
            // Certification Authority Restriction 	[RFC865
            return "CAA";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_AVC)
        {
            // 258
            //   Application Visibility and Control 	[Wolfgang_Riedel]
            return "AVC";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_DOA)
        {
            // 259
            // Digital Object Architecture 	[draft-durand-doa-over-dns-02]
            return "DOA";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_AMTRELAY)
        {
            // 260
            // Automatic Multicast Tunneling Relay 	[RFC8777]
            return "AMTRELAY";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_RESINFO)
        {
            // 261
            // Resolver Information as Key/Value Pairs
            return "RESINFO";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_WALLET)
        {
            // 262
            //   Public wallet address 	[Paul_Hoffman]
            return "WALLET";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_CLA)
        {
            // 263
            //   BP Convergence Layer Adapter 	[draft-johnson-dns-ipn-cla-07]
            return "CLA";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_IPN)
        {
            // 264
            //   BP Node Number 	[draft-johnson-dns-ipn-cla-07]
            return "IPN";
        }
        else if ( rrType >= DNSProtocolConstants.DNS_RR_TYPE_UNASSIGNED_265 && 
                   rrType <=  DNSProtocolConstants.DNS_RR_TYPE_UNASSIGNED_32767 )
        {
            // 265 - 32767
            //   Unassigned
            return "UNASSIGNED(265-32767)";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_TA)
        {
            // 32768
            //   DNSSEC Trust Authorities 	[Sam_Weiler][ Deploying DNSSEC Without a Signed Root. Technical Report 1999-19, Information Networking Institute, Carnegie Mellon University, April 2004.] 	
            return "TA";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_DLV)
        {
            // 32769
            //   DNSSEC Lookaside Validation (OBSOLETE) 	[RFC8749][RFC4431] 
            return "DLV";
        }
        else if ( rrType >= DNSProtocolConstants.DNS_RR_TYPE_UNASSIGNED_32770 && 
                   rrType <=  DNSProtocolConstants.DNS_RR_TYPE_UNASSIGNED_65279 )
        {
            // 32770 - 65279
            //   Unassigned
            return "UNASSIGNED(32770-65279)";
        }
        else if ( rrType >= DNSProtocolConstants.DNS_RR_TYPE_PRIVATE_USE_65280 && 
                   rrType <=  DNSProtocolConstants.DNS_RR_TYPE_PRIVATE_USE_65534 )
        {
            // 65280 - 65534
            //   Private use 	65280-65534 
            return "PRIVATE_USE(65280-65534)";
        }
        else if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_RESERVED_65535)
        {
            // 65535
            //  Reserved
            return "RESERVED";
        }


        // --- ここまで到達しているということは当てはまるリソースレコード番号が存在しないケースなので例外をスローする.
        String msg = String.format("Failed to parse DNS RR Type Nubmer, caused by UNNKOWN RR type number : %d", rrType);
        DNSServiceCommonException exception = new DNSServiceCommonException(msg);
        throw exception;

    }


    // DNSのリソースレコード(RR)のタイプ値に応じてrdataのバイナリデータを文字列形式に変換する.
    // 例) Aレコードの場合はrdataはIPv4アドレスの文字列に変換する.
    public String toDNSRRDataString(int rrType, byte[] rdata) throws DNSServiceCommonException
    {
        String text;
        if ( rrType == DNSProtocolConstants.DNS_RR_TYPE_A)
        {
            //  a host address 	[RFC1035]
            text = rdataToIPv4Address(rdata).toString();
        }
        else
        {
            // TODO : 未実装のリソースタイプの文字列化.
            text = String.format("Unimplemented conversion rdata to text function. rrType(%d) : %s, rdata.length=%d", rrType, toDNSRRTypeString(rrType), rdata.length );
        }

        return text;
    }


    // DNSのリソースレコードで使用されるバイナリ形式のrdataをIPv4アドレスに変換する.
    public InetAddress rdataToIPv4Address(byte[] rdata) throws DNSServiceCommonException
    {
        InetAddress address;
        try
        {
            address = InetAddress.getByAddress(rdata);
        }
        catch(UnknownHostException cause)
        {
            String msg = String.format("Failed to convert DNS ResourceData(rdata) to  IPAddress.");
            DNSServiceCommonException exception = new DNSServiceCommonException(msg, cause);
            throw exception;
        }

        return address;
    }

    // InetAddress形式のIPアドレスを文字列に変換する.
    public String inetAddressToIPAddressString(InetAddress inetAddress) throws DNSServiceCommonException
    {
        // InetAddress.getHostAddress()関数の復帰値は"/0.0.0.0"のように先頭に"/"記号が含まれていることがあるので取り除く.
        String ipString = inetAddress.getHostAddress();
        ipString = ipString.replace("\\/", "");

        return ipString;
    }


    // 指定したInetAddressがIPv6かを判定する.
    public boolean isIPv4Address(InetAddress inetAddress) throws DNSServiceCommonException
    {
        if ( inetAddress == null )
        {
            String msg = String.format("Failed to check InetAddress. Specified InetAddress object is null.");
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        if ( inetAddress.getAddress().length != 4 )
        {
            return false;
        }

        return true;
    }


    // 指定したInetAddressがIPv6かを判定する.
    public boolean isIPv6Address(InetAddress inetAddress) throws DNSServiceCommonException
    {
        if ( isIPv4Address(inetAddress) == true )
        {
            // 指定されたIPアドレスはIPv4.
            return false;
        }

        // 上記(IPv4以外)なのでIPv6とみなす.
        return true;
    }


    // EDNS Client Subnet(ECS)のアドレス部とプリフィックス部の組み合わせが妥当かを判定する.
    // アドレス部がIPv4の場合、プリフィックス部の取りうる範囲は0-32となる. 同IPv6なら0-128となる.
    public boolean isValidPairOfEDNSClientSubnetAddressAndPrefix(InetAddress inetAddress, int prefix) throws DNSServiceCommonException
    {
        if ( isIPv4Address(inetAddress) )
        {
            // IPv4の場合
            if ( prefix >= 0 && prefix <= 32)
            {
                return true;
            }

            return false;
        }
        else if ( isIPv6Address(inetAddress) )
        {
            // IPv6の場合
            if ( prefix >= 0 && prefix <= 128)
            {
                return true;
            }

            return false;
        }
        else
        {
            // ここに来ることはない.
            String msg = String.format("Failed to check EDNS Client Subnet (ECS) address and prefix pair value. address=%s, prefix=%d", inetAddress, prefix );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }
    }


    // EDNS(RFC2671)の拡張RCodeをRRのTTL値から取得する.
    // EDNS0で拡張されたEXTENDED-RCODE
    // RFC6891 pp.8より、DNSリソースレコードのTTL値(32bit)の上位8bitを使用してEXTENDED-RCODEを設定する.
    public int getEDNS0ExtendedRCodeFromTTL(int ttl) throws DNSServiceCommonException
    {
        int exRCode = ( (0xff000000 & ttl) >> 24 );

        if ( exRCode < 0 || exRCode > 255 )
        {
            String msg = String.format("Invalid EDNS0 EXTENDED-RCODE Value : %d", exRCode );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }
        
        return exRCode;
    }

    // EDNS(RFC6891)のEDNS0で拡張されたEDNSのVERSIONをRRのTTL値から取得する.
    // EDNS0で拡張されたEDNSのVERSION
    // RFC6891 pp.8より、DNSリソースレコードのTTL値(32bit)の先頭から2byte目(8bit)を使用してVERSIONを設定する.
    public int getEDNS0VersionFromTTL(int ttl) throws DNSServiceCommonException
    {

        int ednsVersion = ( (0x00ff0000 & ttl) >> 16 );

        if ( ednsVersion < 0 || ednsVersion > 255 )
        {
            String msg = String.format("Invalid EDNS0 VERSION Value : %d", ednsVersion );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        
        
        return ednsVersion;
    }


    // EDNS(RFC2671)の拡張DOフラグをRRのTTL値から取得する.
    // EDNS0で拡張されたEDNSのDO(DNSSEC OK bit)
    // RFC6891 pp.8より、DNSリソースレコードのTTL値(32bit)の先頭から17bit目(1bit)を使用してDOを設定する.
    public int getEDNS0DOFromTTL(int ttl) throws DNSServiceCommonException
    {
        int doFlg = ( (0x0000ff00 & ttl) >> 15 ) & 0b00000001;

        if ( doFlg < 0 || doFlg > 1 )
        {
            String msg = String.format("Invalid EDNS0 DO Flag (DNSSEC OK) Value : %d", doFlg );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }
        
        return doFlg;
    }

    // EDNS0で拡張されたEDNSのZ(将来拡張に備えて予約)
    // RFC6891 pp.8より、DNSリソースレコードのTTL値(32bit)の先頭から18bit目以降(15bit)がZとして予約されている.
    // 通常は0で埋められており、受け取った側も無視するように実装される.
    public int getEDNS0ReservedZFromTTL(int ttl) throws DNSServiceCommonException
    {

        int z = ( (0x0000ffff & ttl) >> 1 );

        if ( z < 0 || z > 128 )
        {
            String msg = String.format("Invalid EDNS0 Z (Reserved) Value : %d", z );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }
        
        return z;
    }


    // EDNS0(RFC6891)の各種値をRRのTTL値(32bit)に落とし込んで4byteのbyte型配列として返す.
    public byte[] getTTLBytesFromEDNS0Parameters(int exRCode, int ednsVersion, int doFlag, int reservedZ) throws DNSServiceCommonException
    {
        byte[] exRCodeBytes = int8ToBytes(exRCode);
        byte[] ednsVersionBytes = int8ToBytes(ednsVersion);

        int doAndZValue = ( ( 0b1 & doFlag ) << 15 ) | (0xffff & reservedZ );
        byte[] reservedZBytes = int16ToBytes(doAndZValue);

        byte[] ttlBytes = new byte[]{ exRCodeBytes[0], ednsVersionBytes[0], reservedZBytes[0], reservedZBytes[1] };
        return ttlBytes;
    }

    // EDNS0(RFC6891)の各種値をRRのTTL値(32bit)に落とし込んでint型として返す.
    public int getTTLFromEDNS0Parameters(int exRCode, int ednsVersion, int doFlag, int reservedZ) throws DNSServiceCommonException
    {
        byte[] ttlBytes = getTTLBytesFromEDNS0Parameters(exRCode, ednsVersion, doFlag, reservedZ);
        long ttlI64 = bytesToUInt32(ttlBytes);

        // 64bit値を32bit値に変換する.
        // TODO : 値が大きいと2の補数表現になるかもしれないので注意.
        int ttlI32 = (int)ttlI64;

        return ttlI32;
    }


    // EDNS0(RFC6891)の疑似RRセット(pseudo-RRのセット)をOPT-RRのrdataに格納するためのbyte配列に変換する.
    public byte[] getRDataBytesFromPseudoOptRR(List<IDNSRROptPseudoRRData> pseudoRRSetList) throws DNSServiceCommonException
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        for( IDNSRROptPseudoRRData pseudoRR : pseudoRRSetList )
        {
            byte[] bytes = pseudoRR.toBytes();
            os.writeBytes(bytes);
        }

        byte[] rdata = os.toByteArray();
        return rdata;
    }


    /**
     * DNSメッセージのbyte配列を解析してIDNSMessage型に変換する.
     * なお、処理の中でSpringのDIは使用しないようにする.
     * 
     * @param dnsMessageBytes
     * @return
     * @throws DNSServiceCommonException
     */
    public IDNSMessage parseDNSMessageBytes(byte[] dnsMessageBytes) throws DNSServiceCommonException
    {

		IDNSHeaderSection dnsHeader = new DNSHeaderSectionImpl();
		dnsHeader.setDNSHeaderBytes(dnsMessageBytes);
		int qdCount = (int)dnsHeader.getQDCOUNT();
        int anCount = (int)dnsHeader.getANCOUNT();


        // SimpleDNSProtocolModelInstanceFactoryImplをファクトリクラスとして使用すれば、SpringのDIを使用しない.
        IDNSProtocolModelInstanceFactory dnsProtocolModelInstanceFactory 
                                = getSimpleDNSProtocolModelInstanceFactory();

        IDNSMessage dnsMessage;
        if ( anCount > 0 )
        {
            dnsMessage = new DNSResponseMessageImpl(dnsProtocolModelInstanceFactory);
        }
        else
        {
            dnsMessage =new DNSQuestionMessageImpl(dnsProtocolModelInstanceFactory);
        }

        dnsMessage.setDNSMessageBytes( dnsMessageBytes );
        return dnsMessage;
    }


    /**
     * 汎用DNSメッセージクラス(IDNSMessage)をDNS問い合わせメッセージクラス(IDNSQuestionMessage)に変換する.
     * 
     * @param dnsMessage
     * @return
     * @throws DNSServiceCommonException
     */
    public IDNSQuestionMessage toDNSQuestionMessage(IDNSMessage dnsMessage) throws DNSServiceCommonException
    {
        if( isDNSQuestionMessage(dnsMessage) == false )
        {
            // 指定されたDNSメッセージは問い合わせメッセージの要件を満たしていない.
        }


        // SimpleDNSProtocolModelInstanceFactoryImplをファクトリクラスとして使用すれば、SpringのDIを使用しない.
        IDNSProtocolModelInstanceFactory dnsProtocolModelInstanceFactory 
                                = getSimpleDNSProtocolModelInstanceFactory();

            
        // 一旦、DNS電文をbyte配列にしてから再度問い合わせメッセージに変換する.
        IDNSQuestionMessage dnsQuestionMessage = new DNSQuestionMessageImpl(dnsProtocolModelInstanceFactory);
        dnsQuestionMessage.setDNSMessageBytes( dnsMessage.getDNSMessageBytes() );

        return dnsQuestionMessage;

    }

    /**
     * 汎用DNSメッセージクラス(IDNSMessage)がDNS問い合わせメッセージの要件を充足しているかを判定する.
     * 
     * @param dnsMessage
     * @return
     * @throws DNSServiceCommonException
     */
    public boolean isDNSQuestionMessage(IDNSMessage dnsMessage) throws DNSServiceCommonException
    {
        IDNSHeaderSection dnsHeader = dnsMessage.getDNSHeaderSection();
        if ( dnsHeader.getBooleanQR() != false )
        {
            // QR （Query Response）が0(リクエスト以外)なのでfalseで復帰する.
            return false;
        }

        if ( dnsHeader.getQDCOUNT() == 0 )
        {
            // QDカウントが0なのでfalseで復帰する.
            return false;
        }

        if( dnsHeader.getANCOUNT() != 0 )
        {
            // ANカウントが0以外なのでfalseで復帰する.
            return false;
        }

        if ( dnsMessage.getDNSAnswerSection() != null )
        {
            // Answerセクションが存在するためfalseで復帰する.
            return false;
        }

        if ( dnsMessage.getDNSQuestionSection() == null )
        {
            // Questionセクションがないのでfalseで復帰する.
            return false;
        }

        return true;
    }


    /**
     * IDNSProtocolModelInstanceFactoryをSpringのDIを用いずにシンプルに取得する.
     * 
     * @return
     * @throws DNSServiceCommonException
     */
    public IDNSProtocolModelInstanceFactory getSimpleDNSProtocolModelInstanceFactory() throws DNSServiceCommonException
    {
        // SimpleDNSProtocolModelInstanceFactoryImplをファクトリクラスとして使用すれば、SpringのDIを使用しない.
        IDNSProtocolModelInstanceFactory dnsProtocolModelInstanceFactory 
                                = new SimpleDNSProtocolModelInstanceFactoryImpl();

        return dnsProtocolModelInstanceFactory;
    }


}