package org.loxsols.net.service.dns.loulandns.server.http.spring.controller.service.api.admin;


import static org.mockito.ArgumentMatchers.nullable;

import java.time.ZonedDateTime;
import java.util.ArrayList;
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
import org.xbill.DNS.Zone;
import org.xbill.DNS.tools.update;
import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;
import org.loxsols.net.service.dns.loulandns.server.http.spring.model.*;
import org.loxsols.net.service.dns.loulandns.server.http.spring.service.LoulanDNSDBService;
import org.loxsols.net.service.dns.loulandns.server.http.spring.service.LoulanDNSLogicalDBService;
import org.loxsols.net.service.dns.loulandns.server.logical.model.factory.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.LoulanDNSLogicalModelService;

import org.loxsols.net.service.dns.loulandns.server.logical.model.*;

import org.loxsols.net.service.dns.loulandns.server.http.spring.common.exception.HttpStatus404Exception;


// LoulanDNSの共通ログサービス(CommonSystemLog)の管理API
@RestController
@RequestMapping("/admin/api/system/log")
public class LoulanDNSAdminCommonSystemLogWebAPIService
{

    @Autowired
    @Qualifier("loulanDNSDBServiceImpl")
    LoulanDNSDBService loulanDNSDBService;

    @Autowired
    @Qualifier("loulanDNSLogicalDBServiceImpl")
    LoulanDNSLogicalDBService loulanDNSLogicalDBService;

    @Autowired
    @Qualifier("loulanDNSLogicalModelServiceImpl")
    LoulanDNSLogicalModelService loulanDNSLogicalModelService;


    DNSServiceInstanceInfoFactory dnsServiceInstanceInfoFactory = new DNSServiceInstanceInfoFactory();
    DNSServiceInstancePropertyInfoFactory dnsServiceInstancePropertyInfoFactory = new DNSServiceInstancePropertyInfoFactory();

    DNSResolverInstanceInfoFactory dnsResolverInstanceInfoFactory = new DNSResolverInstanceInfoFactory();
    DNSResolverInstancePropertyInfoFactory dnsResolverInstancePropertyInfoFactory = new DNSResolverInstancePropertyInfoFactory();
    
    DNSServiceEndpointInstanceInfoFactory dnsServiceEndpointInstanceInfoFactory = new DNSServiceEndpointInstanceInfoFactory();
    DNSServiceEndpointInstancePropertyInfoFactory dnsServiceEndpointInstancePropertyInfoFactory = new DNSServiceEndpointInstancePropertyInfoFactory();

    public LoulanDNSAdminCommonSystemLogWebAPIService()
    {

    }

    // 共通システムログ情報の取得
    /**
     * 
     * 
     * @param tag CommmonSystemLogレコードのタグ
     * @param fromDateTime
     * @param untilDateTime
     * @param maxSize
     * @return
     * @throws DNSServiceCommonException
     */
    @GetMapping("/list/common-system-log")
    public CommonSystemLogInfo[] listCommonSystemLogInfo(@RequestParam(name = "Tag", required = true) String tag, @RequestParam(name = "FromDateTime", required = false) String fromDateTime, @RequestParam(name = "UntilDateTime", required = false) String untilDateTime, @RequestParam(name = "MaxSize", required = false) String maxSizeValueString) throws DNSServiceCommonException
    {
        // 下限日時が未指定の場合は、本システムにおける最小表現日時を指定する.
        ZonedDateTime from = LoulanDNSUtils.getSystemMinimumZonedDateTime();
        if ( fromDateTime != null && fromDateTime.isEmpty() == false )
        {
            from = LoulanDNSUtils.toDateTimeObject(fromDateTime);
        }

        // 上限日時が未指定の場合は、本システムにおける最大表現日時を指定する.
        ZonedDateTime until = LoulanDNSUtils.getSystemMaxZonedDateTime();
        if ( untilDateTime != null && untilDateTime.isEmpty() == false)
        {
            until = LoulanDNSUtils.toDateTimeObject(untilDateTime);
        }

        // 最大取得件数が未指定の場合はとりあえず10000件を取得する.
        int maxSize = 10000;
        if ( maxSizeValueString != null && maxSizeValueString.isEmpty() == false )
        {
            maxSize = Integer.parseInt(maxSizeValueString);
        }


        List<CommonSystemLogInfo> commonSystemLogInfoList 
             = loulanDNSLogicalDBService.getCommonSystemLogInfoListByTag(tag, from, until, maxSize);

        CommonSystemLogInfo[] commonSystemLogInfoArray = new CommonSystemLogInfo[ commonSystemLogInfoList.size() ];

        for( int i=0; i < commonSystemLogInfoList.size(); i++ )
        {
            commonSystemLogInfoArray[i] = commonSystemLogInfoList.get(i);
        }

        return commonSystemLogInfoArray;
    }

}