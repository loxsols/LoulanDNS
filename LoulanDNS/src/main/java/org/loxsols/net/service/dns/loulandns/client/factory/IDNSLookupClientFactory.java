package org.loxsols.net.service.dns.loulandns.client.factory;

import org.loxsols.net.service.dns.loulandns.client.common.DNSClientCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSQuestionMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;
import org.loxsols.net.service.dns.loulandns.client.*;

import java.util.*;


/**
 * IDNSLookupClientインターフェースの具象化クラスを生成するファクトリクラス.
 * 
 */
public interface IDNSLookupClientFactory
{

     /**
      * IDNSLookupClientオブジェクトを生成する.
      * 生成するDNSクライアントのプロトコルはloulan.dns.protocol.typeプロパティキー(PROP_KEY_DNS_PROTOCOL_TYPE)で設定する.
      * 
      * @param properties
      * @return
      * @throws DNSClientCommonException
      */
    public IDNSLookupClient createDNSLookupClient(Properties properties) throws DNSClientCommonException;


}

