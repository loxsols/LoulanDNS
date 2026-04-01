package org.loxsols.net.service.dns.loulandns.app.spring.service.api.admin;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.authentication.AuthenticationProvider;
import org.xbill.DNS.ZoneTransferException;

import org.loxsols.net.service.dns.loulandns.server.http.spring.repository.UserRepository;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;

import org.loxsols.net.service.dns.loulandns.server.http.spring.service.LoulanDNSDBService;
import org.loxsols.net.service.dns.loulandns.server.http.spring.service.LoulanDNSLogicalDBService;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.IDNSProtocolModelInstanceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.factory.IDNSResolverInstanceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.factory.impl.DNSResolverInstanceFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.factory.IDNSServiceInstanceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.logger.factory.ILoulanDNSLoggerFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.logger.factory.impl.LoulanDNSLoggerFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.service.LoulanDNSLogicalModelService;

import org.loxsols.net.service.dns.loulandns.server.http.spring.common.security.provider.*;
import org.loxsols.net.service.dns.loulandns.server.http.spring.common.security.provider.impl.*;
import org.loxsols.net.service.dns.loulandns.server.http.spring.common.security.provider.impl.mock.*;
import org.loxsols.net.service.dns.loulandns.server.http.spring.controller.service.endpoint.doh.LoulanDNSDoHService;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;


import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.model.DNSProtocolModelInstanceFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.IDNSMessageFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.*;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.*;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.service.*;
import org.loxsols.net.service.dns.loulandns.server.http.spring.controller.service.api.admin.*;
import org.loxsols.net.service.dns.loulandns.app.spring.service.endpoint.doh.DoHServiceApplicationConfig;
import org.loxsols.net.service.dns.loulandns.app.spring.base.config.LoulanDNSBaseApplicationConfig;



@Configuration
@Import(LoulanDNSBaseApplicationConfig.class)
@ComponentScan(
    basePackages = "org.loxsols.net.service.dns.loulandns.server.http.spring.controller.service.api.admin",
    useDefaultFilters = false, // 全てのコンポーネントを読み込むデフォルト設定をオフにする
    includeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE, 
        classes = LoulanDNSAdminDNSServiceInstanceWebAPIService.class // ここで指定したコントローラークラスだけを読み込む.
    )
)
@ComponentScan(
    basePackages = "org.loxsols.net.service.dns.loulandns.server.http.spring.controller.service.api.admin",
    useDefaultFilters = false, // 全てのコンポーネントを読み込むデフォルト設定をオフにする
    includeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE, 
        classes = LoulanDNSAdminUserWebAPIService.class // ここで指定したコントローラークラスだけを読み込む.
    )
)
public class LoulanDNSAdminAPIServiceApplicationConfig
{


}