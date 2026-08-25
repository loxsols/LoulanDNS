package org.loxsols.net.service.dns.loulandns.server.common;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.constants.DNSProtocolConstants;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSProtocolUtils;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.model.DNSProtocolModelInstanceFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.IDNSProtocolModelInstanceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSQuestionMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;


// DNSプロトコルの規定に起因する例外のうち、DNSレスポンスメッセージのRCODEがSERVAIL(2)を返却する事象の例外.
// 
public class DNSProtocolRcodeErrorSERVFAILException extends DNSProtocolErrorRCodeException
{

    // DNSメッセージのヘッダセクションのRCODEの値.
    // 常にSERVFAIL(2)で固定する.
    protected int rcode = DNSProtocolConstants.DNS_RCODE_SERVFAIL;

    public void setResponseCode(int code) throws DNSServiceCommonException
    {
        if ( code != DNSProtocolConstants.DNS_RCODE_SERVFAIL )
        {
            // 本クラスはSERVAIL専用なので例外を返す.
            String msg = String.format("Failed to set RCODE(%d). This class is used to only SERVFAIL(%d).", code, DNSProtocolConstants.DNS_RCODE_SERVFAIL );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        this.rcode = code;
    }


    
    public DNSProtocolRcodeErrorSERVFAILException(String msg, Throwable cause, IDNSQuestionMessage questionMessage) throws DNSServiceCommonException
    {
        super(msg, cause, questionMessage, DNSProtocolConstants.DNS_RCODE_SERVFAIL);
    }



}