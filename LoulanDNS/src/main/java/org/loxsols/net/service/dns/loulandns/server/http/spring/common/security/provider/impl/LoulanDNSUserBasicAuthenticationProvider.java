package org.loxsols.net.service.dns.loulandns.server.http.spring.common.security.provider.impl;

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
public class LoulanDNSUserBasicAuthenticationProvider extends LoulanDNSAuthenticationProvider implements AuthenticationProvider
{

  @Autowired
  @Qualifier("loulanDNSDBServiceImpl")
  LoulanDNSDBService loulanDNSDBService;



  public org.loxsols.net.service.dns.loulandns.server.http.spring.model.User getTargetUser(String userName) throws DNSServiceCommonException
  {

    // 指定したユーザーがDB上に存在しない場合は例外をスロー.
    org.loxsols.net.service.dns.loulandns.server.http.spring.model.User user = loulanDNSDBService.getUser(userName);
    if ( user == null )
    {
      String msg = String.format("Specified user is not found. userName={0}", userName);
      DNSServiceCommonException exception = new DNSServiceCommonException(msg);
      throw exception;
    }

    return user;
  }



  public boolean authenticate(String inputUserName, String inputPassword) throws DNSServiceCommonException
  {

    org.loxsols.net.service.dns.loulandns.server.http.spring.model.User user = getTargetUser(inputUserName);

    System.out.println("[DEBUG] LoulanDNSUserBasicAuthenticationProvider.authenticate(String, String) : inputUserName=" + inputUserName );
    System.out.println("[DEBUG] LoulanDNSUserBasicAuthenticationProvider.authenticate(String, String) : inputPassword=" + inputPassword );
    System.out.println("[DEBUG] LoulanDNSUserBasicAuthenticationProvider.authenticate(String, String) : user=" + user );


    // ユーザー名とパスワードが正しいかチェック
    if (inputUserName.equals( user.getUserName() ) && inputPassword.equals( user.getUserPassword() ))
    {
      return true;
    }

    return false;

  }


  // このクラスが引数に指定された認証リクエスト情報をサポートするときは true を返す。
  @Override
  public boolean supports(Class<?> authentication)
  {
    // UsernamePasswordAuthenticationToken として扱える認証リクエストであれば true を返す
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }


}
