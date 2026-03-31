package org.loxsols.net.service.dns.loulandns.server.http.spring.common.security.provider.impl.mock;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationServiceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// import org.loxsols.net.service.dns.loulandns.server.http.spring.model.User;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;
import org.loxsols.net.service.dns.loulandns.server.http.spring.service.LoulanDNSDBService;


import org.loxsols.net.service.dns.loulandns.server.http.spring.common.security.provider.*;

// LoulanDNSのBASIC認証クラス
/**
 * LoulanDNSのBASIC認証クラスのモッククラス.
 * 全てのリクエストを承認する.
 */
public class LoulanDNSMockAuthenticationProvider extends LoulanDNSAuthenticationProvider implements AuthenticationProvider
{

  /**
   * モッククラスなので、常にtrueを返す.
   * 
   * @param inputUserName
   * @param inputPassword
   * @return
   * @throws DNSServiceCommonException
   */
  public boolean authenticate(String inputUserName, String inputPassword) throws DNSServiceCommonException
  {
    return true;
  }


}
