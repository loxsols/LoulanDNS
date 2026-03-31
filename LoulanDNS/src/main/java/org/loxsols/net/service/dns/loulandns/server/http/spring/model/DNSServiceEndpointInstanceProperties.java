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
@Table(name = "DNS_SERVICE_ENDPOINT_INSTANCE_PROPERTIES")
@Entity
public class DNSServiceEndpointInstanceProperties
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DNS_SERVICE_ENDPOINT_INSTANCE_PROPERTY_ID", nullable = false)
    public Long dnsServiceEndpointInstancePropertyID;

    @Column(name = "DNS_SERVICE_ENDPOINT_INSTANCE_ID", nullable = false)
    public Long dnsServiceEndpointInstanceID;

    @Column(name = "DNS_SERVICE_ENDPOINT_INSTANCE_PROPERTY_KEY", nullable = false, unique = true)
    public String dnsServiceEndpointInstancePropertyKey;

    @Column(name = "DNS_SERVICE_ENDPOINT_INSTANCE_PROPERTY_VALUE", nullable = false )
    public String dnsServiceEndpointInstancePropertyValue;

    @Column(name = "DNS_SERVICE_ENDPOINT_INSTANCE_PROPERTY_EXPLAIN", nullable = false )
    public String dnsServiceEndpointInstancePropertyExplain;

    @Column(name = "RECORD_STATUS", nullable = false)
    public long recordStatus;

    @Column(name = "MEMO")
    public String memo;

    @Column(name = "CREATE_DATE" )
    public String createDate;

    @Column(name = "UPDATE_DATE" )
    public String updateDate;

}