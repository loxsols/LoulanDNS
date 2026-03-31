package org.loxsols.net.service.dns.loulandns.server.http.spring.controller;


import java.util.Base64;

import org.springframework.stereotype.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;




import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.http.spring.model.*;
import org.loxsols.net.service.dns.loulandns.server.http.spring.service.LoulanDNSDBService;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.IDNSMessageFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.IDNSResolverInstance;

import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.factory.*;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;

@RestController
@RequestMapping("/doh")
public class LoulanDNSDoHService
{


    @Autowired
    @Qualifier("loulanDNSDBServiceImpl")
    LoulanDNSDBService loulanDNSDBService;


    @Autowired
    @Qualifier("dnsMessageFactoryImpl")
    IDNSMessageFactory dnsMessageFactory;


    @Autowired
    @Qualifier("dnsServiceInstanceFactoryImpl")
    IDNSServiceInstanceFactory dnsServiceInstanceFactory;

    
    
    // public LoulanDNSDoHService(DNSRecordRepository dnsRecordRepository)
    public LoulanDNSDoHService()
    {

    }



    @GetMapping("/test/db/get/user")
    public User getUser(@RequestParam(name = "userName", required = true) String userName) throws DNSServiceCommonException
    {

        User user = loulanDNSDBService.getUser(userName);
        return user;

    }



    @GetMapping("/test/DoHQuery")
    public byte[] handleTestDoHQuery(@RequestParam(name = "dns", required = true) String dnsQueryBase64) throws DNSServiceCommonException
    {

        // dnsパラメータはbase64エンコーディングされているので平文に変換する.
        byte[] rawDNSRequest = Base64.getDecoder().decode(dnsQueryBase64);



        // TODO : レスポンスタイプは "application/dnsmessage" を使用する.
        // TODO : TTLは HTTPヘッダ　"cache-control = max-age=<秒数>"　で表現する.
        
        // レスポンスボディの作成
        // 
        // RFC 8484では7.1節でDNSメッセージのエンコーディングについて以下のとおり定義している.
        //
        //      Encoding considerations: This is a binary format. The contents are a
        //      DNS message as defined in RFC 1035. The format used here is for
        //      DNS over UDP, which is the format defined in the diagrams in RFC 1035.
        //
        // したがって、RFC 1035(DNSメッセージの仕様書)に従ってレスポンスメッセージを作成する必要がある.

        // DNS問い合わせメッセージを解析する.
        IDNSQuestionMessage dnsQuestionMessage = parseDNSQuestionMessageBytes(rawDNSRequest);

        // DNSサービスインスタンスを取得or新規生成する.
        // 本メソッドは、試験用のDoHのI/Fなので、デフォルトのDNSサービスインスタンスを使用する.
        IDNSServiceInstance dnsServiceInstance = getDefaultDNSServiceInstance();

        // DNSクエリを処理してレスポンスを生成する.
        IDNSResponseMessage dnsResponseMessage = dnsServiceInstance.serveDNSQuery(dnsQuestionMessage);


        byte[] rawDNSResponse = dnsResponseMessage.getDNSMessageBytes();


        // RFC 8484では使用するHTTPプロトコルを以下のように定義している.
        //      The responses may be processed and transported in any order using HTTP’s
        //      multi-streaming functionality (see Section 5 of [RFC7540]).
        // RFC 7540はHTTP/2の仕様書なので、HTTP/2でレスポンスを返却する能力が求められる.


        return rawDNSResponse;

    }


    // *******************
    // DoHクエリ処理関数
    //      /doh/<DoHインスタンス名> で問い合わせを受け付ける.
    //      問い合わせるドメイン名はRFC8484ではURLパラメータdnsを使用することになっている.
    //      URLパラメータ:dnsはbase64エンコーディングされたドメイン名である.
    //      
    //      RFC8484では HTTPヘッダ(accept = application/dns-message)を指定することになっている.
    @GetMapping("/{dohInstanceName}")
    public byte[] handleDoHQuery(@PathVariable("dohInstanceName") String dohInstanceName, @RequestParam(name = "dns", required = true) String dnsQueryBase64) throws DNSServiceCommonException
    {

        // TODO :DoHインスタンスに応じた動的処理
        
        // dnsパラメータはbase64エンコーディングされているので平文に変換する.
        byte[] rawDNSRequest = Base64.getDecoder().decode(dnsQueryBase64);



        // TODO : レスポンスタイプは "application/dnsmessage" を使用する.
        // TODO : TTLは HTTPヘッダ　"cache-control = max-age=<秒数>"　で表現する.
        
        // レスポンスボディの作成
        // 
        // RFC 8484では7.1節でDNSメッセージのエンコーディングについて以下のとおり定義している.
        //
        //      Encoding considerations: This is a binary format. The contents are a
        //      DNS message as defined in RFC 1035. The format used here is for
        //      DNS over UDP, which is the format defined in the diagrams in RFC 1035.
        //
        // したがって、RFC 1035(DNSメッセージの仕様書)に従ってレスポンスメッセージを作成する必要がある.

        // DNS問い合わせメッセージを解析する.
        IDNSQuestionMessage dnsQuestionMessage = parseDNSQuestionMessageBytes(rawDNSRequest);


        // DNSサービスインスタンスを取得or新規生成する.
        // 本メソッドは、試験用のDoHのI/Fなので、デフォルトのDNSサービスインスタンスを使用する.
        IDNSServiceInstance dnsServiceInstance = getDefaultDNSServiceInstance();

        // DNSクエリを処理してレスポンスを生成する.
        IDNSResponseMessage dnsResponseMessage = dnsServiceInstance.serveDNSQuery(dnsQuestionMessage);


        byte[] rawDNSResponse = dnsResponseMessage.getDNSMessageBytes();



        // RFC 8484では使用するHTTPプロトコルを以下のように定義している.
        //      The responses may be processed and transported in any order using HTTP’s
        //      multi-streaming functionality (see Section 5 of [RFC7540]).
        // RFC 7540はHTTP/2の仕様書なので、HTTP/2でレスポンスを返却する能力が求められる.


        return rawDNSResponse;
    }


    protected IDNSServiceInstance getDNSServiceInstance(String userName, String serviceInstanceName) throws DNSServiceCommonException
    {
        IDNSServiceInstance dnsServiceInstance = dnsServiceInstanceFactory.getOrCreateDNSServiceInstance(userName, serviceInstanceName);
        return dnsServiceInstance;
    }

    /**
     * 試験用のデフォルトDNSサービスインスタンスを返却する.
     * 
     * @return
     * @throws DNSServiceCommonException
     */
    protected IDNSServiceInstance getDefaultDNSServiceInstance() throws DNSServiceCommonException
    {

        IDNSServiceInstance dnsServiceInstance = dnsServiceInstanceFactory.getOrCreateDefaultDNSServiceInstance();
        return dnsServiceInstance;
    }


    protected IDNSQuestionMessage parseDNSQuestionMessageBytes(byte[] rawDNSRequest) throws DNSServiceCommonException
    {
        IDNSQuestionMessage dnsQuestionMessage = dnsMessageFactory.createQuestionDNSMesssage(rawDNSRequest);

        return dnsQuestionMessage;
    }



}