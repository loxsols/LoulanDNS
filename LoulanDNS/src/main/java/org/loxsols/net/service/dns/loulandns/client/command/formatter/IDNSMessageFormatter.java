package org.loxsols.net.service.dns.loulandns.client.command.formatter;

import org.loxsols.net.service.dns.loulandns.client.common.DNSClientCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSQuestionMessage;

public interface IDNSMessageFormatter
{
    // 指定したDNS問い合わせメッセージを生成する.
    public IDNSQuestionMessage buildDNSQuestionMessage(String qname, int qtype, int qclass)  throws DNSServiceCommonException;

    public String toBase64String(IDNSMessage dnsMessage) throws DNSServiceCommonException;

    public String toHexString(IDNSMessage dnsMessage) throws DNSServiceCommonException;

}

