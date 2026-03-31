package org.loxsols.net.service.dns.loulandns.server.http.spring.repository;


import java.util.List;

import org.loxsols.net.service.dns.loulandns.server.http.spring.model.DNSServiceInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface DNSServiceInstanceRepository extends JpaRepository<DNSServiceInstance, Long>
{

    @Query("select p from DNSServiceInstance p where p.dnsServiceInstanceID = :dnsServiceInstanceID order by p.dnsServiceInstanceID asc")
    public List<DNSServiceInstance> findByDNSServiceInstanceID(long dnsServiceInstanceID);

    @Query("select p from DNSServiceInstance p where p.userID = :userID order by p.dnsServiceInstanceID asc")
    public List<DNSServiceInstance> findByUserID(long userID);

    public DNSServiceInstance save(DNSServiceInstance dnsServiceInstance);

    @Transactional
    @Query("delete from DNSServiceInstance p where p.dnsServiceInstanceID = :dnsServiceInstanceID ")
    public void deleteDNSServiceInstanceByDNSServiceInstanceID(long dnsServiceInstanceID);

}



