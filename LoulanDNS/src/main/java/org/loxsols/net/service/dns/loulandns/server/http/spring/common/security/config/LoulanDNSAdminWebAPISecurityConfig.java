package org.loxsols.net.service.dns.loulandns.server.http.spring.common.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.config.Customizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;


import org.loxsols.net.service.dns.loulandns.server.http.spring.common.security.provider.*;
import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;




@Configuration
@EnableWebSecurity
public class LoulanDNSAdminWebAPISecurityConfig
{

    @Autowired
    @Qualifier("loulanDNSAuthenticationProviderImpl")
    LoulanDNSAuthenticationProvider provider;

    /*
    // 以下はテスト用のオンメモリ認証用.
	@Bean
	public UserDetailsService userDetailsService()
    {
		UserDetails userDetails = User.withDefaultPasswordEncoder()
			.username("user")
			.password("password")
			.roles("USER")
			.build();

		return new InMemoryUserDetailsManager(userDetails);
	}
    */

    /*
    // Spring5.4以降のverではWebSecurityConfigurerAdapterを継承する操作は実装できない.
    // 別途、SecurityFilterChain をBean定義して実装すること.
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception
    {
        String adminUserName = LoulanDNSConstatns.CONST_ADMIN_USER_NAME;
        AuthenticationProvider authProvider = new LoulanDNSUserBasicAuthenticationProvider(adminUserName);
        auth.authenticationProvider( authProvider );
    }
    */

    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        /*
        http.authorizeHttpRequests((requests) -> requests.requestMatchers("/admin/api").authenticated()
        .requestMatchers("/").permitAll())
        .formLogin(Customizer.withDefaults())
        .httpBasic(Customizer.withDefaults());
        */

        // アクセス権限に関する設定
        http.authorizeHttpRequests(
                // /はアクセス制限をかけない
                (requests) -> requests
                .requestMatchers("/admin/**").authenticated()
                .requestMatchers("/admin/api/*").authenticated()
                // それ以外のページは認証不要
               .anyRequest().permitAll()
            ).httpBasic(Customizer.withDefaults()).authenticationProvider( this.authProvider() );

        // CSRF対策が有効だとTokenの付されていないPOSTメソッドやPUTメソッドがHTTP403エラーとなるため、無効化する
        http.csrf().disable();

        return http.build();
    }

    
    @Bean
    public AuthenticationProvider authProvider()
    {

        return provider;
    }
    

    


}