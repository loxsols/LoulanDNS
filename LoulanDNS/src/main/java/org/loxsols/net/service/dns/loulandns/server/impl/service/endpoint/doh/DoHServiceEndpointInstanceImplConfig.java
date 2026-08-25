package org.loxsols.net.service.dns.loulandns.server.impl.service.endpoint.doh;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.core.env.Environment;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.web.bind.annotation.RestController;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.io.IOException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.FilterType;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.authentication.AuthenticationProvider;
import org.xbill.DNS.ZoneTransferException;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Configuration;

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
import org.loxsols.net.service.dns.loulandns.app.spring.base.config.LoulanDNSBaseApplicationConfig;


/*
@SpringBootApplication
@Configuration
@ComponentScan(
    basePackages = "org.loxsols.net.service.dns.loulandns.server.http.spring.controller.service.endpoint.doh",
    useDefaultFilters = false, // 全てのコンポーネントを読み込むデフォルト設定をオフにする
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.CUSTOM,
        classes = DoHServiceEndpointInstanceImplConfig.ControllerMatchFilter.class
    )
)
*/

@Configuration
@Import(LoulanDNSBaseApplicationConfig.class)
@ComponentScan(
    basePackages = "org.loxsols.net.service.dns.loulandns.server.http.spring.controller.service.endpoint.doh",
    useDefaultFilters = false, // 全てのコンポーネントを読み込むデフォルト設定をオフにする
    includeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE, 
        classes = LoulanDNSDoHService.class // ここで指定したコントローラークラスだけを読み込む.
    )
)
public class DoHServiceEndpointInstanceImplConfig implements WebServerFactoryCustomizer<ConfigurableWebServerFactory>
{

    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        // ソースコード内で直接ポート番号を指定
        // factory.setPort(58080);
    }

    // Configurationクラス内のネストクラスとしてフィルターを定義
    // Springが自動的に引数のEnvironmentをインジェクトしてくれます
    public static class ControllerMatchFilter implements TypeFilter {
        
        private final Environment environment;

        public ControllerMatchFilter(Environment environment) {
            this.environment = environment;
        }

        /*
        @Override
        public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
            // 対象クラスが @RestController を持っているか判定
            boolean isRestController = metadataReader.getAnnotationMetadata().hasAnnotation(RestController.class.getName());
            if (!isRestController) {
                return false; // コントローラー以外はスルー（除外しない）
            }

            // 起動引数や properties から指定のクラス名（またはキーワード）を取得
            // String targetController = environment.getProperty("target.controller");
            String targetController = "org.loxsols.net.service.dns.loulandns.server.http.spring.controller.service.endpoint.doh.LoulanDNSDoHService";


            // 指定がない場合はすべてのRestControllerを有効化（除外しない）
            if (targetController == null || targetController.isEmpty()) {
                return false;
            }

            String className = metadataReader.getClassMetadata().getClassName();

            // クラスの完全修飾名(FQN)で一致するかチェック
            boolean ret = true;
            if ( className.equals(targetController) )
            {
                ret = false;
            }

            System.out.println("[DEBUG] DoHServiceEndpointInstanceImplConfig.ControllerMatchFilter.match() : className=" + className + ", ret=" + ret);

            return ret;

        }

        */

        @Override
        public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException
        {
            boolean ret = false;

            String className = metadataReader.getClassMetadata().getClassName();
            System.out.println("[DEBUG] DoHServiceEndpointInstanceImplConfig.ControllerMatchFilter.match() : className=" + className + ", ret=" + ret);

            return ret;
        }
    }


    @Bean
    public CommandLineRunner logEndpoints(RequestMappingHandlerMapping handlerMapping) {
        return args -> {
            // 特定のシステムプロパティがある場合のみ実行
            if ("true".equals(System.getProperty("debug.mode"))) {
                System.out.println("\n====== [DEBUG] 登録済みURLマッピング一覧 ======");
                handlerMapping.getHandlerMethods().forEach((info, method) -> {
                    // INFOからURLパターンとHTTPメソッド、背後のクラス・メソッド名を出力
                    System.out.printf("Mapping: %s -> %s.%s()\n",
                            info.getPatternsCondition(),
                            method.getBeanType().getSimpleName(),
                            method.getMethod().getName());
                });
                System.out.println("===============================================\n");
            }
        };
    }


    @Bean
    public FilterRegistrationBean<Filter> loggingFilter() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        
        // 匿名クラス、またはラムダでリクエストダンプ用のFilterを定義
        registrationBean.setFilter(new Filter() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {
                
                if ("true".equals(System.getProperty("debug.mode")) && request instanceof HttpServletRequest) {
                    HttpServletRequest httpRequest = (HttpServletRequest) request;
                    
                    System.out.println("------ [DEBUG] 受信HTTPリクエスト ------");
                    System.out.printf("Method/URL: %s %s\n", httpRequest.getMethod(), httpRequest.getRequestURI());
                    System.out.printf("Remote ADDR: %s\n", httpRequest.getRemoteAddr());
                    
                    // ヘッダーのダンプ
                    System.out.println("Headers:");
                    Collections.list(httpRequest.getHeaderNames()).forEach(headerName -> 
                        System.out.printf("  %s: %s\n", headerName, httpRequest.getHeader(headerName))
                    );
                    System.out.println("----------------------------------------");
                }
                
                // 次の処理（Spring MVC等）へリクエストを流す
                chain.doFilter(request, response);
            }
        });

        // すべてのパス（/*）を対象にする
        registrationBean.addUrlPatterns("/*");
        // SpringのFilterチェーンの中で最優先（最外殻）で実行させる
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE); 
        
        return registrationBean;
    }

}



