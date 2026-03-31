
package org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.model;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.*;


import org.loxsols.net.service.dns.loulandns.server.common.*;


import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.ComponentScan;


@ComponentScan
public class DNSProtocolModelInstanceFactoryImpl extends SimpleDNSProtocolModelInstanceFactoryImpl implements IDNSProtocolModelInstanceFactory
{

    /*
    @Autowired
    @Qualifier("dnsHeaderSectionFactoryImpl")
    public IDNSHeaderSectionFactory headerSectionFactory;

    @Autowired
    @Qualifier("dnsQuestionSectionFactoryImpl")
    public IDNSQuestionSectionFactory questionSectionFactory;

    @Autowired
    @Qualifier("dnsAnswerSectionFactoryImpl")
    public IDNSAnswerSectionFactory answerSectionFactory;

    @Autowired
    @Qualifier("dnsAuthoritySectionFactoryImpl")
    public IDNSAuthoritySectionFactory authoritySectionFactory;

    @Autowired
    @Qualifier("dnsAdditionalSectionFactoryImpl")
    public IDNSAdditionalSectionFactory additionalSectionFactory;
    */



    public DNSProtocolModelInstanceFactoryImpl() throws DNSServiceCommonException
    {
        super();

    }


    @Autowired
    @Qualifier("dnsHeaderSectionFactoryImpl")
    public void setHeaderSectionFactory(IDNSHeaderSectionFactory instance)
    {
        super.setHeaderSectionFactory(instance);
    }

    @Autowired
    @Qualifier("dnsQuestionSectionFactoryImpl")
    public void setQuestionSectionFactory(IDNSQuestionSectionFactory instance)
    {
        super.setQuestionSectionFactory(instance);
    }

    @Autowired
    @Qualifier("dnsAnswerSectionFactoryImpl")
    public void setAnswerSectionFactory(IDNSAnswerSectionFactory instance)
    {
        this.answerSectionFactory = instance;
    }

    @Autowired
    @Qualifier("dnsAuthoritySectionFactoryImpl")
    public void setAuthoritySectionFactory(IDNSAuthoritySectionFactory instance)
    {
        super.setAuthoritySectionFactory(instance);
    }

    @Autowired
    @Qualifier("dnsAdditionalSectionFactoryImpl")
    public void setAdditionalSectionFactory(IDNSAdditionalSectionFactory instance)
    {
        super.setAdditionalSectionFactory(instance);
    }




}