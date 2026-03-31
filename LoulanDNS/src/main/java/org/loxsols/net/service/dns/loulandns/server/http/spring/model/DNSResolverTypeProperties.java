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
@Table(name = "DNS_RESOLVER_TYPE_PROPERTIES")
@Entity
public class DNSResolverTypeProperties
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DNS_RESOLVER_TYPE_PROPERTY_ID", nullable = false)
    public Long dnsResolverTypePropertyID;

    @Column(name = "DNS_RESOLVER_TYPE_ID", nullable = false)
    public Long dnsResolverTypeID;

    @Column(name = "DNS_RESOLVER_TYPE_PROPERTY_KEY", nullable = false )
    public String dnsResolverTypePropertyKey;

    @Column(name = "DNS_RESOLVER_TYPE_PROPERTY_VALUE", nullable = false )
    public String dnsResolverTypePropertyValue;

    @Column(name = "DNS_RESOLVER_TYPE_PROPERTY_EXPLAIN", nullable = false )
    public String dnsResolverTypePropertyExplain;

    @Column(name = "RECORD_STATUS", nullable = false)
    public long recordStatus;

    @Column(name = "MEMO")
    public String memo;

    @Column(name = "CREATE_DATE" )
    public String createDate;

    @Column(name = "UPDATE_DATE" )
    public String updateDate;

}