package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section;

import org.loxsols.net.service.dns.loulandns.server.common.*;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;

public interface IDNSHeaderSectionFactory
{

    // DNSクエリのヘッダセクションを生成する.
    // id : ランダムなID値(16bit)
    // tc : Truncated Message  : パケットサイズが512バイトを超えるならtrue
    // rd : Recursion Desired) : リクエストを受けたサーバーが該当するレコードを所持していない場合、再帰的な名前解決を依頼する場合はtrue
    // qd : QDCOUNT(16bit)     : Questionセクションに含まれるエントリの数
    public IDNSHeaderSection createDNSQueryHeaderSection(int id, boolean tc, boolean rd, int qd)  throws DNSServiceCommonException;



}