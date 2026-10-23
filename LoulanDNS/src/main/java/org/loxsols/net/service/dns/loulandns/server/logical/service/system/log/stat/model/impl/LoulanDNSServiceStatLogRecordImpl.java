package org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.stat.model.impl;

import static org.mockito.Mockito.doAnswer;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;
import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSProtocolUtils;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;
import org.loxsols.net.service.dns.loulandns.server.http.spring.model.CommonSystemLog;
import org.loxsols.net.service.dns.loulandns.server.logical.model.CommonSystemLogInfo;
import org.loxsols.net.service.dns.loulandns.server.logical.model.CommonSystemLogPropertiesInfo;
import org.loxsols.net.service.dns.loulandns.server.logical.model.DNSServiceInstanceInfo;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSQuestionMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSResourceRecord;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.IDNSQueryPart;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.stat.model.ILoulanDNSServiceStatLogRecord;
import org.xbill.DNS.Zone;

/**
 * DNSサービス層の統計データのモデルクラスの実装クラス
 * CommmonSystemLogInfo論理クラスを継承して実装する.
 * DB層のCommonSystemLogレコードを利用してデータを記録する.
 * 
 * LoulanDNSServiceStatLogRecordImpl
 */
public class LoulanDNSServiceStatLogRecordImpl extends CommonSystemLogInfo implements ILoulanDNSServiceStatLogRecord
{

    public static final String CONST_PROP_KEY_USER_NAME = LoulanDNSConstants.PROP_KEY_USER_NAME;
    public static final String CONST_PROP_KEY_DNS_SERVICE_NAME = LoulanDNSConstants.PROP_KEY_SERVICE_INSTANCE_NAME;

    public static final String CONST_PROP_KEY_DNS_QUESTION_MESSAGE_WIRED_GZIP = "dns.message.question.wired.gzip";
    public static final String CONST_PROP_KEY_DNS_RESPONSE_MESSAGE_WIRED_GZIP = "dns.message.response.wired.gzip";

    public static final String CONST_PROP_KEY_DNS_QUESTION_MESSAGE_QNAME = "dns.message.question.qname";
    public static final String CONST_PROP_KEY_DNS_QUESTION_MESSAGE_QTYPE = "dns.message.question.qtype";
    public static final String CONST_PROP_KEY_DNS_QUESTION_MESSAGE_QCLASS = "dns.message.question.qclass";
    public static final String CONST_PROP_KEY_DNS_QUESTION_MESSAGE_QUERY_ID = "dns.message.question.query-id";

    public static final String CONST_PROP_KEY_DNS_RESPONSE_MESSAGE_RNAME = "dns.message.response.rname";
    public static final String CONST_PROP_KEY_DNS_RESPONSE_MESSAGE_RTYPE= "dns.message.response.rtype";
    public static final String CONST_PROP_KEY_DNS_RESPONSE_MESSAGE_RCLASS = "dns.message.response.rclass";

    // DNSレスポンス中のRRのインデックス番号(独自実装) : 回答セクション中のRRの格納順位を0-Nで表現する.
    public static final String CONST_PROP_KEY_DNS_RESPONSE_MESSAGE_RR_INDEX = "dns.message.response.rr-index";

    // DNS統計情報として、処理開始時刻を設定する.
    public static final String CONST_PROP_KEY_STAT_DNS_SERVICE_PROCESS_START_TIME = "stat.dns.service.process.start.time";
    
    // DNS統計情報として、処理終了時刻を設定する.
    public static final String CONST_PROP_KEY_STAT_DNS_SERVICE_PROCESS_END_TIME = "stat.dns.service.process.end.time";

    // DNS統計情報として、処理に要した時間(ms単位)を設定する.
    public static final String CONST_PROP_KEY_STAT_DNS_SERVICE_PROCESS_TIME_MSEC = "stat.dns.service.process.time.msec";





    @JsonIgnore
    static LoulanDNSProtocolUtils protocolUtils = new LoulanDNSProtocolUtils();

    @JsonIgnore
    LoulanDNSUtils loulanDNSUtils = new LoulanDNSUtils();

    
    /**
     * コンストラクタ
     * 
     * @param userName
     * @param dnsServiceInstanceName
     * @param questionMessage
     * @param responseMessage
     * @param options
     * @param loggingDateTime
     * @throws LoulanDNSSystemServiceException
     */
    public LoulanDNSServiceStatLogRecordImpl(String userName, String dnsServiceInstanceName, IDNSQuestionMessage questionMessage, IDNSResponseMessage responseMessage, String[] options, ZonedDateTime loggingDateTime) throws LoulanDNSSystemServiceException
    {
        init(userName, dnsServiceInstanceName, questionMessage, responseMessage, options, loggingDateTime);
    }


