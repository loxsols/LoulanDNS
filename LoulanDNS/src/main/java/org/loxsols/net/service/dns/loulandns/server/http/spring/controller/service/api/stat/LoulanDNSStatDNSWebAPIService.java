package org.loxsols.net.service.dns.loulandns.server.http.spring.controller.service.api.stat;


import static org.mockito.ArgumentMatchers.nullable;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

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
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.http.MediaType;


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
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.stat.model.ILoulanDNSServiceStatLogRecord;
import org.loxsols.net.service.dns.loulandns.server.logical.model.*;

import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.stat.*;


import org.loxsols.net.service.dns.loulandns.server.http.spring.common.exception.HttpStatus404Exception;


// LoulanDNSのDNS統計情報取得Webサービス
@RestController
@RequestMapping("/stat/api/dns")
public class LoulanDNSStatDNSWebAPIService
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

    @Autowired
    @Qualifier("loulanDNSStatLogServiceFactoryImpl")
    ILoulanDNSStatLogServiceFactory loulanDNSStatLogServiceFactory;


    DNSServiceInstanceInfoFactory dnsServiceInstanceInfoFactory = new DNSServiceInstanceInfoFactory();
    DNSServiceInstancePropertyInfoFactory dnsServiceInstancePropertyInfoFactory = new DNSServiceInstancePropertyInfoFactory();

    DNSResolverInstanceInfoFactory dnsResolverInstanceInfoFactory = new DNSResolverInstanceInfoFactory();
    DNSResolverInstancePropertyInfoFactory dnsResolverInstancePropertyInfoFactory = new DNSResolverInstancePropertyInfoFactory();
    
    DNSServiceEndpointInstanceInfoFactory dnsServiceEndpointInstanceInfoFactory = new DNSServiceEndpointInstanceInfoFactory();
    DNSServiceEndpointInstancePropertyInfoFactory dnsServiceEndpointInstancePropertyInfoFactory = new DNSServiceEndpointInstancePropertyInfoFactory();



    public LoulanDNSStatDNSWebAPIService()
    {
        
    }


    protected ILoulanDNSStatLogService getLoulanDNSStatLogService(String userName, String dnsServiceInstanceName) throws DNSServiceCommonException
    {

        // DNS統計情報ログサービスのインスタンス(本インスタンスは必ずファクトリクラスから生成すること.直接DIできない.)
        String statLogServiceName = LoulanDNSUtils.getDNSStatLogServiceName( userName, dnsServiceInstanceName);
        Properties options = new Properties();
        ILoulanDNSStatLogService loulanDNSStatLogServiceInstance = loulanDNSStatLogServiceFactory.getOrCreateLoulanDNSStatLogService(statLogServiceName, options);
        
        return loulanDNSStatLogServiceInstance;
    }


    /**
     * DNSサービスインスタンス層におけるDNS統計情報ログの取得API.
     * NDJSON方式で各レコードを1行のJSONレコードとして出力する.
     * 
     * @param userName
     * @param dnsServiceInstanceName
     * @param fromDateTime
     * @param untilDateTime
     * @param maxSize
     * @return
     * @throws DNSServiceCommonException
     */
    @GetMapping(value = "/list/dns-service-stat-log-record", produces = "application/x-ndjson")
    public ResponseBodyEmitter listDNSServiceStatLogRecord(@RequestParam(name = "UserName", required = true) String userName, @RequestParam(name="DNSServiceInstanceName", required=true) String dnsServiceInstanceName, @RequestParam(name = "FromDateTime", required = false) String fromDateTime, @RequestParam(name = "UntilDateTime", required = false) String untilDateTime, @RequestParam(name = "MaxSize", required = false)String maxSizeValue) throws DNSServiceCommonException
    {
        // 下限日時が未指定の場合は、本システムにおける最小表現日時を指定する.
        ZonedDateTime from = LoulanDNSUtils.getSystemMinimumZonedDateTime();
        if ( fromDateTime != null && fromDateTime.isEmpty() == false )
        {
            from = LoulanDNSUtils.toDateTimeObject(fromDateTime);
        }

        // 上限日時が未指定の場合は、本システムにおける最大表現日時を指定する.
        ZonedDateTime until = LoulanDNSUtils.getSystemMaxZonedDateTime();
        if ( untilDateTime != null && untilDateTime.isEmpty() == false )
        {
            until = LoulanDNSUtils.toDateTimeObject(untilDateTime);
        }

        // 最大取得件数が未指定の場合はとりあえず10000件を取得する.
        int maxSize = 10000;
        if ( maxSizeValue != null && maxSizeValue.isEmpty() == false )
        {
            maxSize = Integer.parseInt(maxSizeValue);
        }

        ILoulanDNSStatLogService dnsStatLogService = getLoulanDNSStatLogService(userName, dnsServiceInstanceName);

        List<ILoulanDNSServiceStatLogRecord> list = dnsStatLogService.getDNSServiceStatLogRecords(userName, dnsServiceInstanceName, from, until, maxSize);


        // NDJSON形式(各レコードを個別のJSONレコードとして改行処理した形式)で返すためのおまじない.
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();

        try
        {
            for (ILoulanDNSServiceStatLogRecord record : list)
            {
                emitter.send(record);
            
                // NDJSON（改行区切り）にするため、明示的に改行コードを送信
                emitter.send("\n", MediaType.TEXT_PLAIN);
            }

            // 4. すべて送信し終えたらクローズ
            emitter.complete();
        }
        catch(IOException cause)
        {
            String msg = String.format("Failed to convert to NDJSON Format.");
            DNSServiceCommonException exception = new DNSServiceCommonException(msg, cause);
            throw exception;
        }

        return emitter;
    }





}