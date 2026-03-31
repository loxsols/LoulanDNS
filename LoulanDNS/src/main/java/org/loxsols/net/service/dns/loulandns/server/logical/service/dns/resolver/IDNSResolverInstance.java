
package org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver;


import java.util.*;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;


public interface IDNSResolverInstance
{


    // 初期化メソッド.
    public void init(Properties properties ) throws DNSServiceCommonException;

    // DNSクエリ(クエリ部)を処理してDNSレスポンスメッセージを返す.
    public IDNSResponseMessage resolve(IDNSQueryPart dnsQueryPart) throws DNSServiceCommonException;

    // DNS問い合わせセクション(複数のDNSクエリ部からなるセクション)を処理してDNSレスポンスメッセージを返す.
    public IDNSResponseMessage resolve(IDNSQuestionSection dnsQueetionSection) throws DNSServiceCommonException;

    // DNS問い合わせメッセージを処理してDNSレスポンスメッセージを返す.
    public IDNSResponseMessage resolve(IDNSQuestionMessage dnsQuestionMessage) throws DNSServiceCommonException;



}