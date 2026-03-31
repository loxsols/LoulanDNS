package org.loxsols.net.service.dns.loulandns.client;

import java.util.Properties;

import org.loxsols.net.service.dns.loulandns.client.common.DNSClientCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSQuestionMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;

public interface IDNSLookupClient
{

    // 指定した条件でDNSの名前解決を行う.
    public IDNSResponseMessage resolve(String qname, int qtype, int qclass) throws DNSClientCommonException;
    public IDNSResponseMessage resolve(IDNSQuestionMessage questionMessage) throws DNSClientCommonException;


    // 本クラスの初期化処理.
    public void init(Properties properties) throws DNSClientCommonException;


    // 外部問い合わせ先のDNSサーバーのIPアドレスを設定する.
    public void setDNSServerAddress(String address) throws DNSClientCommonException;

    // 外部問い合わせ先のDNSサーバーのポートを設定する.
    public void setDNSServerPort(int port) throws DNSClientCommonException;


    // ------- EDNS関係の項目
    // EDNS(0)を使用するかを設定する.
    public void setEnableEDNS(boolean value) throws DNSClientCommonException;
    
    // --------------- EDNS関係 / EDNS Client Subnet (ECS)関係の項目.
    // EDNS Client Subnet(ECS)を使用するかを設定する.
    public void setEnableEDNSClientSubnet(boolean value) throws DNSClientCommonException;

    // EDNS Client Subnet(ECS)のアドレス情報を設定する.
    public void setEDNSClientSubnetAddress(String address, int prefix) throws DNSClientCommonException;

    // EDNS Client Subnet(ECS)のアドレス情報のアドレス部を取得する.
    public String getEDNSClientSubnetAddress() throws DNSClientCommonException;

    // EDNS Client Subnet(ECS)のアドレス情報のプリフィックス部を取得する.
    public int getEDNSClientSubnetAddressPrefix() throws DNSClientCommonException;



}

