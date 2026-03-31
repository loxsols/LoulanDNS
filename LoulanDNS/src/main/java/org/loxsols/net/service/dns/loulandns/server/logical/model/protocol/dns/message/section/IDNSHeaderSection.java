package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section;


import java.util.*;
import java.time.ZonedDateTime;

import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;


import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;


// DNSヘッダのインターフェース
public interface IDNSHeaderSection
{

    // ID (16bit)
    // リクエストに割り当てられるランダムなID。
    // レスポンスはリクエストと同じIDで応答しなければならない。
    public int getID()  throws DNSServiceCommonException;
    public void setID(int id)  throws DNSServiceCommonException;


    // QR : Query Response (1bit)
    // リクエストは０、レスポンスは１
    public int getQR()  throws DNSServiceCommonException;
    public void setQR(int qr)  throws DNSServiceCommonException;


    // OPCODE : Operation Code (4bit)
    // 問い合わせの種類を表す.
    // 0が通常のクエリ、4がNotify、5がUpdate.
    public long getOPCode()  throws DNSServiceCommonException;
    public void setOPCode(int opcode)  throws DNSServiceCommonException;


    // AA : Authoritative Answer (1bit)
    // 応答するサーバーが権威サーバー（自分自身の所持するDNSレコードを返している）かどうかを表すフラグ。
    public long getAA()  throws DNSServiceCommonException;
    public void setAA(int aa)  throws DNSServiceCommonException;


    // TC  : Truncated Message (1bit)
    // パケットサイズが512バイトを超えるなら１.
    // 従来は長さ制限がないTCPでの再問い合わせをするかどうかのヒントとして使用された.
    public long getTC()  throws DNSServiceCommonException;
    public void setTC(int tc)  throws DNSServiceCommonException;


    // RD  : Recursion Desired (1bit)
    // リクエストを受けたサーバーが該当するレコードを所持していない場合、再帰的にな名前解決をすべきかリクエストの送信側が指定するためのフラグ。
    public long getRD()  throws DNSServiceCommonException;
    public void setRD(int rd)  throws DNSServiceCommonException;


    // RA  : Recursion Available (1bit)
    // サーバーが再帰的な名前解決が可能かを提示するフラグ
    public long getRA()  throws DNSServiceCommonException;
    public void setRA(int ra)  throws DNSServiceCommonException;
    

    // Z  : Reserved (1bit)
    // 将来的な拡張のために利用される領域.
    public long getZ()  throws DNSServiceCommonException;
    public void setZ(int z)  throws DNSServiceCommonException;

    
    // AD  : Authentic Data (1bit)
    // DNSSEC検証に成功したことを表すフラグ
    public long getAD()  throws DNSServiceCommonException;
    public void setAD(int ad)  throws DNSServiceCommonException;
    

    // CD : Checking Disabled (1bit)
    // DNSSEC検証の禁止を指定するフラグ
    public long getCD()  throws DNSServiceCommonException;
    public void setCD(int cd)  throws DNSServiceCommonException;


    // RCode : Response Code (4bit)
    // サーバーがレスポンスの状態（成功、失敗など）をクライアントに提示するために使用されるコード
    public long getRCode()  throws DNSServiceCommonException;
    public void setRCode(int rcode)  throws DNSServiceCommonException;
    

    // QDCOUNT  : Question Count (16bit)
    // Questionセクションに含まれるエントリの数
    public long getQDCOUNT()  throws DNSServiceCommonException;
    public void setQDCOUNT(int qdcount)  throws DNSServiceCommonException;

    // ANCOUNT : Answer Count (16bit)
    // Answerセクションに含まれるエントリの数
    public long getANCOUNT()  throws DNSServiceCommonException;
    public void setANCOUNT(int ancount)  throws DNSServiceCommonException;

    // NSCOUNT  : Authority Count (16bit)
    // Authorityセクションに含まれるエントリの数
    public long getNSCOUNT()  throws DNSServiceCommonException;
    public void setNSCOUNT(int nscount)  throws DNSServiceCommonException;

    
    // ARCOUNT   : Additional Count (16bit)
    // Additionalセクションに含まれるエントリの数
    public long getARCOUNT()  throws DNSServiceCommonException;
    public void setARCOUNT(int arcount)  throws DNSServiceCommonException;
    

    // DNSヘッダのbyte配列を返す. (12byte)
    public byte[] getDNSHeaderBytes() throws DNSServiceCommonException;

    // DNSヘッダのbyte配列(12byte)を元に本クラスの各値を設定する.
    public void setDNSHeaderBytes(byte[] headerBytes) throws DNSServiceCommonException;

    public boolean getBooleanQR()  throws DNSServiceCommonException;
    public boolean getBooleanAA()  throws DNSServiceCommonException;
    public boolean getBooleanTC()  throws DNSServiceCommonException;
    public boolean getBooleanRD()  throws DNSServiceCommonException;
    public boolean getBooleanRA()  throws DNSServiceCommonException;
    public boolean getBooleanZ()  throws DNSServiceCommonException;
    public boolean getBooleanAD()  throws DNSServiceCommonException;
    public boolean getBooleanCD()  throws DNSServiceCommonException;

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
    // nsCount              : NSCOUNT  : Authority Count (16bit)    Authorityセクションに含まれるエントリの数
    // arCount              : ARCOUNT   : Additional Count (16bit)  Additionalセクションに含まれるエントリの数
    public void init(int id, boolean queryResponse, int opCode, boolean authoritativeAnswer, boolean truncatedMessage, boolean recursionDesired, boolean recursionAvailable, boolean reservedZ, boolean authenticData, boolean checkingDisabled, int rCode,  int qdCount, int anCount, int nsCount, int arCount)  throws DNSServiceCommonException;


    // DNSヘッダセクションの初期化を実行する.
    // id                   : ID (16bit)                            リクエストに割り当てられるランダムなID.
    // flags                : FLAGS (16bit)                         QRやOCPDEなどを格納する16bitの数値データ(FLAGSという名称はDNS標準仕様ではない)
    // qdCount              : QDCOUNT  : Question Count (16bit)     Questionセクションに含まれるエントリの数
    // nsCount              : NSCOUNT  : Authority Count (16bit)    Authorityセクションに含まれるエントリの数
    // arCount              : ARCOUNT   : Additional Count (16bit)  Additionalセクションに含まれるエントリの数
    public void init(int id, int flags, int qdCount, int anCount, int nsCount, int arCount)  throws DNSServiceCommonException;


    // OPCodeの文字列表現を返す.
    public String getOPCodeString() throws DNSServiceCommonException;

    // RCodeの文字列表現を返す.
    public String getRCodeString() throws DNSServiceCommonException;


}