
package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model;

import org.loxsols.net.service.dns.loulandns.server.common.*;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.*;

public interface IDNSProtocolModelInstanceFactory
{

    public IDNSMessage createDNSMesssageInstance()  throws DNSServiceCommonException;
    public IDNSQuestionMessage createDNSQuestionMessageInstance()  throws DNSServiceCommonException; 
    public IDNSResponseMessage createDNSResponseMessageInstance()  throws DNSServiceCommonException;
    

    public IDNSHeaderSection createDNSHeaderSectionInstance()  throws DNSServiceCommonException;
    public IDNSQuestionSection createDNSQuestionSectionInstance()  throws DNSServiceCommonException;
    public IDNSAnswerSection createDNSAnswerSectionInstance()  throws DNSServiceCommonException;
    public IDNSAuthoritySection createDNSAuthoritySectionInstance()  throws DNSServiceCommonException;
    public IDNSAdditionalSection createDNSAdditionalSectionInstance()  throws DNSServiceCommonException;

    public IDNSQueryPart createDNSQueryPart()  throws DNSServiceCommonException;


    // DNSクエリのヘッダセクションを生成する.
    // id : ランダムなID値(16bit)
    // tc : Truncated Message  : パケットサイズが512バイトを超えるならtrue
    // rd : Recursion Desired) : リクエストを受けたサーバーが該当するレコードを所持していない場合、再帰的な名前解決を依頼する場合はtrue
    // qd : QDCOUNT(16bit)     : Questionセクションに含まれるエントリの数
    public IDNSHeaderSection createDNSQueryHeaderSectionInstance(int id, boolean tc, boolean rd, int qd)  throws DNSServiceCommonException;

    // シンプルなDNS問い合わせセクションを生成する.
    // qname : FQDN名
    // qtype : Query Type
    // qclas : Query Class
    public IDNSQuestionSection createSimpleDNSQuestionSectionInstance(String qname, int qtype, int qclass) throws DNSServiceCommonException;


    

    // DNSクエリレコードを作成する.
    public IDNSQueryPart createDNSQueryRecord() throws DNSServiceCommonException;

    // 空のDNSリソースレコードを作成する.
    public IDNSResourceRecord createDNSResourceRecord() throws DNSServiceCommonException;

    // DNSリソースレコードを作成する.
    public IDNSResourceRecord createDNSResourceRecord(String rname, int rtype, int rclass, int rTTL, byte[] rdata) throws DNSServiceCommonException;




}