    public LoulanDNSServiceStatLogRecordImpl(String userName, String dnsServiceInstanceName, IDNSQuestionMessage questionMessage, IDNSResponseMessage responseMessage, String[] options, ZonedDateTime loggingDateTime, ZonedDateTime starDateTime, ZonedDateTime endDateTime) throws LoulanDNSSystemServiceException
    {
        init(userName, dnsServiceInstanceName, questionMessage, responseMessage, options, loggingDateTime, starDateTime, endDateTime);
    }



    /**
     * コンストラクタ.
     * 既存のCommonSystemLogInfoレコードから本クラスのインスタンスを生成する.
     * 
     * 
     * @param baseInfo
     * @throws LoulanDNSSystemServiceException
     */
    public LoulanDNSServiceStatLogRecordImpl(CommonSystemLogInfo baseInfo) throws LoulanDNSSystemServiceException
    {
        setCommonSystemLogID( baseInfo.getCommonSystemLogID() );
        setCommonSystemLogOccurDate( baseInfo.getCommonSystemLogOccurDate() );
        setCommonSystemLogOption1( baseInfo.getCommonSystemLogOption1() );
        setCommonSystemLogOption2( baseInfo.getCommonSystemLogOption2() );
        setCommonSystemLogOption3( baseInfo.getCommonSystemLogOption3() );
        setCommonSystemLogPropertiesInfoList( baseInfo.getCommonSystemLogPropertiesInfoList() );
        setCommonSystemLogRecord( baseInfo.getCommonSystemLogRecord()); 
        setCommonSystemLogTag( baseInfo.getCommonSystemLogTag() );

        setCreateDate( baseInfo.getCreateDate()  );
        setUpdateDate( baseInfo.getUpdateDate()  );
    } 




    /**
     * 初期化メソッド.
     * 
     * @param userName
     * @param dnsServiceInstanceName
     * @param questionMessage
     * @param responseMessage
     * @param options
     * @param loggingDateTime
     * @throws LoulanDNSSystemServiceException
     */
    protected void init(String userName, String dnsServiceInstanceName, IDNSQuestionMessage questionMessage, IDNSResponseMessage responseMessage, String[] options, ZonedDateTime loggingDateTime) throws LoulanDNSSystemServiceException
    {

        setTag(userName, dnsServiceInstanceName);
        setLogRecordValue(responseMessage);

        setUserName(userName);
        setDNSServiceInstanceName(dnsServiceInstanceName);
        
        setQuestionMessage(questionMessage);
        setResponseMessage(responseMessage);

        setOptions(options);

        setLoggingDateTime(loggingDateTime);

        // 現在時刻をcreateDateとupdateDateに設定.
        String currentDate = loulanDNSUtils.getCurrentDateTimeString();
        setCreateDate(currentDate);
        setUpdateDate(currentDate);

    }



    protected void init(String userName, String dnsServiceInstanceName, IDNSQuestionMessage questionMessage, IDNSResponseMessage responseMessage, String[] options, ZonedDateTime loggingDateTime, ZonedDateTime startDateTime, ZonedDateTime endDateTime) throws LoulanDNSSystemServiceException
    {

        setTag(userName, dnsServiceInstanceName);
        setLogRecordValue(responseMessage);

        setUserName(userName);
        setDNSServiceInstanceName(dnsServiceInstanceName);
        
        setQuestionMessage(questionMessage);
        setResponseMessage(responseMessage);

        setOptions(options);
        setLoggingDateTime(loggingDateTime);

        setStartDateTime(startDateTime);
        setEndDateTime(endDateTime);

        setProcessTime(startDateTime, endDateTime);


        // 現在時刻をcreateDateとupdateDateに設定.
        String currentDate = loulanDNSUtils.getCurrentDateTimeString();
        setCreateDate(currentDate);
        setUpdateDate(currentDate);

    }



   /**
     * DNSサービスインスタンスの所属ユーザー名を取得する.
     * 
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public String getUserName() throws LoulanDNSSystemServiceException
    {
        String userName = getProperty(CONST_PROP_KEY_USER_NAME);
        return userName;
    }


    /**
     * DNSサービスインスタンスの所属ユーザー名を設定する.
     * 
     * @param userName
     * @throws LoulanDNSSystemServiceException
     */
    public void setUserName(String userName) throws LoulanDNSSystemServiceException
    {
        setProperty(CONST_PROP_KEY_USER_NAME, userName);
    }

