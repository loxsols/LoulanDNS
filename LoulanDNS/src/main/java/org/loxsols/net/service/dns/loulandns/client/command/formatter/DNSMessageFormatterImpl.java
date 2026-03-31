package org.loxsols.net.service.dns.loulandns.client.command.formatter;


import java.io.IOException;
import java.net.InetAddress;

import org.loxsols.net.service.dns.loulandns.client.impl.*;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSProtocolUtils;
import org.loxsols.net.service.dns.loulandns.server.http.spring.common.security.provider.LoulanDNSAuthenticationProvider;
import org.loxsols.net.service.dns.loulandns.server.http.spring.service.LoulanDNSDBService;
import org.loxsols.net.service.dns.loulandns.server.http.spring.service.LoulanDNSLogicalDBService;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.DNSMessageFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.section.DNSAnswerSectionFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.section.DNSHeaderSectionFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.section.DNSQuestionSectionFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.model.DNSProtocolModelInstanceFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.IDNSMessageFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.IDNSAnswerSectionFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.IDNSHeaderSectionFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.IDNSQuestionSectionFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.IDNSProtocolModelInstanceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSQuestionMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.service.LoulanDNSLogicalModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.xbill.DNS.ZoneTransferException;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;


import org.loxsols.net.service.dns.loulandns.client.*;
import org.loxsols.net.service.dns.loulandns.client.common.DNSClientCommonException;

@SpringBootApplication
@ComponentScan
@EntityScan("org.loxsols.net.service.dns.loulandns.server.http.spring.model")
public class DNSMessageFormatterImpl implements IDNSMessageFormatter
{

    @Autowired
    @Qualifier("dnsMessageFactoryImpl")
    IDNSMessageFactory dnsMessageFactory;

    


    LoulanDNSProtocolUtils protocolUtils = new LoulanDNSProtocolUtils();


    // 指定したDNS問い合わせメッセージを生成する.
    public IDNSQuestionMessage buildDNSQuestionMessage(String qname, int qtype, int qclass)  throws DNSServiceCommonException
    {
        IDNSQuestionMessage message = dnsMessageFactory.createQuestionDNSMesssage(qname, qtype, qclass);
        return message;
    }

    public String toBase64String(IDNSMessage dnsMessage) throws DNSServiceCommonException
    {
        byte[] bytes = dnsMessage.getDNSMessageBytes();
        String base64String = protocolUtils.toBase64String(bytes);

        return base64String;
    }

    public String toHexString(IDNSMessage dnsMessage) throws DNSServiceCommonException
    {
        byte[] bytes = dnsMessage.getDNSMessageBytes();
        String hexString = protocolUtils.toDebugHexString(bytes);

        return hexString;
    }

    

}
