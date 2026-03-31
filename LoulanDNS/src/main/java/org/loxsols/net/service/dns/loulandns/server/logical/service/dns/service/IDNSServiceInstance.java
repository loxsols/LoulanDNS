
package org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service;


import java.util.*;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;

/**
 * DNSサービスインスタンスのインターフェース
 */
public interface IDNSServiceInstance
{

    /**
     * DNSサーバーのサービスを開始する.
     * @throws DNSServiceCommonException
     */
    public void runDNSService() throws DNSServiceCommonException;

    /**
     * DNSサーバーのサービスを停止する.
     * @throws DNSServiceCommonException
     */
    public void stopDNSService() throws DNSServiceCommonException;

    /**
     * DNSサーバーのパラメーターを設定する.
     * @param properties
     * @throws DNSServiceCommonException
     */
    public void initDNSService(Properties properties) throws DNSServiceCommonException;

    /**
     * DNS問い合わせメッセージを処理して、DNSレスポンスメッセージを返す.
     * @param dnsQuestionMessage
     * @return
     * @throws DNSServiceCommonException
     */
    public IDNSResponseMessage serveDNSQuery(IDNSQuestionMessage dnsQuestionMessage) throws DNSServiceCommonException;


    /**
     * DNSサービスのインスタンス名を返す.
     * @return
     * @throws DNSServiceCommonException
     */
    public String getDNSServiceInstanceName() throws DNSServiceCommonException;


    /**
     * このDNSサービスインスタンスが所属するユーザー名を返す.
     * 
     * @return
     * @throws DNSServiceCommonException
     */
    public String getDNSServiceUserName() throws DNSServiceCommonException;



}