    /**
     * DNSサービスインスタンスの名前を取得する.
     * 
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public String getDNSServiceInstanceName() throws LoulanDNSSystemServiceException
    {
        String dnsServiceInstanceName = getProperty(CONST_PROP_KEY_DNS_SERVICE_NAME);
        return dnsServiceInstanceName;
    }


    public void setDNSServiceInstanceName(String dnsServiceInstanceName) throws LoulanDNSSystemServiceException
    {
        setProperty(CONST_PROP_KEY_DNS_SERVICE_NAME, dnsServiceInstanceName );
    }


    /**
     * DNS問い合わせメッセージを取得する.
     * 本実装クラスでは、DNS問い合わせメッセージはプロパティ(key="CONST_PROP_KEY_DNS_QUESTION_MESSAGE_GZIP_WIRED")に設定する.
     * 本実装クラスでは、DNS問い合わせメッセージ(wired表現)は、GZip圧縮したうえで、Base64文字列化して格納する.
     * 
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    @JsonIgnore
    public IDNSQuestionMessage getQuestionMessage() throws LoulanDNSSystemServiceException
    {
        String base64GZipWiredMessage = getProperty(CONST_PROP_KEY_DNS_QUESTION_MESSAGE_WIRED_GZIP);

        IDNSMessage dnsMessage;
        try
        {
            byte[] gzipBytes = protocolUtils.base64StringToBytes(base64GZipWiredMessage);
            byte[] wiredMessage = loulanDNSUtils.decodeGZIP(gzipBytes);
            
            dnsMessage = protocolUtils.parseDNSMessageBytes(wiredMessage);
        }
        catch(DNSServiceCommonException cause)
        {
            // このDNSメッセージは問い合わせメッセージではない.
            String msg = String.format("Failed to convert base64 String to IDNSQuestionMessage. base64GZipWiredMessage=%s", base64GZipWiredMessage );
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg, cause);
            throw exception;
        }

        if ( dnsMessage instanceof IDNSQuestionMessage == false )
        {
            // このDNSメッセージは問い合わせメッセージではない.
            String msg = String.format("Failed to convert base64 String to IDNSQuestionMessage. base64GZipWiredMessage=%s, dnsMessage=%s", base64GZipWiredMessage, dnsMessage.toString() );
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg);
            throw exception;
        }

        return (IDNSQuestionMessage)dnsMessage;
    }


    /**
     * DNS問い合わせメッセージを設定する.
     * 本実装クラスでは、DNS問い合わせメッセージはプロパティ(key="CONST_PROP_KEY_DNS_QUESTION_MESSAGE_GZIP_WIRED")に設定する.
     * 本実装クラスでは、DNS問い合わせメッセージ(wired表現)は、GZip圧縮したうえで、Base64文字列化して格納する.
     * 
     * 
     * @param questionMessage
     * @throws LoulanDNSSystemServiceException
     */
    public void setQuestionMessage(IDNSQuestionMessage questionMessage) throws LoulanDNSSystemServiceException
    {
        try
        {
            // DNSクエリのwireを取得して、基底クラスのcommonSystemLogOption1にbase64文字列として設定する
            byte[] wiredMessage = questionMessage.getDNSMessageBytes();
            byte[] gzipBytes = loulanDNSUtils.encodeGZIP(wiredMessage);

            String base64GZipMessage = protocolUtils.toBase64String(gzipBytes);

            setProperty(CONST_PROP_KEY_DNS_QUESTION_MESSAGE_WIRED_GZIP, base64GZipMessage);
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to set DNSQuestionMessage to StatRecord. questionMessage=%s", questionMessage.toString() );
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg, cause);
            throw exception;
        }


