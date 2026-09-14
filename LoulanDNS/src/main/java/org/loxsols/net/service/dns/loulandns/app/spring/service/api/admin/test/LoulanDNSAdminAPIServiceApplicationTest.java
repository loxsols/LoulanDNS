package org.loxsols.net.service.dns.loulandns.app.spring.service.api.admin.test;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.util.test.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import android.icu.impl.Assert;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.util.Arrays;
import java.util.Properties;
import java.net.UnknownHostException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.MalformedDNSRequestException;
import org.loxsols.net.service.dns.loulandns.server.common.constants.DNSProtocolConstants;
import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.DNSQueryPartImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.DNSAnswerSectionImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.DNSHeaderSectionImpl;


import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.IDNSQueryPart;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSQuestionMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSAnswerSection;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSResourceRecord;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSHeaderSection;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSQuestionSection;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSDebugUtils;
import org.loxsols.net.service.dns.loulandns.server.http.spring.test.LoulanDNSSSpringTestTargeImpltBase;
import org.loxsols.net.service.dns.loulandns.server.http.spring.test.LoulanDNSSSpringTestTarget;
import org.loxsols.net.service.dns.loulandns.app.spring.service.endpoint.general.LoulanDNSEndpointServiceApplication;
import org.loxsols.net.service.dns.loulandns.client.IDNSLookupClient;
import org.loxsols.net.service.dns.loulandns.client.common.DNSClientCommonException;
import org.loxsols.net.service.dns.loulandns.client.common.LoulanDNSClientConstants;
import org.loxsols.net.service.dns.loulandns.client.impl.simple.SimpleUDPResolverImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.model.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.IDNSProtocolModelInstanceFactory;

import org.loxsols.net.service.dns.loulandns.app.spring.service.api.admin.*;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;


