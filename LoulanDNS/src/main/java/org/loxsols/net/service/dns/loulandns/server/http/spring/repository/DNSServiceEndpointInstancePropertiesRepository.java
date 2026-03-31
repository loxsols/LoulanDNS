package org.loxsols.net.service.dns.loulandns.server.http.spring.repository;


import java.util.List;

import org.loxsols.net.service.dns.loulandns.server.http.spring.model.DNSServiceEndpointInstanceProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface DNSServiceEndpointInstancePropertiesRepository extends JpaRepository<DNSServiceEndpointInstanceProperties, Long>
{

    @Query("select p from DNSServiceEndpointInstanceProperties p where p.dnsServiceEndpointInstancePropertyID = :dnsServiceEndpointInstancePropertyID order by p.dnsServiceEndpointInstancePropertyID asc")
    public List<DNSServiceEndpointInstanceProperties> findByDNSServiceEndpointInstancePropertyID(long dnsServiceEndpointInstancePropertyID);

    @Query("select p from DNSServiceEndpointInstanceProperties p where p.dnsServiceEndpointInstanceID = :dnsServiceEndpointInstanceID order by p.dnsServiceEndpointInstancePropertyID asc")
    public List<DNSServiceEndpointInstanceProperties> findByDNSServiceEndpointInstanceID(long dnsServiceEndpointInstanceID);

    public DNSServiceEndpointInstanceProperties save(DNSServiceEndpointInstanceProperties dnsServiceEndpointInstanceProperties);

    @Transactional
    @Query("delete from DNSServiceEndpointInstanceProperties p where p.dnsServiceEndpointInstancePropertyID = :dnsServiceEndpointInstancePropertyID ")
    public void deleteByDNSServiceEndpointInstancePropertyID(long dnsServiceEndpointInstancePropertyID);

}



