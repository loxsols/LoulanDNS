package org.loxsols.net.service.dns.loulandns.client.factory.impl;

import org.loxsols.net.service.dns.loulandns.client.common.DNSClientCommonException;
import org.loxsols.net.service.dns.loulandns.client.common.LoulanDNSClientConstants;
import org.loxsols.net.service.dns.loulandns.client.factory.IDNSLookupClientFactory;
import org.loxsols.net.service.dns.loulandns.client.impl.UDPResolverImpl;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSQuestionMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;
import org.loxsols.net.service.dns.loulandns.client.*;
import org.loxsols.net.service.dns.loulandns.client.factory.*;

import java.util.*;


/**
 * IDNSLookupClientインターフェースの具象化クラスを生成するファクトリクラス.
 * 
 */
public class DNSLookupClientFactoryImpl implements IDNSLookupClientFactory
{

     /**
      * IDNSLookupClientオブジェクトを生成する.
      * 生成するDNSクライアントのプロトコルはloulan.dns.protocol.typeプロパティキー(LoulanDNSClientConstants.PROP_KEY_DNS_PROTOCOL_TYPE)で設定する.
      * 
      * @param properties
      * @return
      * @throws DNSClientCommonException
      */
    public IDNSLookupClient createDNSLookupClient(Properties properties) throws DNSClientCommonException
    {
      IDNSLookupClient client = null;

      String dnsProtocolType = (String)properties.get( LoulanDNSClientConstants.PROP_KEY_DNS_PROTOCOL_TYPE );
      if ( dnsProtocolType == null )
      {
        String msg = String.format("Failed to create DNSLookupClient. DNS protocol type is not specified. key = %s", LoulanDNSClientConstants.PROP_KEY_DNS_PROTOCOL_TYPE );
        DNSClientCommonException exception = new DNSClientCommonException(msg);
        throw exception;
      }


      try
      {
        if ( dnsProtocolType.equals( LoulanDNSClientConstants.DNS_PROTOCOL_TYPE_UDP ) )
        {
          client = new UDPResolverImpl();

          client.init(properties);

        }
        else if ( dnsProtocolType.equals( LoulanDNSClientConstants.DNS_PROTOCOL_TYPE_TCP ) )
        {
          String msg = String.format("Failed to create DNSLookupClient. Specified DNS protocol type is not implemented. key=%s ,value=%s", LoulanDNSClientConstants.PROP_KEY_DNS_PROTOCOL_TYPE, dnsProtocolType );
          DNSClientCommonException exception = new DNSClientCommonException(msg);
          throw exception;
        }
        else if ( dnsProtocolType.equals( LoulanDNSClientConstants.DNS_PROTOCOL_TYPE_DOH ) )
        {
          String msg = String.format("Failed to create DNSLookupClient. Specified DNS protocol type is not implemented. key=%s ,value=%s", LoulanDNSClientConstants.PROP_KEY_DNS_PROTOCOL_TYPE, dnsProtocolType );
          DNSClientCommonException exception = new DNSClientCommonException(msg);
          throw exception;
        }
        else if ( dnsProtocolType.equals( LoulanDNSClientConstants.DNS_PROTOCOL_TYPE_DOT ) )
        {
          String msg = String.format("Failed to create DNSLookupClient. Specified DNS protocol type is not implemented. key=%s ,value=%s", LoulanDNSClientConstants.PROP_KEY_DNS_PROTOCOL_TYPE, dnsProtocolType );
          DNSClientCommonException exception = new DNSClientCommonException(msg);
          throw exception;
        }
        else
        {
          String msg = String.format("Failed to create DNSLookupClient. Unknown DNS protocol type is not implemented. key=%s ,value=%s", LoulanDNSClientConstants.PROP_KEY_DNS_PROTOCOL_TYPE, dnsProtocolType );
          DNSClientCommonException exception = new DNSClientCommonException(msg);
          throw exception;
        }
    }
    catch(DNSServiceCommonException cause)
    {
      String msg = String.format("Failed to create DNSLookupClient. Caused by DNSServiceCommonException. key=%s ,value=%s", LoulanDNSClientConstants.PROP_KEY_DNS_PROTOCOL_TYPE, dnsProtocolType );
      DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
      throw exception;
    }
      
      

      return client;
    }






}

