package org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.stat.impl;

import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;
import org.loxsols.net.service.dns.loulandns.server.http.spring.model.CommonSystemLog;
import org.loxsols.net.service.dns.loulandns.server.http.spring.service.LoulanDNSLogicalDBService;
import org.loxsols.net.service.dns.loulandns.server.logical.model.CommonSystemLogInfo;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSQuestionMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSAnswerSection;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.IDNSServiceInstance;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.stat.model.ILoulanDNSServiceStatLogRecord;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.stat.model.impl.LoulanDNSServiceStatLogRecordImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.stat.ILoulanDNSStatLogService;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import android.hardware.soundtrigger.V2_3.ISoundTriggerHw.queryParameterCallback;


/**
 * LoulanDNSの統計情報入出力実装クラス
 * 
 */
public class LoulanDNSStatLogServiceImpl implements  ILoulanDNSStatLogService
{

    LoulanDNSUtils loulanDNSUtils = new LoulanDNSUtils();

    LoulanDNSLogicalDBService logicalDBService;

    String dnsStatLogServiceName;
    Properties options;

    @Autowired
    @Qualifier("loulanDNSLogicalDBServiceImpl")
    public void setLogicalDBService(LoulanDNSLogicalDBService instance)
    {
        this.logicalDBService = instance;
    }


    public String getDNSStatLogServiceName() throws LoulanDNSSystemServiceException
    {
        return this.dnsStatLogServiceName;
    }

    protected void setDNSStatLogServiceName(String value) throws LoulanDNSSystemServiceException
    {
        this.dnsStatLogServiceName = value;
    }


    protected void setOptions(Properties value) throws LoulanDNSSystemServiceException
    {
        this.options = value;
    }
    


    /**
     * コンストラクタ
     * 
     * @param dnsStatLogServiceName
     * @param opitons
     * @throws LoulanDNSSystemServiceException
     */
    public LoulanDNSStatLogServiceImpl(String dnsStatLogServiceName, Properties opitons) throws LoulanDNSSystemServiceException
    {
        setDNSStatLogServiceName(dnsStatLogServiceName);
        setOptions(opitons);
    }


    /**
     * DNSサービスインスタンス層における名前解決処理の統計情報を出力する.
     * 
     * @param userName
     * @param dnsServiceInstanceName
     * @param dnsQuestionMessage
     * @param dnsResponseMessage
     * @param options
     * @throws LoulanDNSSystemServiceException
     */
    public void saveDNSServiceStatLogRecord(String userName, String dnsServiceInstanceName, IDNSQuestionMessage dnsQuestionMessage, IDNSResponseMessage dnsResponseMessage, String[] options) throws LoulanDNSSystemServiceException
    {
        ZonedDateTime loggingDateTime = loulanDNSUtils.getCurrentZonedDateTime();
        ILoulanDNSServiceStatLogRecord statLogRecord = new LoulanDNSServiceStatLogRecordImpl(userName, dnsServiceInstanceName, dnsQuestionMessage, dnsResponseMessage, options, loggingDateTime);
        
        logicalDBService.saveCommonSystemLogInfo( (CommonSystemLogInfo)statLogRecord);
    }


    /**
     * DNSサービスインスタンス層における名前解決処理の統計情報を出力する.
     * 付加情報として処理開始時刻と、処理終了時刻を付加する.
     * 
     * @param userName
     * @param dnsServiceInstanceName
     * @param dnsQuestionMessage
     * @param dnsResponseMessage
     * @param options
     * @throws LoulanDNSSystemServiceException
     */
    public void saveDNSServiceStatLogRecord(String userName, String dnsServiceInstanceName, IDNSQuestionMessage dnsQuestionMessage, IDNSResponseMessage dnsResponseMessage, String[] options, ZonedDateTime startDateTime, ZonedDateTime endDateTime) throws LoulanDNSSystemServiceException
    {
        ZonedDateTime loggingDateTime = loulanDNSUtils.getCurrentZonedDateTime();
        ILoulanDNSServiceStatLogRecord statLogRecord = new LoulanDNSServiceStatLogRecordImpl(userName, dnsServiceInstanceName, dnsQuestionMessage, dnsResponseMessage, options, loggingDateTime, startDateTime, endDateTime);
        
        logicalDBService.saveCommonSystemLogInfo( (CommonSystemLogInfo)statLogRecord);
    }



    /**
     * DNSサービスインスタンス層における名前解決処理の統計情報を取得する.
     * 
     * @param userName
     * @param dnsServiceInstanceName
     * 
     * 
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public List<ILoulanDNSServiceStatLogRecord> getDNSServiceStatLogRecords(String userName, String dnsServiceInstanceName, ZonedDateTime fromDateTime, ZonedDateTime untilDateTime, int maxSize ) throws LoulanDNSSystemServiceException
    {

        String tag = LoulanDNSServiceStatLogRecordImpl.buildTagString(userName, dnsServiceInstanceName);
        List<CommonSystemLogInfo> list = logicalDBService.getCommonSystemLogInfoListByTag(tag, fromDateTime, untilDateTime, maxSize);

        List<ILoulanDNSServiceStatLogRecord> statLogRecordList = new ArrayList<ILoulanDNSServiceStatLogRecord>();
        for(CommonSystemLogInfo logInfo : list)
        {
            ILoulanDNSServiceStatLogRecord statLogRecord = LoulanDNSServiceStatLogRecordImpl.fromCommonSystemLogInfo(logInfo);
            statLogRecordList.add( statLogRecord );
        }

        return statLogRecordList;
    }

    
}


