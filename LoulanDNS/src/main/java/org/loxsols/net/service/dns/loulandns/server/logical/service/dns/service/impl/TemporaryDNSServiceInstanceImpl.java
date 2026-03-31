package org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.impl;


import java.util.*;

import lombok.Getter;
import lombok.Setter;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.factory.IDNSServiceInstanceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.LoulanDNSLogicalModelService;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.IDNSResolverInstance;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.loxsols.net.service.dns.loulandns.server.logical.model.*;

import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.factory.*;

import org.loxsols.net.service.dns.loulandns.server.common.constants.*;
import org.loxsols.net.service.dns.loulandns.server.common.util.*;

import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.impl.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.impl.base.DNSServiceInstanceImplBase;

/**
 * DNSサービスインスタンスのテンポラリ実装クラス.
 * オンメモリで処理が完結し、DBへの問い合わせを行わない.
 */
@Setter
@Getter
public class TemporaryDNSServiceInstanceImpl extends DNSServiceInstanceImplBase implements IDNSServiceInstance
{

    // -----------------------------------------------
    // 以下はインターフェース関数の実装
    // -----------------------------------------------

    /**
     * このDNSサービスインスタンスが所属するユーザー名を返す.
     * 
     * @return
     * @throws DNSServiceCommonException
     */
    public String getDNSServiceUserName() throws DNSServiceCommonException
    {
        // 本クラスでは、一時的な仮想ユーザー名(TemporaryUser_<ユーザーID>を返す.)
        String userName = String.format("TemporaryUser_%d", getDNSSrviceInstanceInfo().getUserID() );
        return userName;
    }


    /**
     * コンストラクタ.
     * 
     * @param info
     * @throws DNSServiceCommonException
     */
    public TemporaryDNSServiceInstanceImpl(DNSServiceInstanceInfo info, IDNSResolverInstance resolverInstance) throws DNSServiceCommonException
    {
        super(info, resolverInstance);
    }



}