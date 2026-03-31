package org.loxsols.net.service.dns.loulandns.server.http.spring.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;



import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "DNS_SERVICE_ENDPOINT_INSTANCE")
@Entity
public class DNSServiceEndpointInstance
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DNS_SERVICE_ENDPOINT_INSTANCE_ID", nullable = false)
    public Long dnsServiceEndpointInstanceID;

    @Column(name = "DNS_SERVICE_INSTANCE_ID", nullable = false)
    public Long dnsServiceInstanceID;

    @Column(name = "DNS_SERVICE_ENDPOINT_INSTANCE_NAME", nullable = false, unique = true)
    public String dnsServiceEndpointInstanceName;

    @Column(name = "DNS_SERVICE_ENDPOINT_INSTANCE_EXPLAIN", nullable = false )
    public String dnsServiceEndpointInstanceExplain;

    @Column(name = "DNS_SERVICE_ENDPOINT_TYPE_CODE", nullable = false )
    public Long dnsServiceEndpointTypeCode;

    @Column(name = "RECORD_STATUS", nullable = false)
    public long recordStatus;

    @Column(name = "MEMO")
    public String memo;

    @Column(name = "CREATE_DATE" )
    public String createDate;

    @Column(name = "UPDATE_DATE" )
    public String updateDate;


    /**
     * dnsServiceEndpointInstanceIDメンバ変数のsetterメソッド.
     * 何故か、dnsServiceEndpointInstanceIDメンバ変数の設定処理がJDKのコンパイル時に異常になることがあるため、手動で実装した.
     * おそらく、lombokと相性が悪いと思われる.
     * 
     * @param value
     */
    public void setDNSServiceEndpointInstanceID(Long value)
    {
        this.dnsServiceEndpointInstanceID = value;
    }


}