/*
 * LoulanDNSAdminAPIServiceApplicationの試験クラス
 * 
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Component
public class LoulanDNSAdminAPIServiceApplicationTest extends LoulanDNSSSpringTestTargeImpltBase implements LoulanDNSSSpringTestTarget
{

    private String apiServiceHost = "localhost";
    private int apiServicePort = 8080;

    // 共通のHttpClientを作成（再利用可能）
    private final HttpClient client = HttpClient.newHttpClient();


    // [試験実行コマンド]
    // ```
    // >bin\test\junit\spring\exec-spring-junit4-test.bat METHOD org.loxsols.net.service.dns.loulandns.app.spring.service.endpoint.test.LoulanDNSEndpointServiceApplicationTest#testUDPServiceEndpoint001
    // ```
    /*
     * リゾルバ管理APIの疎通確認試験
     * 		# 試験内容
     *          - 基本的なリゾルバインスタンスの設定ができること
     *
     *		# 期待結果
     *			-基本的なリゾルバインスタンスの設定ができること
     * 
     */
    @Test
    public void testCreateResolverInstanceTest001() throws DNSClientCommonException
    {

        String cmdLine = "";
        String[] args = cmdLine.split(" ");

        // AdminAPIServiceの起動
        LoulanDNSAdminAPIServiceApplication.main(args);


        // リゾルバインスタンスの生成 : 以下のリゾルバインスタンスを生成する.

            // [テスト用リゾルバインスタンス(junit-test-resolver-001)の作成]
            // ```
            // >bin\test\api\admin\create-dns-resolver-instance.bat 8080 admin password admin junit-test-resolver-001 test-explain 201 101 test-memo
            // ```

            // [テスト用リゾルバインスタンス(test-resolver-01)のプロパティ情報の作成]
            // ```
            // >bin\test\api\admin\put-dns-resolver-instance-property.bat 8080 admin password admin test-resolver-01 loulan.dns.resolver.outbound.server.host.primary 1.1.1.1
            // >bin\test\api\admin\put-dns-resolver-instance-property.bat 8080 admin password admin test-resolver-01 loulan.dns.resolver.outbound.server.port.primary 53
            // ```

            // [テストサービス(test-service-udp)の作成]
            // ```
            // >bin\test\api\admin\create-dns-service-instance.bat 8080 admin password admin test-service-udp explain_test-service-udp 0 <UDP外部問い合わせするDNSリゾルバ(test-resolver-01)のID> 101 memo_test-service-udp
            // ```

            // [テストエンドポイント(test-endpoint-udp)の作成]
            // ```
            // >bin\test\api\admin\create-dns-service-endpoint-instance.bat 8080 admin password admin test-endpoint-udp test-explain 10101 test-service-udp 101 test-memo
            // ```

            // [テストエンドポイントプロパティ(test-endpoint-udpのプロパティ)の作成]
            // ```
            // >bin\test\api\admin\put-dns-service-endpoint-instance-property.bat 8080 admin password admin test-endpoint-udp loulan.dns.user.name admin
            // >bin\test\api\admin\put-dns-service-endpoint-instance-property.bat 8080 admin password admin test-endpoint-udp loulan.dns.service.instance.name test-service-udp
            // >bin\test\api\admin\put-dns-service-endpoint-instance-property.bat 8080 admin password admin test-endpoint-udp loulan.dns.service.endpoint.udp.address 0.0.0.0
            // >bin\test\api\admin\put-dns-service-endpoint-instance-property.bat 8080 admin password admin test-endpoint-udp loulan.dns.service.endpoint.udp.port 50053
            // ```


        

        // エンドポイントが起動するまで待機.
        // とりあえず、10秒待機する.
        // TODO : APPが起動したかを外部から動的に判定する処理.
        try
        {
            Thread.sleep(10000);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


        // call %CURL_CMD% -X PUT localhost:8080/admin/api/dns/service/create/dns-resolver-instance -u %LOGIN_USER_NAME%:%LOGIN_PASSWORD% -d "UserName=%TARGET_USER%" -d "DNSResolverInstanceName=%RESOLVER_INSTANCE_NAME%" -d "DNSResolverInstanceExplain=%EXPLAIN%" -d "DNSResolverTypeCode=%RESOLVER_TYPE_CODE%" -d "RecordStatus=%RECORD_STATUS%" -d "Memo=%MEMO%" 
        URI uri = URI.create( String.format("http://%s:%d/admin/api/dns/service/create/dns-resolver-instance", apiServiceHost, apiServicePort ) );


        String userName = "admin";
        String dnsResolverInstanceName = "junit-test-resolver-001";
        String dnsResolverInstanceExplain = "junit-test-resolver-001_explain";
        String dnsResolverTypeCode = "201";
        String recordStatus = "101";
        String memo = "junit-test-resolver-001_memo";

        // -u %LOGIN_USER_NAME%:%LOGIN_PASSWORD% 
        // -d "UserName=%TARGET_USER%" 
        // -d "DNSResolverInstanceName=%RESOLVER_INSTANCE_NAME%" 
        // -d "DNSResolverInstanceExplain=%EXPLAIN%" 
        // -d "DNSResolverTypeCode=%RESOLVER_TYPE_CODE%" 
        // -d "RecordStatus=%RECORD_STATUS%" 
        // -d "Memo=%MEMO%"
        String formData = 
            String.format(  "UserName=%s&" + 
                            "DNSResolverInstanceName=%s&" + 
                            "DNSResolverInstanceExplain=%s&" + 
                            "DNSResolverTypeCode=%s&" + 
                            "RecordStatus=%s&" + 
                            "Memo=%s",
                            userName,
                            dnsResolverInstanceName,
                            dnsResolverInstanceExplain,
                            dnsResolverTypeCode,
                            recordStatus,
                            memo );

        BodyPublisher body = HttpRequest.BodyPublishers.ofString(formData);

        HttpRequest request = HttpRequest.newBuilder()
                .uri( uri )
                .header("Accept", "application/json")
                .PUT(body)
                .build();


        HttpResponse<String> response;
        
        try
        {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        catch(IOException | InterruptedException exception)
        {
            exception.printStackTrace();
            return ;
        }

        // For Debug.
        System.out.println(response.body());


        // TODO : Appを外部から動的に終了する処理.
        

    }

    
}