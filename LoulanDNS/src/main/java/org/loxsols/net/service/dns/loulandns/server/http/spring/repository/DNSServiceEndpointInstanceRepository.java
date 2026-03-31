package org.loxsols.net.service.dns.loulandns.server.http.spring.repository;


import java.util.List;

import org.loxsols.net.service.dns.loulandns.server.http.spring.model.DNSServiceEndpointInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface DNSServiceEndpointInstanceRepository extends JpaRepository<DNSServiceEndpointInstance, Long>
{

    @Query("select p from DNSServiceEndpointInstance p where p.dnsServiceEndpointInstanceID = :dnsServiceEndpointInstanceID order by p.dnsServiceEndpointInstanceID asc")
    public List<DNSServiceEndpointInstance> findByDNSServiceEndpointInstanceID(long dnsServiceEndpointInstanceID);

    @Query("select p from DNSServiceEndpointInstance p where p.dnsServiceInstanceID = :dnsServiceInstanceID order by p.dnsServiceEndpointInstanceID asc")
    public List<DNSServiceEndpointInstance> findByDNSServiceInstanceID(long dnsServiceInstanceID);

    public DNSServiceEndpointInstance save(DNSServiceEndpointInstance dnsServiceInstance);

    @Transactional
    @Query("delete from DNSServiceEndpointInstance p where p.dnsServiceEndpointInstanceID = :dnsServiceEndpointInstanceID ")
    public void deleteByDNSServiceEndpointInstanceID(long dnsServiceEndpointInstanceID);

}



