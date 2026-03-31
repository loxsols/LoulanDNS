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
@Table(name = "DNS_RESOLVER_INSTANCE")
@Entity
public class DNSResolverInstance
{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DNS_RESOLVER_INSTANCE_ID", nullable = false)
    private Long dnsResolverInstanceID;

    @Column(name = "USER_ID", nullable = false)
    private Long userID;

    @Column(name = "DNS_RESOLVER_INSTANCE_NAME", nullable = false )
    private String dnsResolverInstanceName;

    @Column(name = "DNS_RESOLVER_INSTANCE_EXPLAIN", nullable = false )
    private String dnsResolverInstanceExplain;

    @Column(name = "DNS_RESOLVER_TYPE_CODE", nullable = false )
    private Long dnsResolverTypeCode;

    @Column(name = "RECORD_STATUS", nullable = false)
    private long recordStatus;

    @Column(name = "MEMO")
    private String memo;

    @Column(name = "CREATE_DATE" )
    private String createDate;

    @Column(name = "UPDATE_DATE" )
    private String updateDate;

}