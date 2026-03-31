package org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.logger.factory.impl;

import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.logger.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.logger.factory.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.logger.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;
import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.http.spring.service.LoulanDNSLogicalDBService;

import java.util.*;


/**
 * ロガーインスタンスのファクトリクラス
 * 
 */
@Configuration
@EnableAutoConfiguration
@EnableJpaRepositories( basePackages = "org.loxsols.net.service.dns.loulandns.server.http.spring.repository" )
@EntityScan("org.loxsols.net.service.dns.loulandns.server.http.spring")
@ComponentScan("org.loxsols.net.service.dns.loulandns.server.http.spring")
@ComponentScan("org.loxsols.net.service.dns.loulandns.server.http.spring.repository")
@ComponentScan("org.loxsols.net.service.dns.loulandns.server.http.spring.model")
@ComponentScan("org.loxsols.net.service.dns.loulandns.server.http.spring.controller")
@ComponentScan("org.loxsols.net.service.dns.loulandns.server.http.spring.common.security")
@ComponentScan("org.loxsols.net.service.dns.loulandns.server.http.spring.common.security.provider")
public class LoulanDNSLoggerFactoryImpl extends LoulanDNSLoggerFactoryImplBase implements ILoulanDNSLoggerFactory
{


    public void setLogicalDBService(LoulanDNSLogicalDBService instance)
    {
        super.setLogicalDBService(instance);
    }

    
}