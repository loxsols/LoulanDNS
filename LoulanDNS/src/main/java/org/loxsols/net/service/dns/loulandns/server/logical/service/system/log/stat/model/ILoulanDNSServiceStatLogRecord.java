package org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.stat.model;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;
import org.loxsols.net.service.dns.loulandns.server.logical.model.DNSServiceInstanceInfo;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSQuestionMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;

/**
 * DNSサービス層の統計データのモデルクラスのI/F
 * 
 * ILoulanDNSServiceStatLogRecord
 */
public interface ILoulanDNSServiceStatLogRecord
{


    /**
     * DNSサービスインスタンスの所属ユーザー名を取得する.
     * 
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public String getUserName() throws LoulanDNSSystemServiceException;

    /**
     * DNSサービスインスタンスの名前を取得する.
     * 
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public String getDNSServiceInstanceName() throws LoulanDNSSystemServiceException;

    /**
     * DNS問い合わせメッセージを取得する.
     * 
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    @JsonIgnore
    public IDNSQuestionMessage getQuestionMessage() throws LoulanDNSSystemServiceException;


    /**
     * DNS回答メッセージを取得する.
     * 
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    @JsonIgnore
    public IDNSResponseMessage getResponseMessage() throws LoulanDNSSystemServiceException;


    /**
     * オプション情報を取得する.
     * 
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public String[] getOptions() throws LoulanDNSSystemServiceException;


    /**
     * 本統計情報が記録された日時を取得する.
     * 
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public String getLoggingDateTime() throws LoulanDNSSystemServiceException;



    /**
     * プロパティ情報(オプション値)を取得する.
     * 
     * @param key
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public String getProperty(String key) throws LoulanDNSSystemServiceException;


}