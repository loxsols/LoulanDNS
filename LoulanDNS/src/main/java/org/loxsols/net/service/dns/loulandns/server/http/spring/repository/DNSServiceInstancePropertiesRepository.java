package org.loxsols.net.service.dns.loulandns.server.http.spring.repository;


import java.util.List;

import org.loxsols.net.service.dns.loulandns.server.http.spring.model.DNSServiceInstance;
import org.loxsols.net.service.dns.loulandns.server.http.spring.model.DNSServiceInstanceProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface DNSServiceInstancePropertiesRepository extends JpaRepository<DNSServiceInstance, Long>
{

    @Query("select p from DNSServiceInstanceProperties p where p.dnsServiceInstancePropertyID = :dnsServiceInstancePropertyID order by p.dnsServiceInstancePropertyID asc")
    public List<DNSServiceInstanceProperties> findByDNSServiceInstancePropertyID(long dnsServiceInstancePropertyID);

    @Query("select p from DNSServiceInstanceProperties p where p.dnsServiceInstanceID = dnsServiceInstanceID order by p.dnsServiceInstancePropertyID asc")
    public List<DNSServiceInstanceProperties> findByDNSServiceInstanceID(long dnsServiceInstanceID);

    public DNSServiceInstanceProperties save(DNSServiceInstanceProperties dnsServiceInstanceProperties);


    @Query("delete from DNSServiceInstanceProperties p where p.dnsServiceInstancePropertyID = :dnsServiceInstancePropertyID ")   
    public void deleteDNSServiceInstancePropertiesByDNSServiceInstancePropertyID(long dnsServiceInstancePropertyID);

    @Query("delete from DNSServiceInstanceProperties p where p.dnsServiceInstanceID = :dnsServiceInstanceID ")   
    public void deleteDNSServiceInstancePropertiesByDNSServiceInstanceID(long dnsServiceInstanceID);


}