        try
        {
            // ----- 以下のDNSクエリのメタ情報を設定する.
            // - クエリID
            // - QNAME
            // - QTYPE
            // - QCLASS

            // クエリIDを設定
            int queryID = questionMessage.getDNSHeaderSection().getID();
            setProperty(CONST_PROP_KEY_DNS_QUESTION_MESSAGE_QUERY_ID, Integer.toString(queryID) );

            IDNSQueryPart dnsQuery = questionMessage.getDNSQuestionSection().getDNSQueries()[0];

            // QNAMEを設定
            String qname = dnsQuery.getDNSQueryName();
            setProperty(CONST_PROP_KEY_DNS_QUESTION_MESSAGE_QNAME, qname);

            // QTYPEを設定
            String qtype = protocolUtils.toDNSRRTypeString( dnsQuery.getDNSQueryType() );
            setProperty(CONST_PROP_KEY_DNS_QUESTION_MESSAGE_QTYPE, qtype);

            // QCLASSを設定
            String qclass = protocolUtils.toDNSClassValueString( dnsQuery.getDNSQueryClass() );
            setProperty(CONST_PROP_KEY_DNS_QUESTION_MESSAGE_QCLASS, qclass);

        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to build DNSServiceStatLogRecord.");
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg, cause);
            throw exception;
        }

    }


    /**
     * DNS回答メッセージを取得する.
     * 本実装クラスでは、DNSレスポンスメッセージはプロパティ(key="CONST_PROP_KEY_DNS_RESPONSE_MESSAGE_GZIP_WIRED")に設定する.
     * 
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    @JsonIgnore
    public IDNSResponseMessage getResponseMessage() throws LoulanDNSSystemServiceException
    {

        String base64GZipWiredMessage = getProperty(CONST_PROP_KEY_DNS_RESPONSE_MESSAGE_WIRED_GZIP);

        IDNSMessage dnsMessage;
        try
        {
            byte[] gzipBytes = protocolUtils.base64StringToBytes(base64GZipWiredMessage);
            byte[] wiredMessage = loulanDNSUtils.decodeGZIP(gzipBytes);

            dnsMessage = protocolUtils.parseDNSMessageBytes(wiredMessage);
        }
        catch(DNSServiceCommonException cause)
        {
            // このbyte配列はDNSメッセージではない.
            String msg = String.format("Failed to convert base64 String to IDNSResponseMessage. base64GZipWiredMessage=%s", base64GZipWiredMessage );
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg, cause);
            throw exception;
        }

        if ( dnsMessage instanceof IDNSResponseMessage == false )
        {
            // このDNSメッセージは問い合わせメッセージではない.
            String msg = String.format("Failed to convert base64 String to IDNSResponseMessage. base64GZipWiredMessage=%s, dnsMessage=%s", base64GZipWiredMessage, dnsMessage.toString() );
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg);
            throw exception;
        }

        return (IDNSResponseMessage)dnsMessage;
    }


    /**
     * DNSレスポンスメッセージを設定する.
     * 本実装クラスでは、DNSレスポンスメッセージはプロパティ(key="CONST_PROP_KEY_DNS_RESPONSE_MESSAGE_GZIP_WIRED")に設定する.
     * 
     * @param responseMessage
     * @throws LoulanDNSSystemServiceException
     */
    public void setResponseMessage(IDNSResponseMessage responseMessage) throws LoulanDNSSystemServiceException
    {
        try
        {
            // DNSレスポンスのwired表現を取得して、GZIP圧縮したBase64文字列として、基底クラスのcommonSystemLogOption2に文字列として設定する
            byte[] wiredMessage = responseMessage.getDNSMessageBytes();
            byte[] gzipBytes = loulanDNSUtils.encodeGZIP(wiredMessage);
            String base64GZipWiredMessage = protocolUtils.toBase64String(gzipBytes);

            setProperty(CONST_PROP_KEY_DNS_RESPONSE_MESSAGE_WIRED_GZIP, base64GZipWiredMessage);
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to set DNSResponseMessage to StatRecord. responseMessage=%s", responseMessage.toString() );
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg, cause);
            throw exception;
        }


        try
        {
            // ----- 以下のDNSレスポンスのメタ情報を設定する.
            // - RRのインデックス番号(RRレコードの格納順位を0-Nで表現した数値. 本システムの独自設計値.)
            // - RNAME
            // - RTYPE
            // - RCLASS


            // DBのユニークキー制約がkeyに係っているから、"プロパティキー:rrIndex"で格納する.
            for( int rrIndex=0; rrIndex < responseMessage.getDNSAnswerSection().getDNSRRCount(); rrIndex++ )
            {
                // RRインデックス値を設定
                // DNSレスポンス中のRRのインデックス番号(独自実装) : 回答セクション中のRRの格納順位を0-Nで表現する.
                setProperty( String.format("%s:%d", CONST_PROP_KEY_DNS_RESPONSE_MESSAGE_RR_INDEX, rrIndex), Integer.toString(rrIndex) );

                IDNSResourceRecord rr = responseMessage.getDNSAnswerSection().getDNSResourceRecords()[rrIndex];

                // RNAMEを設定
                String rname = rr.getDNSResourceName();
                setProperty( String.format("%s:%d", CONST_PROP_KEY_DNS_RESPONSE_MESSAGE_RNAME, rrIndex), rname);

                // RTYPEを設定
                String rtype = protocolUtils.toDNSRRTypeString( rr.getResourceType() );
                setProperty( String.format("%s:%d", CONST_PROP_KEY_DNS_RESPONSE_MESSAGE_RTYPE, rrIndex), rtype);

                // RCLASSを設定
                String rclass = protocolUtils.toDNSClassValueString( rr.getResourceClass() );
                setProperty( String.format("%s:%d", CONST_PROP_KEY_DNS_RESPONSE_MESSAGE_RCLASS, rrIndex), rclass);

            }

        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to build DNSServiceStatLogRecord.");
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg, cause);
            throw exception;
        }


    }



    /**
     * オプション情報を取得する.
     * 
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public String[] getOptions() throws LoulanDNSSystemServiceException
    {
        String[] options = new String[3];

        options[0] = getCommonSystemLogOption1();
        options[1] = getCommonSystemLogOption2();
        options[2] = getCommonSystemLogOption3();
 
        return options;
    }


    
    /**
     * オプション情報を設定する.
     * 
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public void setOptions(String[] options) throws LoulanDNSSystemServiceException
    {

        if ( options.length > 3 )
        {
            // 指定できるオプション個数の最大個数は3個までとする.
            String msg = String.format("Failed to set options to LoulanDNSServiceStatLogRecord. Over the max-size(%d) of options.", 3);
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg);
            throw exception;
        }

        String option1, option2, option3;
        option1 = option2 = option3 = null;

        if (options.length >= 1 )
        {
            option1 = options[0];
        }

        if (options.length >= 2 )
        {
            option2 = options[1];
        }

        if (options.length >= 3 )
        {
            option3 = options[2];
        }

        setCommonSystemLogOption1(option1);
        setCommonSystemLogOption2(option2);
        setCommonSystemLogOption3(option3);

        return ;
    }


    /**
     * 本統計情報が記録された日時を取得する.
     * 
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public String getLoggingDateTime() throws LoulanDNSSystemServiceException
    {
        // 本実装クラスでは、基底クラスのcommonSystemLogOccurDateを利用して記録日時を保持する.
        ZonedDateTime dateTime = getCommonSystemLogOccurDate();
        String dateTimeString = LoulanDNSUtils.toDateTimeString(dateTime);

        return dateTimeString;
    }

    /**
     * 本統計情報が記録された日時を設定する.
     * 
     * @param dateTime
     * @throws LoulanDNSSystemServiceException
     */
    public void setLoggingDateTime(ZonedDateTime dateTime) throws LoulanDNSSystemServiceException
    {
        // 本実装クラスでは、基底クラスのcommonSystemLogOccurDateを利用して記録日時を保持する.
        setCommonSystemLogOccurDate(dateTime);
    }

    
    /**
     * 本統計情報の対象処理の開始時刻を設定する.
     * 
     * @param startDateTime
     * @throws LoulanDNSSystemServiceException
     */
    public void setStartDateTime(ZonedDateTime startDateTime) throws LoulanDNSSystemServiceException
    {
        String startDateTimeString = loulanDNSUtils.toDateTimeString(startDateTime);
        setProperty(CONST_PROP_KEY_STAT_DNS_SERVICE_PROCESS_START_TIME, startDateTimeString);
    }


    /**
     * 本統計情報の対象処理の終了時刻を設定する.
     * 
     * @param endDateTime
     * @throws LoulanDNSSystemServiceException
     */
    public void setEndDateTime(ZonedDateTime endDateTime) throws LoulanDNSSystemServiceException
    {
        String endDateTimeString = loulanDNSUtils.toDateTimeString(endDateTime);
        setProperty(CONST_PROP_KEY_STAT_DNS_SERVICE_PROCESS_END_TIME, endDateTimeString);
    }


    /**
     * 本統計情報の対象処理に要した時間をを設定する.
     * 
     * @param startDateTime
     * @param endDateTime
     * @throws LoulanDNSSystemServiceException
     */
    public void setProcessTime(ZonedDateTime startDateTime, ZonedDateTime endDateTime) throws LoulanDNSSystemServiceException
    {
        // startDateTime から endDateTime までのミリ秒差分を計算
        long processMSec = ChronoUnit.MILLIS.between(startDateTime, endDateTime);

        setProperty(CONST_PROP_KEY_STAT_DNS_SERVICE_PROCESS_TIME_MSEC, Long.toString(processMSec));
    }



    /**
     * 本統計情報に付随するプロパティ情報を設定する.
     * 
     * @param key
     * @param value
     * @throws LoulanDNSSystemServiceException
     */
    public void setProperty(String key, String value) throws LoulanDNSSystemServiceException
    {
        CommonSystemLogPropertiesInfo proertyInfo = new CommonSystemLogPropertiesInfo();
        proertyInfo.setCommonSystemLogID( getCommonSystemLogID() );

        proertyInfo.setCommonSystemLogPropertyKey(key);
        proertyInfo.setCommonSystemLogPropertyValue( value );

        String currentDate = loulanDNSUtils.getCurrentDateTimeString();
        proertyInfo.setCreateDate(currentDate);
        proertyInfo.setUpdateDate(currentDate);

        List<CommonSystemLogPropertiesInfo> properties = getCommonSystemLogPropertiesInfoList();
        if ( properties == null )
        {
            // 基底クラスのプロパティがnullなので追加する.
            properties = new ArrayList<CommonSystemLogPropertiesInfo>();
            setCommonSystemLogPropertiesInfoList(properties);
        }

        properties.add( proertyInfo );
    }


    /**
     * プロパティ情報(オプション値)を取得する.
     * 
     * @param key
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public String getProperty(String key) throws LoulanDNSSystemServiceException
    {
        List<CommonSystemLogPropertiesInfo> properties = getCommonSystemLogPropertiesInfoList();
        if ( properties == null )
        {
            return null;
        }

        CommonSystemLogPropertiesInfo propInfo = null;
        for(CommonSystemLogPropertiesInfo tmpPropInfo : properties)
        {
            if ( tmpPropInfo.getCommonSystemLogPropertyKey().equals(key) )
            {
                propInfo = tmpPropInfo;
                break;
            }
        }

        String value = null;
        if ( propInfo != null )
        {
            value = propInfo.getCommonSystemLogPropertyValue();
        }

        return value;
    }


    /**
     * タグ文字列を設定する.
     * 
     * @param userName
     * @param dnsServiceInstanceName
     * @throws LoulanDNSSystemServiceException
     */
    public void setTag(String userName, String dnsServiceInstanceName) throws LoulanDNSSystemServiceException
    {
        String tag = buildTagString(userName, dnsServiceInstanceName);
        setCommonSystemLogTag(tag);
    }

    /**
     * ログレコードの値を設定する.
     * 本実装クラスでは、レスポンスメッセージの文字列表現を設定する.
     * 
     * @param responseMessage
     * @throws LoulanDNSSystemServiceException
     */
    public void setLogRecordValue(IDNSResponseMessage responseMessage) throws LoulanDNSSystemServiceException
    {
        String value = responseMessage.toString();
        setCommonSystemLogRecord(value);
    }



    /**
     * CommonSystemLogレコードに登録する際のタグ文字列を生成する.
     * 
     * @param userName
     * @param dnsServiceInstanceName
     * @param questionMessage
     * @param loggingDateTime
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    @JsonIgnore
    public static String buildTagString(String userName, String dnsServiceInstanceName) throws LoulanDNSSystemServiceException
    {       
        
            // ----- タグ文字列の仕様は以下の通り.
            // DNSServiceStatLog/<ユーザー名>/<サービス名>
            String tag = String.format("DNSServiceStatLog/%s/%s", userName, dnsServiceInstanceName);
            return tag;
    }




    /**
     * CommonSystemLogInfoクラスから、本実装クラスに変換して返す.
     * (2026/10/07 ベースとなるCommonSystemLogInfoクラスのメンバ変数をそのまま利用する方式に修正)
     * 
     * @param commonSystemLogInfo
     * @return
     */
    @JsonIgnore
    public static ILoulanDNSServiceStatLogRecord fromCommonSystemLogInfo(CommonSystemLogInfo commonSystemLogInfo) throws LoulanDNSSystemServiceException
    {
        ILoulanDNSServiceStatLogRecord statLogRecord = new LoulanDNSServiceStatLogRecordImpl( commonSystemLogInfo );
        return statLogRecord;
    }

}