package org.loxsols.net.service.dns.loulandns.server.http.spring.common.security.provider;

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



// LoulanDNSの認証プロバイダ
public abstract class LoulanDNSAuthenticationProvider implements AuthenticationProvider
{

  // 認証処理を実行する
  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException
  {

    // 入力されたユーザー名とパスワードを取得
    String inputUserName = authentication.getName();
    String inputPassword = authentication.getCredentials().toString();

    System.out.println("[DEBUG] LoulanDNSAuthenticationProvider.authenticate(Authentication authentication)  : inputUserName=" + inputUserName );
    System.out.println("[DEBUG] LoulanDNSAuthenticationProvider.authenticate(Authentication authentication)  : inputPassword=" + inputPassword );


    // ユーザー名とパスワードが正しいかチェック
    boolean isAuthenticated;
    try
    {
      isAuthenticated = authenticate(inputUserName, inputPassword);
    }
    catch(DNSServiceCommonException cause )
    {
      String msg = String.format("Failed to check user authentication. userName=%s", inputUserName );
      AuthenticationException exception = new AuthenticationServiceException(msg, cause);
      throw exception;
    }

    if ( isAuthenticated == true )
    {
      // ユーザー名とパスワードを表現する認証済みオブジェクトを返す.
      UsernamePasswordAuthenticationToken token = 
        new UsernamePasswordAuthenticationToken(inputUserName, inputPassword, authentication.getAuthorities());
      return token;
    }
    else
    {
      throw new BadCredentialsException("Invalid user information.");
    }
  }

  // ユーザー名/パスワードによる認証処理を実行する.
  public boolean authenticate(String inputUserName, String inputPassword) throws DNSServiceCommonException
  {
    throw new DNSServiceCommonException("Not Implemented.");
  }


  // このクラスが引数に指定された認証リクエスト情報をサポートするときは true を返す.
  public boolean supports(Class<?> authentication)
  {
    // UsernamePasswordAuthenticationToken として扱える認証リクエストであれば true を返す
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }

}
