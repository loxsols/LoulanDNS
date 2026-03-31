package org.loxsols.net.service.dns.loulandns.server.http.spring.controller.api.admin;


import java.util.Base64;
import java.util.List;

import org.springframework.stereotype.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;



import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;

import org.loxsols.net.service.dns.loulandns.server.http.spring.model.*;
import org.loxsols.net.service.dns.loulandns.server.http.spring.service.LoulanDNSDBService;
import org.loxsols.net.service.dns.loulandns.server.http.spring.service.LoulanDNSLogicalDBService;

import org.loxsols.net.service.dns.loulandns.server.logical.model.UserInfo;
import org.loxsols.net.service.dns.loulandns.server.logical.model.factory.UserInfoFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.service.LoulanDNSLogicalModelService;

import org.loxsols.net.service.dns.loulandns.server.http.spring.common.exception.HttpStatus404Exception;


// LoulanDNSのDNSサービスインスタンス管理用WebAPIサービスクラス
@RestController
@RequestMapping("/admin/api/dns/service")
public class LoulanDNSAdminDNSServiceInstanceWebAPIService
{

    // 次の値と同等の文字列を設定する. ※ 固定値でないとコンパイルが通らないため.
    // "LoulanDNSConstatns.DB_CONST_VALUE_RECORD_STATUS_ACTIVE"
    final static String PRM_STR_DB_CONST_VALUE_RECORD_STATUS_ACTIVE = "101";
    // "LoulanDNSConstatns.DB_CONST_VALUE_RECORD_STATUS_INACTIVE"
    final static String PRM_STR_DB_CONST_VALUE_RECORD_STATUS_INACTIVE = "401";


    @Autowired
    @Qualifier("loulanDNSDBServiceImpl")
    LoulanDNSDBService loulanDNSDBService;

    @Autowired
    @Qualifier("loulanDNSLogicalDBServiceImpl")
    LoulanDNSLogicalDBService loulanDNSLogicalDBService;

    @Autowired
    @Qualifier("loulanDNSLogicalModelServiceImpl")
    LoulanDNSLogicalModelService loulanDNSLogicalModelService;


    UserInfoFactory userInfoFactory = new UserInfoFactory();
    
    public LoulanDNSAdminDNSServiceInstanceWebAPIService()
    {

    }

        // DNSサービスインスタンス情報の一覧取得
        @GetMapping("/list/dns-service-instance")
        public DNSServiceInstance[] listDNSServiceInstance(@RequestParam(name = "UserName", required = false) String userName) throws DNSServiceCommonException
        {
            List<DNSServiceInstance> dnsServiceInstanceList = null;
            
            if ( userName != null )
            {
                UserInfo userInfo = loulanDNSLogicalModelService.getUserInfo(userName);
                if(userInfo == null )
                {
                    return null;
                }

                dnsServiceInstanceList = loulanDNSDBService.getDNSServiceInstanceListByUserID( userInfo.getUserID() );
            }
            else
            {
                dnsServiceInstanceList = loulanDNSDBService.getDNSServiceInstanceList();
            }

            DNSServiceInstance[] dnsServiceInstanceArray = dnsServiceInstanceList.toArray(new DNSServiceInstance[dnsServiceInstanceList.size()]);
            return dnsServiceInstanceArray;
        }


        // DoHインスタンス情報取得
        @GetMapping("/get/dns-service-instance")
        public DNSServiceInstance getDoHServerInstance(  @RequestParam(name = "UserName", required = true) String userName,
                                                    @RequestParam(name = "DNSServiceInstanceName", required = true) String dnsServiceInstanceName ) throws DNSServiceCommonException
        {

            DNSServiceInstance[] dnsServiceInstanceArray = listDNSServiceInstance(userName);

            if ( dnsServiceInstanceArray == null )
            {
                return null;
            }
            
            for( DNSServiceInstance dnsServiceInstance : dnsServiceInstanceArray )
            {

                if ( dnsServiceInstance.getDNSServiceInstanceName().equals(dnsServiceInstanceName) )
                {
                    return dnsServiceInstance;
                }
            }

            return null;
        }

}