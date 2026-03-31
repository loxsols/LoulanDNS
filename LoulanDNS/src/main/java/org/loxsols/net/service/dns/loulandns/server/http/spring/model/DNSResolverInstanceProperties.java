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
@Table(name = "DNS_RESOLVER_INSTANCE_PROPERTIES")
@Entity
public class DNSResolverInstanceProperties
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DNS_RESOLVER_INSTANCE_PROPERTY_ID", nullable = false)
    public Long dnsResolverInstancePropertyID;

    @Column(name = "DNS_RESOLVER_INSTANCE_ID", nullable = false)
    public Long dnsResolverInstanceID;

    @Column(name = "DNS_RESOLVER_INSTANCE_PROPERTY_KEY", nullable = false )
    public String dnsResolverInstancePropertyKey;

    @Column(name = "DNS_RESOLVER_INSTANCE_PROPERTY_VALUE", nullable = false )
    public String dnsResolverInstancePropertyValue;

    @Column(name = "DNS_RESOLVER_INSTANCE_PROPERTY_EXPLAIN", nullable = false )
    public String dnsResolverInstancePropertyExplain;

    @Column(name = "RECORD_STATUS", nullable = false)
    public long recordStatus;

    @Column(name = "MEMO")
    public String memo;

    @Column(name = "CREATE_DATE" )
    public String createDate;

    @Column(name = "UPDATE_DATE" )
    public String updateDate;

}