package org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.stat;

import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSQuestionMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSAnswerSection;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.IDNSServiceInstance;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.stat.model.ILoulanDNSServiceStatLogRecord;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * LoulanDNSの統計情報入出力I/F
 * 
 */
public interface ILoulanDNSStatLogService
{

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
    public void saveDNSServiceStatLogRecord(String userName, String dnsServiceInstanceName, IDNSQuestionMessage dnsQuestionMessage, IDNSResponseMessage dnsResponseMessage, String[] options, ZonedDateTime starDateTime, ZonedDateTime endDateTime) throws LoulanDNSSystemServiceException;


    /**
     * DNSサービスインスタンス層における名前解決処理の統計情報を取得する.
     * 
     * @param userName
     * @param dnsServiceInstanceName
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public List<ILoulanDNSServiceStatLogRecord> getDNSServiceStatLogRecords(String userName, String dnsServiceInstanceName, ZonedDateTime fromDateTime, ZonedDateTime untilDateTime, int maxSize) throws LoulanDNSSystemServiceException;

    
}


