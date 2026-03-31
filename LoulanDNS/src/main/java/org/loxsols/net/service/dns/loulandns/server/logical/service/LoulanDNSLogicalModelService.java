package org.loxsols.net.service.dns.loulandns.server.logical.service;


import java.util.*;


import org.loxsols.net.service.dns.loulandns.server.logical.model.*;
import org.springframework.boot.autoconfigure.security.oauth2.server.servlet.OAuth2AuthorizationServerProperties.Endpoint;
import org.loxsols.net.service.dns.loulandns.server.http.spring.model.DNSResolverInstanceProperties;
import org.loxsols.net.service.dns.loulandns.server.http.spring.service.LoulanDNSLogicalDBService;

import org.loxsols.net.service.dns.loulandns.server.common.*;



public class LoulanDNSLogicalModelService
{

    LoulanDNSLogicalDBService logicalDBService;

    public LoulanDNSLogicalModelService(LoulanDNSLogicalDBService logicalDBService)
    {
        this.logicalDBService = logicalDBService;
    }


    // DBからユーザー情報(論理モデル)を取得する.
    public UserInfo getUserInfo(String userName) throws LoulanDNSSystemServiceException
    {
        UserInfo userInfo = logicalDBService.getUserInfo(userName);
        return userInfo;
    }

    // DBからユーザー情報(論理モデル)を取得する.
    public UserInfo getUserInfo(long userID) throws LoulanDNSSystemServiceException
    {
        UserInfo userInfo = logicalDBService.getUserInfo(userID);
        return userInfo;
    }


    public boolean isExistingUser(String userName) throws LoulanDNSSystemServiceException
    {
        if ( getUserInfo(userName) == null )
        {
            return false;
        }

        return true;
    }


    public boolean isExistingUser(Long userID) throws LoulanDNSSystemServiceException
    {
        if ( userID == null )
        {
            return false;
        }


        if ( getUserInfo(userID) == null )
        {
            return false;
        }
        
        return true;
    }


    // DBからユーザー情報(論理モデル)を取得する.
    public List<UserInfo> getUserInfoList() throws LoulanDNSSystemServiceException
    {
        List<UserInfo> userInfoList = logicalDBService.getUserInfoList();
        return userInfoList;
    }


    // ユーザー情報(論理モデル)をもとにDBを更新する.(複数のテーブルを同時に更新する.)
    public UserInfo saveUserInfo(UserInfo userInfo) throws LoulanDNSSystemServiceException
    {
        UserInfo savedUserInfo = logicalDBService.saveUserInfo(userInfo);
        return savedUserInfo;
    }

    // 指定したユーザーIDに紐づく情報を削除する.
    public void deleteUserInfo(long userID) throws LoulanDNSSystemServiceException
    {
        logicalDBService.deleteUserInfo(userID);
    }


    // DNSサービスインスタンス情報(論理モデル)をDBから取得する.
    public DNSServiceInstanceInfo getDNSServiceInstanceInfo(long dnsServiceInstanceID) throws LoulanDNSSystemServiceException
    {
        DNSServiceInstanceInfo dnsServiceInstanceInfo = logicalDBService.getDNSServiceInstanceInfo(dnsServiceInstanceID);
        return dnsServiceInstanceInfo;
    }

    // DNSサービスインスタンス情報(論理モデル)をもとにDBを更新する.(複数のテーブルを同時に更新する.)
    public DNSServiceInstanceInfo saveDNSServiceInstanceInfo(DNSServiceInstanceInfo dnsServiceInstanceInfo) throws LoulanDNSSystemServiceException
    {
        DNSServiceInstanceInfo savedDNSServiceInstanceInfo = logicalDBService.saveDNSServiceInstanceInfo(dnsServiceInstanceInfo);
        return savedDNSServiceInstanceInfo;
    }

    // DNSサービスインスタンス情報(論理モデル)をDBから削除する.(複数のテーブルを同時に更新する.)
    public void deleteDNSServiceInstanceInfo(long dnsServiceInstanceID) throws LoulanDNSSystemServiceException
    {
        logicalDBService.deleteDNSServiceInstanceInfo(dnsServiceInstanceID);
    }


    /**
     * DNSリゾルバインスタンス情報(論理モデル)をDBから取得する.
     * 
     * @param dnsServiceInstanceID
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public DNSResolverInstanceInfo getDNSResolverInstanceInfo(long dnsServiceInstanceID) throws LoulanDNSSystemServiceException
    {
        DNSResolverInstanceInfo dnsResolverInstanceInfo = logicalDBService.getDNSResolverInstanceInfo(dnsServiceInstanceID);
        return dnsResolverInstanceInfo;
    }



    // DBからシステムプロパティ情報(論理モデル)のリストを取得する.
    public List<SystemPropertiesInfo> getSystemPropertiesInfoList() throws LoulanDNSSystemServiceException
    {
        List<SystemPropertiesInfo> systemPropertiesInfoList = logicalDBService.getSystemPropertiesInfoList();
        return systemPropertiesInfoList;
    }

    // DBからシステムプロパティ情報(論理モデル)の指定したいプロパティキーに対応するリストを取得する.
    public List<SystemPropertiesInfo> getSystemPropertiesInfoList(String systemPropertyKey) throws LoulanDNSSystemServiceException
    {
        List<SystemPropertiesInfo> systemPropertiesInfoList = logicalDBService.getSystemPropertiesInfoListBySystemPropertyKey(systemPropertyKey);
        return systemPropertiesInfoList;
    }



    /**
     * ユーザー名とサービスインスタンス名をもとに、エンドポイント情報を返す.
     * 
     * @param userName
     * @param serviceInstanceName
     * @return
     * @throws LoulanDNSSystemServiceExceptions
     */
    public DNSServiceInstanceInfo getDNSServiceInstanceInfo(String userName, String serviceInstanceName) throws LoulanDNSSystemServiceException
    {
        UserInfo userInfo = getUserInfo(userName);
        if ( userInfo == null )
        {
            return null;
        }

        List<DNSServiceInstanceInfo> serviceInfoList = userInfo.getDNSServiceInstanceInfoList();

        for( DNSServiceInstanceInfo serviceInfo : serviceInfoList )
        {
            if (serviceInfo.getDNSServiceInstanceName().equals(serviceInstanceName) )
            {
                return serviceInfo;
            }            
        }

        return null;
    }


    /**
     * ユーザー名とサービスインスタンス名、エンドポイントインスタンス名をもとに、エンドポイント情報を返す.
     * 
     * @param userName
     * @param endpointInstanceName
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public DNSServiceEndpointInstanceInfo getDNSServiceEndpointInstanceInfo(String userName, String serviceInstanceName, String endpointInstanceName) throws LoulanDNSSystemServiceException
    {
        DNSServiceInstanceInfo serviceInfo = getDNSServiceInstanceInfo(userName, serviceInstanceName);
        if ( serviceInfo == null )
        {
            return null;
        }

        List<DNSServiceEndpointInstanceInfo>  endpointInfoList = serviceInfo.getDNSServiceEndpointInstanceInfoList();
        for( DNSServiceEndpointInstanceInfo endpointInfo :  endpointInfoList )
        {
            if ( endpointInfo.getDNSServiceEndpointInstanceName().equals(endpointInstanceName) )
            {
                return endpointInfo;
            }
        }

        return null;
    }

}

