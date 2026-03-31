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
@Table(name = "DNS_RESOLVER_TYPE")
@Entity
public class DNSResolverType
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DNS_RESOLVER_TYPE_ID", nullable = false)
    private Long dnsResolverTypeID;


    @Column(name = "DNS_RESOLVER_TYPE_CODE", nullable = false)
    private Long dnsResolverTypeCode;


    @Column(name = "DNS_RESOLVER_TYPE_NAME", nullable = false )
    private String dnsResolverTypeName;


    @Column(name = "DNS_RESOLVER_TYPE_EXPLAIN", nullable = false )
    private String dnsResolverTypeExplain;


    @Column(name = "DNS_RESOLVER_CLASS_NAME", nullable = false )
    private String dnsResolverClassName;


    @Column(name = "DNS_RESOLVER_CLASS_LOADER_NAME", nullable = false )
    private String dnsResolverClassLoaderName;


    @Column(name = "DNS_RESOLVER_OPTION_1", nullable = false )
    private String dnsResolverOption1;


    @Column(name = "DNS_RESOLVER_OPTION_2", nullable = false )
    private String dnsResolverOption2;


    @Column(name = "DNS_RESOLVER_OPTION_3", nullable = false )
    private String dnsResolverOption3;


    @Column(name = "RECORD_STATUS", nullable = false)
    public long recordStatus;

    @Column(name = "MEMO")
    public String memo;

    @Column(name = "CREATE_DATE" )
    public String createDate;

    @Column(name = "UPDATE_DATE" )
    public String updateDate;

}