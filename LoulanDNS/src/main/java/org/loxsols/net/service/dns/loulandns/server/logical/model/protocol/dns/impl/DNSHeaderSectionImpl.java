package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl;


import java.util.*;
import java.io.ByteArrayOutputStream;
import java.time.ZonedDateTime;

import java.nio.ByteBuffer;

import org.loxsols.net.service.dns.loulandns.client.common.DNSClientCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.constants.DNSProtocolConstants;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSDebugUtils;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;


import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;

import android.util.DebugUtils;


// DNSヘッダーの実装クラス.
public class DNSHeaderSectionImpl implements IDNSHeaderSection
{

    long id;
    long qr;
    long opcode;
    long aa;
    long tc;
    long rd;
    long ra;
    long z;
    long ad;
    long cd;
    long rcode;

    long qdcount;
    long ancount;
    long nscount;
    long arcount;





    // ID (16bit)
    // リクエストに割り当てられるランダムなID。
    // レスポンスはリクエストと同じIDで応答しなければならない。
    public int getID()  throws DNSServiceCommonException
    {
        return (int)id;
    }

    public void setID(int id)  throws DNSServiceCommonException
    {
        this.id = id;
    }


    // QR : Query Response (1bit)
    // リクエストは０、レスポンスは１
    public int getQR()  throws DNSServiceCommonException
    {
        return (int)qr;
    }

    public void setQR(int qr)  throws DNSServiceCommonException
    {
        this.qr = qr;
    }


    // OPCODE : Operation Code (4bit)
    // 問い合わせの種類を表す.
    // 0が通常のクエリ、4がNotify、5がUpdate.
    public long getOPCode()  throws DNSServiceCommonException
    {
        return opcode;
    }
    public void setOPCode(int opcode)  throws DNSServiceCommonException
    {
        this.opcode = opcode;
    }


    // AA : Authoritative Answer (1bit)
    // 応答するサーバーが権威サーバー（自分自身の所持するDNSレコードを返している）かどうかを表すフラグ。
    public long getAA()  throws DNSServiceCommonException
    {
        return aa;
    }

    public void setAA(int aa)  throws DNSServiceCommonException
    {
        this.aa = aa;
    }


    // TC  : Truncated Message (1bit)
    // パケットサイズが512バイトを超えるなら１.
    // 従来は長さ制限がないTCPでの再問い合わせをするかどうかのヒントとして使用された.
    public long getTC()  throws DNSServiceCommonException
    {
        return tc;
    }

    public void setTC(int tc)  throws DNSServiceCommonException
    {
        this.tc = tc;
    }


    // RD  : Recursion Desired (1bit)
    // リクエストを受けたサーバーが該当するレコードを所持していない場合、再帰的にな名前解決をすべきかリクエストの送信側が指定するためのフラグ。
    public long getRD()  throws DNSServiceCommonException
    {
        return rd;
    }

    public void setRD(int rd)  throws DNSServiceCommonException
    {
        this.rd = rd;
    }


    // RA  : Recursion Available (1bit)
    // サーバーが再帰的な名前解決が可能かを提示するフラグ
    public long getRA()  throws DNSServiceCommonException
    {
        return ra;
    }

    public void setRA(int ra)  throws DNSServiceCommonException
    {
        this.ra = ra;
    }

    // Z  : Reserved (1bit)
    // 将来的な拡張のために利用される領域.
    public long getZ()  throws DNSServiceCommonException
    {
        return z;
    }

    public void setZ(int z)  throws DNSServiceCommonException
    {
        this.z = z;
    }

    
    // AD  : Authentic Data (1bit)
    // DNSSEC検証に成功したことを表すフラグ
    public long getAD()  throws DNSServiceCommonException
    {
        return ad;
    }
    
    public void setAD(int ad)  throws DNSServiceCommonException
    {
        this.ad = ad;
    }
    

