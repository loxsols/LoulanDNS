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
@Table(name = "DNS_SERVICE_INSTANCE")
@Entity
public class DNSServiceInstance
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DNS_SERVICE_INSTANCE_ID", nullable = false)
    public Long dnsServiceInstanceID;

    @Column(name = "USER_ID", nullable = false)
    public Long userID;

    @Column(name = "DNS_SERVICE_INSTANCE_NAME", nullable = false, unique = true)
    public String dnsServiceInstanceName;

    @Column(name = "DNS_SERVICE_INSTANCE_EXPLAIN", nullable = false )
    public String dnsServiceInstanceExplain;

    @Column(name = "DNS_SERVICE_TYPE_CODE", nullable = false )
    public Long dnsServiceTypeCode;

    @Column(name = "DNS_RESOLVER_INSTANCE_ID", nullable = false)
    public Long dnsResolverInstanceID;

    @Column(name = "RECORD_STATUS", nullable = false)
    public long recordStatus;

    @Column(name = "MEMO")
    public String memo;

    @Column(name = "CREATE_DATE" )
    public String createDate;

    @Column(name = "UPDATE_DATE" )
    public String updateDate;


    public Long getDNSServiceInstanceID()
    {
        return this.dnsServiceInstanceID;
    }

    public void setDNSServiceInstanceID(Long value)
    {
        this.dnsServiceInstanceID = value;
    }


    public String getDNSServiceInstanceName()
    {
        return this.dnsServiceInstanceName;
    }

    public void setDNSServiceInstanceName(String value)
    {
        this.dnsServiceInstanceName = value;
    }

    public String getDNSServiceInstanceExplain()
    {
        return this.dnsServiceInstanceExplain;
    }

    public void setDNSServiceInstanceExplain(String value)
    {
        this.dnsServiceInstanceExplain = value;
    }


    public Long getDNSResolverInstanceID()
    {
        return this.dnsResolverInstanceID;
    }

    public void setDNSResolverInstanceID(Long dnsResolverInstanceID)
    {
        this.dnsResolverInstanceID = dnsResolverInstanceID;
    }

}