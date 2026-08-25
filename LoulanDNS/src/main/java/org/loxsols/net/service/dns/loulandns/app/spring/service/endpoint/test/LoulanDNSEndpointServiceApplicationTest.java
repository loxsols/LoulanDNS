package org.loxsols.net.service.dns.loulandns.app.spring.service.endpoint.test;

import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.util.test.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import android.icu.impl.Assert;

import static org.junit.Assert.*;

import java.net.InetAddress;
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



/*
 * LoulanDNSEndpointServiceApplicationの試験クラス
 * 
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class LoulanDNSEndpointServiceApplicationTest extends LoulanDNSSSpringTestTargeImpltBase implements LoulanDNSSSpringTestTarget
{

    DNSMessageTestUtils dnsMessageTestUtils = new DNSMessageTestUtils();





    // [試験実行コマンド]
    // ```
    // >bin\test\junit\spring\exec-spring-junit4-test.bat METHOD org.loxsols.net.service.dns.loulandns.app.spring.service.endpoint.test.LoulanDNSEndpointServiceApplicationTest#testUDPServiceEndpoint001
    // ```
    /*
     * UDPサービスエンドポイントの疎通確認試験
     * 		# 試験内容
     *          - UDPサービスエンドポイントをDBモードで起動する
     * 			- google.co.jpドメインに対する問い合わせクエリをUDPで受け付ける
     *          - 外部リゾルバ(1.1.1.1)に再帰問い合わせする
     *          - DNSレスポンスを返却する
     *
     *		# 期待結果
     *			DNS問い合わせが正常にできることを確認する.
     * 
     */
    @Test
    public void testUDPServiceEndpoint001() throws DNSClientCommonException
    {
        final String qname = "google.co.jp";
        final String addr = "127.0.0.1";
        final int port = 50053;

        String cmdLine = "-load database -su admin -sn test-service-udp -eu admin -en test-endpoint-udp";
        String[] args = cmdLine.split(" ");


        // TODO : DBにテスト用のインスタンスを生成する処理.

            // [テスト用リゾルバインスタンス(test-resolver-01)の作成]
            // ```
            // >bin\test\api\admin\create-dns-resolver-instance.bat 8080 admin password admin test-resolver-01 test-explain 201 101 test-memo
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



        // EndpoinServiceの起動
        LoulanDNSEndpointServiceApplication.main(args);
        

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



        // DNSクエリメッセージの作成
        IDNSQuestionMessage questionMessage = createDNSQuestionMessage(qname);


        // DNSクエリの送信.
        IDNSResponseMessage response = sendUDPMessage(addr, port, questionMessage);

        try
        {
            response.validate();
        }
        catch(DNSServiceCommonException exception)
        {
            exception.printStackTrace();
            fail();
        }


        try
        {
            // AnswerセクションのRRの個数が0以外か
            assertNotEquals(response.getDNSAnswerSection().getDNSRRCount(), 0);

            // Answerセクションの先頭RRのリソース名が、問い合わせメッセージのリソース名と同じか
            assertEquals(response.getDNSAnswerSection().getDNSResourceRecords()[0].getDNSResourceName(), questionMessage.getDNSQuestionSection().getDNSQueries()[0].getDNSQueryName() );
        }
        catch(DNSServiceCommonException exception)
        {
            exception.printStackTrace();
            fail();
        }

        // TODO : Appを外部から動的に終了する処理.
        

    }


    private IDNSQuestionMessage createDNSQuestionMessage(String qname) throws DNSClientCommonException
    {        
        int qtype = DNSProtocolConstants.DNS_RR_TYPE_A;
        int qclass = DNSProtocolConstants.DNS_CLASS_IN;

        byte[] messageBytes;
        try
        {
            messageBytes = dnsMessageTestUtils.createDNSQueryMessage(qname, qtype, qclass);
        }
        catch(MalformedDNSRequestException cause)
        {
            String msg = String.format("Failed to create DNS Question Message. qname=%s, qtype=%d, qclass=%d", qname, qtype, qclass);
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }


        IDNSQuestionMessage questionMessage;
        try
        {
            IDNSProtocolModelInstanceFactory factory = new SimpleDNSProtocolModelInstanceFactoryImpl();
            questionMessage = factory.createDNSQuestionMessageInstance();
            questionMessage.setDNSMessageBytes(messageBytes);
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to create DNS Question Message. qname=%s, qtype=%d, qclass=%d", qname, qtype, qclass);
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }

        return questionMessage;
    }



    private IDNSResponseMessage sendUDPMessage(String addr, int port, IDNSQuestionMessage questionMessage) throws DNSClientCommonException
    {
        IDNSLookupClient client;
        
        try
        {

            Properties properties = new Properties();
            properties.setProperty( LoulanDNSClientConstants.PROP_KEY_DNS_SERVER_ADDRESS, addr);
            properties.setProperty( LoulanDNSClientConstants.PROP_KEY_DNS_SERVER_PORT, Integer.toString(port) );

            client = new SimpleUDPResolverImpl();
            client.init(properties);

        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to send UDP message. questionMessage=%s", questionMessage.toString() );
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }


        
        IDNSResponseMessage response = client.resolve(questionMessage);

        return response;
    }





    
}