    // CD : Checking Disabled (1bit)
    // DNSSEC検証の禁止を指定するフラグ
    public long getCD()  throws DNSServiceCommonException
    {
        return cd;
    }

    public void setCD(int cd)  throws DNSServiceCommonException
    {
        this.cd = cd;
    }


    // RCode : Response Code (4bit)
    // サーバーがレスポンスの状態（成功、失敗など）をクライアントに提示するために使用されるコード
    public long getRCode()  throws DNSServiceCommonException
    {
        return rcode;
    }

    public void setRCode(int rcode)  throws DNSServiceCommonException
    {
        this.rcode = rcode;
    }
    

    // QDCOUNT  : Question Count (16bit)
    // Questionセクションに含まれるエントリの数
    public long getQDCOUNT()  throws DNSServiceCommonException
    {
        return qdcount;
    }

    public void setQDCOUNT(int qdcount)  throws DNSServiceCommonException
    {
        this.qdcount = qdcount;
    }

    // ANCOUNT : Answer Count (16bit)
    // Answerセクションに含まれるエントリの数
    public long getANCOUNT()  throws DNSServiceCommonException
    {
        return ancount;
    }

    public void setANCOUNT(int ancount)  throws DNSServiceCommonException
    {
        this.ancount = ancount;
    }

    // NSCOUNT  : Authority Count (16bit)
    // Authorityセクションに含まれるエントリの数
    public long getNSCOUNT()  throws DNSServiceCommonException
    {
        return nscount;
    }

    public void setNSCOUNT(int nscount)  throws DNSServiceCommonException
    {
        this.nscount = nscount;
    }

    
    // ARCOUNT   : Additional Count (16bit)
    // Additionalセクションに含まれるエントリの数
    public long getARCOUNT()  throws DNSServiceCommonException
    {
       return this.arcount;
    }

    public void setARCOUNT(int arcount)  throws DNSServiceCommonException
    {
        this.arcount = arcount;
    }
    

    // DNSヘッダのbyte配列を返す. (12byte)
    public byte[] getDNSHeaderBytes() throws DNSServiceCommonException
    {
        // DNSヘッダーは96bit(12byte)で構成される.
        ByteBuffer buffer = ByteBuffer.allocate(12);

        // ID(16bit)
        short id = (short)getID();
        buffer.putShort( (short)id );
        
        // QR(1bit) | Opcode(4bit) | AA(1bit) | TC(1bit) | RD(1bit) | RA(1bit) | Z(1bit) | AD(1bit) | CD(1bit) | RCODE(4bit) |
        long flags = ( getQR() ) | ( getOPCode() << 1 ) | ( getAA() << 5 ) | ( getTC() << 6 ) | ( getRD() << 7 ) | ( getRA() << 8 ) | ( getZ() << 9 ) | ( getAD() << 10 ) | ( getCD() << 11 ) | ( getRCode() << 12 );
        buffer.putShort( (short)flags);

        long qdcount = getQDCOUNT();
        buffer.putShort( (short)qdcount);

        long ancount = getANCOUNT();
        buffer.putShort( (short)ancount);

        long nscount = getNSCOUNT();
        buffer.putShort( (short)nscount);

        long arcount = getARCOUNT();
        buffer.putShort( (short)arcount);


        byte[] bytes = buffer.array();
        return bytes;

    }

