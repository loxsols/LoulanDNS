package org.loxsols.net.service.dns.loulandns.server.common;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.constants.DNSProtocolConstants;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSProtocolUtils;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.model.DNSProtocolModelInstanceFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.IDNSProtocolModelInstanceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSQuestionMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;


// DNSプロトコルの規定に起因する例外のうち、DNSレスポンスメッセージがエラー(RCODEが1-23)を返却すべき現象が生じた場合の例外.
// 
public class DNSProtocolErrorRCodeException extends DNSProtocolException
{

    LoulanDNSProtocolUtils protocolUtils = new LoulanDNSProtocolUtils();

    protected IDNSQuestionMessage dnsQuestionMessage;

    // DNSメッセージのヘッダセクションのRCODEの値.
    protected int rcode;

    public void setDNSQuestionMessage(IDNSQuestionMessage message)
    {
        this.dnsQuestionMessage = message;
    }

    public IDNSQuestionMessage getDNSQuestionMessage()
    {
        return this.dnsQuestionMessage;
    }


    public void setResponseCode(int code) throws DNSServiceCommonException
    {
        if (code < 0 || code > 23 )
        {
            // rcodeは4bitなので0以上23以下の値でないとおかしい.
            String msg = String.format("Invalid RCODE(%d). RCODE is 4bit value.", code );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        this.rcode = code;
    }

    public int getResponseCode() throws DNSServiceCommonException
    {
        return this.rcode;
    }


    
    public DNSProtocolErrorRCodeException(String msg, Throwable cause, IDNSQuestionMessage questionMessage, int rcode) throws DNSServiceCommonException
    {
        super(msg, cause);
        setDNSQuestionMessage(questionMessage);
        setResponseCode( rcode );
    }


    public IDNSResponseMessage createDNSErrorResponseMessage() throws DNSServiceCommonException
    {

        // 問い合わせメッセージのコピーを生成する.(メッセージ中の各セクションのインスタンスを直接編集するため.)
        IDNSQuestionMessage questionMessage = protocolUtils.copyDNSQuestionMessageInstance(getDNSQuestionMessage());
        
        // レスポンスメッセージを生成する.
        IDNSResponseMessage responseMessage = protocolUtils.createDNSResponseMessageFromQuestionMessage( questionMessage );
        responseMessage.getDNSHeaderSection().setRCode( getResponseCode() );

        return responseMessage;
    }






}