    // DNSヘッダのbyte配列(12byte)を元に本クラスの各値を設定する.
    public void setDNSHeaderBytes(byte[] headerBytes) throws DNSServiceCommonException
    {
        if ( headerBytes.length < 12 )
        {
            String msg = String.format("Failed to parse DNS Header. Byte array length is less than 12 bytes. length=%d",  headerBytes.length );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        // ByteBufferクラスを使用するとgetShort()などで数値型を取り出す際に負数フラグがONの値を正常に取得できない.
        // ByteBufferクラスはDNSメッセージの解析には不適なので使用しないこと.
        //// ByteBuffer buffer = ByteBuffer.wrap(headerBytes);
        

        LoulanDNSDebugUtils.printHexString(getClass(), "setDNSHeaderBytes() headerBytes=", headerBytes);

        // ID 16bit
        //// 先に8bit値を0xffとAND演算しておかないとint型の負数に型変換されてしまうため注意.
        int id = ( 0xff & headerBytes[0]) << 8;
        id = ( id  | ( 0xff & headerBytes[1]) ) & 0x0000ffff;

        setID(id);

        // flags 16bit
        // 0        | 1            | 5        | 6        | 7        | 8        | 9       | 10       | 11       | 12          |   
        // QR(1bit) | Opcode(4bit) | AA(1bit) | TC(1bit) | RD(1bit) | RA(1bit) | Z(1bit) | AD(1bit) | CD(1bit) | RCODE(4bit) |
        
        int flags = 0x00ff & headerBytes[2];
        flags = ( flags << 8 ) | ( 0x00ff & headerBytes[3] );
        

        
        int qr         = ( flags & 0b0000000000000001 );
        int opecode    = ( flags & 0b0000000000011110 ) >> 1;
        int aa         = ( flags & 0b0000000000100000 ) >> 5;
        int tc         = ( flags & 0b0000000001000000 ) >> 6;
        int rd         = ( flags & 0b0000000010000000 ) >> 7;
        int ra         = ( flags & 0b0000000100000000 ) >> 8;
        int z          = ( flags & 0b0000001000000000 ) >> 9;
        int ad         = ( flags & 0b0000010000000000 ) >> 10;
        int cd         = ( flags & 0b0000100000000000 ) >> 11;
        int rcode      = ( flags & 0b1111000000000000 ) >> 12;

        // LoulanDNSDebugUtils.printHexString(getClass(), "setDNSHeaderBytes() : headerBytes", headerBytes);
        // LoulanDNSDebugUtils.printDebug(this.getClass(), "setDNSHeaderBytes() : flags(0b)", Integer.toBinaryString(flags) );
        // LoulanDNSDebugUtils.printDebug(this.getClass(), "setDNSHeaderBytes() : rcode(0b)", Integer.toBinaryString(rcode) );


        setQR( (int)qr);
        setOPCode( (int)opecode);
        setAA( (int)aa);
        setTC( (int)tc);
        setRD( (int)rd);
        setRA( (int)ra);
        setZ( (int)z);
        setAD( (int)ad);
        setCD( (int)cd);
        setRCode( (int)rcode);


        // QDCOUNT 16bit
        int qdcount = ( ( headerBytes[4] << 8 ) | headerBytes[5] ) & 0x0000ffff;
        setQDCOUNT(qdcount);

        // ANCOUNT 16bit
        int ancount = ( ( headerBytes[6] << 8 ) | headerBytes[7] ) & 0x0000ffff;
        setANCOUNT(ancount);

        // NSCOUNT 16bit
        int nscount = ( ( headerBytes[8] << 8 ) | headerBytes[9] ) & 0x0000ffff;
        setNSCOUNT(nscount);

        // ARCOUNT 16bit
        int arcount = ( ( headerBytes[10] << 8 ) | headerBytes[11] ) & 0x0000ffff;
        setARCOUNT(arcount);

    }


    public boolean getBooleanQR()  throws DNSServiceCommonException
    {
        boolean ret = int2boolean( (int)getQR() );
        return ret;
    }

    public boolean getBooleanAA()  throws DNSServiceCommonException
    {
        boolean ret = int2boolean( (int)getAA() );
        return ret;
    }

    public boolean getBooleanTC()  throws DNSServiceCommonException
    {
        boolean ret = int2boolean( (int)getTC() );
        return ret;
    }

    public boolean getBooleanRD()  throws DNSServiceCommonException
    {
        boolean ret = int2boolean( (int)getRD() );
        return ret;
    }

    public boolean getBooleanRA()  throws DNSServiceCommonException
    {
        boolean ret = int2boolean( (int)getRA() );
        return ret;
    }

    public boolean getBooleanZ()  throws DNSServiceCommonException
    {
        boolean ret = int2boolean( (int)getZ() );
        return ret;
    }

    public boolean getBooleanAD()  throws DNSServiceCommonException
    {
        boolean ret = int2boolean( (int)getAD() );
        return ret;
    }


    public boolean getBooleanCD()  throws DNSServiceCommonException
    {
        boolean ret = int2boolean( (int)getCD() );
        return ret;
    }



    // DNSヘッダセクションの初期化を実行する.
    // id                   : ID (16bit)                            リクエストに割り当てられるランダムなID.
    // queryResponse        : QR : Query Response (1bit)            リクエストは０、レスポンスは１
    // opCode               : OPCODE : Operation Code (4bit)        問い合わせの種類を表す. 0が通常のクエリ、4がNotify、5がUpdate.
    // authoritativeAnswer  : AA : Authoritative Answer (1bit)      応答するサーバーが権威サーバー（自分自身の所持するDNSレコードを返している）かどうかを表すフラグ。
    // truncatedMessage     : TC  : Truncated Message (1bit)        パケットサイズが512バイトを超えるなら１.
    // recursionDesired     : RD  : Recursion Desired (1bit)        リクエストを受けたサーバーが該当するレコードを所持していない場合、再帰的にな名前解決をすべきかリクエストの送信側が指定するためのフラグ。
    // recursionAvailable   : RA  : Recursion Available (1bit)      サーバーが再帰的な名前解決が可能かを提示するフラグ
    // reservedZ            : Z  : Reserved (1bit)                  将来的な拡張のために利用される領域.
    // authenticData        : AD  : Authentic Data (1bit)           DNSSEC検証に成功したことを表すフラグ
    // checkingDisabled     : CD : Checking Disabled (1bit)         DNSSEC検証の禁止を指定するフラグ
    // rCode                : RCode : Response Code (4bit)          サーバーがレスポンスの状態（成功、失敗など）をクライアントに提示するために使用されるコード
    // qdCount              : QDCOUNT  : Question Count (16bit)     Questionセクションに含まれるエントリの数
    // ANCOUNT : Answer Count (16bit) : Answerセクションに含まれるエントリの数
    // nsCount              : NSCOUNT  : Authority Count (16bit)    Authorityセクションに含まれるエントリの数
    // arCount              : ARCOUNT   : Additional Count (16bit)  Additionalセクションに含まれるエントリの数
    public void init(int id, boolean queryResponse, int opCode, boolean authoritativeAnswer, boolean truncatedMessage, boolean recursionDesired, boolean recursionAvailable, boolean reservedZ, boolean authenticData, boolean checkingDisabled, int rCode,  int qdCount, int anCount, int nsCount, int arCount)  throws DNSServiceCommonException
    {
        setID(id);
        setQR( queryResponse ? 1 : 0);
        setOPCode(opCode);
        setAA( authoritativeAnswer ? 1 : 0 );
        setTC( truncatedMessage ? 1 : 0 );
        setRD( recursionDesired ? 1 : 0 );
        setRA( recursionAvailable ? 1 : 0 );
        setZ( reservedZ ? 1 : 0 );
        setAD( authenticData ? 1 : 0 );
        setCD( checkingDisabled ? 1 : 0 );
        setRCode(rCode);
        setQDCOUNT(qdCount);
        setANCOUNT(anCount);
        setNSCOUNT(nsCount);
        setARCOUNT(arCount);
    }

    // DNSヘッダセクションの初期化を実行する.
    // id                   : ID (16bit)                            リクエストに割り当てられるランダムなID.
    // flags                : FLAGS (16bit)                         QRやOCPDEなどを格納する16bitの数値データ(FLAGSという名称はDNS標準仕様ではない)
    // qdCount              : QDCOUNT  : Question Count (16bit)     Questionセクションに含まれるエントリの数
    // nsCount              : NSCOUNT  : Authority Count (16bit)    Authorityセクションに含まれるエントリの数
    // arCount              : ARCOUNT   : Additional Count (16bit)  Additionalセクションに含まれるエントリの数
    public void init(int id, int flags, int qdCount, int anCount, int nsCount, int arCount)  throws DNSServiceCommonException
    {
        setID(id);
        setFLAGSValue(flags);
        setQDCOUNT(qdCount);
        setANCOUNT(anCount);
        setNSCOUNT(nsCount);
        setARCOUNT(arCount);
    }



    //  QRやOCPDEなどを格納する16bitの数値データ 
    private void setFLAGSValue(int flags) throws DNSServiceCommonException
    {
        int qr         = ( flags & 0b0000000000000001 );
        int opecode    = ( flags & 0b0000000000011110 ) >> 1;
        int aa         = ( flags & 0b0000000000100000 ) >> 5;
        int tc         = ( flags & 0b0000000001000000 ) >> 6;
        int rd         = ( flags & 0b0000000010000000 ) >> 7;
        int ra         = ( flags & 0b0000000100000000 ) >> 8;
        int z          = ( flags & 0b0000001000000000 ) >> 9;
        int ad         = ( flags & 0b0000010000000000 ) >> 10;
        int cd         = ( flags & 0b0000100000000000 ) >> 11;
        int rcode      = ( flags & 0b1111000000000000 ) >> 12;

        setQR( (int)qr);
        setOPCode( (int)opecode);
        setAA( (int)aa);
        setTC( (int)tc);
        setRD( (int)rd);
        setRA( (int)ra);
        setZ( (int)z);
        setAD( (int)ad);
        setCD( (int)cd);
        setRCode( (int)rcode);
    }


    private boolean int2boolean(int value)
    {
        if (value == 0)
        {
            return false;
        }
        return true;
    }

    // OPCodeの文字列表現を返す.
    public String getOPCodeString() throws DNSServiceCommonException
    {
        String ret;
        int opcode = (int)getOPCode();

        if ( opcode == DNSProtocolConstants.DNS_OPCODE_QUERY )
        {
            ret = "QUERY";
        }
        else if ( opcode == DNSProtocolConstants.DNS_OPCODE_IQUERY)
        {
            ret = "IQUERY";
        }
        else if ( opcode == DNSProtocolConstants.DNS_OPCODE_STATUS)
        {
            ret = "STATUS";
        }     
        else if ( opcode == DNSProtocolConstants.DNS_OPCODE_RESERVED_03)
        {
            ret = "RESERVED(3)";
        }
        else if ( opcode == DNSProtocolConstants.DNS_OPCODE_NOTIFY)
        {
            ret = "NOTIFY";
        }
        else if ( opcode == DNSProtocolConstants.DNS_OPCODE_UPDATE)
        {
            ret = "UPDATE";
        }
        else if ( opcode == DNSProtocolConstants.DNS_OPCODE_DSO)
        {
            ret = "DSO";
        }
        else if ( opcode >= DNSProtocolConstants.DNS_OPCODE_RESERVED_07 
                    && opcode <= DNSProtocolConstants.DNS_OPCODE_RESERVED_15 )
        {
            ret = String.format("RESERVED(%d)", opcode);
        }
        else
        {
            ret = String.format("UNKNOWN(%d)", opcode);
        }

        return ret;
    }

    // RCodeの文字列表現を返す.
    public String getRCodeString() throws DNSServiceCommonException
    {
        String ret;
        int rcode = (int)getRCode();

        if ( rcode == DNSProtocolConstants.DNS_RCODE_NOERROR )
        {
            ret = "NOERROR";
        }
        else if ( rcode == DNSProtocolConstants.DNS_RCODE_FORMERR )
        {
            ret = "FORMERR";
        }
        else if ( rcode == DNSProtocolConstants.DNS_RCODE_SERVFAIL )
        {
            ret = "SERVFAIL";
        }
        else if ( rcode == DNSProtocolConstants.DNS_RCODE_NXDOMAIN )
        {
            ret = "NXDOMAIN";
        }
        else if ( rcode == DNSProtocolConstants.DNS_RCODE_NOTIMP )
        {
            ret = "NOTIMP";
        }
        else if ( rcode == DNSProtocolConstants.DNS_RCODE_REFUSED )
        {
            ret = "REFUSED";
        }
        else if ( rcode == DNSProtocolConstants.DNS_RCODE_YXDOMAIN )
        {
            ret = "YXDOMAIN";
        }
        else if ( rcode == DNSProtocolConstants.DNS_RCODE_YXRRSET )
        {
            ret = "YXRRSET";
        }
        else if ( rcode == DNSProtocolConstants.DNS_RCODE_NXRRSET )
        {
            ret = "NXRRSET";
        }
        else if ( rcode == DNSProtocolConstants.DNS_RCODE_NOTAUTH )
        {
            ret = "NOTAUTH";
        }
        else if ( rcode == DNSProtocolConstants.DNS_RCODE_DSOTYPENI )
        {
            ret = "DSOTYPENI";
        }
        else if ( rcode >= DNSProtocolConstants.DNS_RCODE_RESERVED_12 && rcode <= DNSProtocolConstants.DNS_RCODE_RESERVED_15)
        {
            // 12-15は未割り当て (予約済み)
            ret = String.format("RESERVED(%d)", rcode );
        }
        else if ( rcode == DNSProtocolConstants.DNS_RCODE_BADKEY )
        {
            ret = "BADKEY";
        }
        else if ( rcode == DNSProtocolConstants.DNS_RCODE_BADKEY )
        {
            ret = "BADKEY";
        }
        else if ( rcode == DNSProtocolConstants.DNS_RCODE_BADMODE )
        {
            ret = "BADMODE";
        }
        else if ( rcode == DNSProtocolConstants.DNS_RCODE_BADNAME )
        {
            ret = "BADNAME";
        }
        else if ( rcode == DNSProtocolConstants.DNS_RCODE_BADTRUNC )
        {
            ret = "BADTRUNC";
        }
        else if ( rcode == DNSProtocolConstants.DNS_RCODE_BADCOOKIE )
        {
            ret = "BADCOOKIE";
        }
        else if ( rcode >= DNSProtocolConstants.DNS_RCODE_RESERVED_24 && rcode <= DNSProtocolConstants.DNS_RCODE_RESERVED_3840)
        {
            // 24-3840は未割り当て (予約済み)    
            ret = String.format("RESERVED(%d)", rcode );
        }
        else if ( rcode >= DNSProtocolConstants.DNS_RCODE_RESERVED_FOR_PRIVATE_USE_3841 && rcode <= DNSProtocolConstants.DNS_RCODE_RESERVED_FOR_PRIVATE_USE_4095)
        {
            // 3841-4095は未割り当て (予約済み)    
            ret = String.format("RESERVED_FOR_PRIVATE_USE(%d)", rcode );
        }
        else if ( rcode >= DNSProtocolConstants.DNS_RCODE_RESERVED_4096 && rcode <= DNSProtocolConstants.DNS_RCODE_RESERVED_65534)
        {
            // 4096-65534は未割り当て (予約済み)    
            ret = String.format("RESERVED(%d)", rcode );
        }
        else if ( rcode == DNSProtocolConstants.DNS_RCODE_RESERVED_65535 )
        {
            // 65535 : Reserved, can be allocated by Standards Action[RFC6895]    
            ret = String.format("RESERVED(%d)", rcode );
        }
        else
        {
            ret = String.format("UNNKOWN(%d)", rcode );
        }
        
        return ret;

    }


}