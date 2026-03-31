package org.loxsols.net.service.dns.loulandns.server.http.spring.repository;


import java.util.List;

import org.loxsols.net.service.dns.loulandns.server.http.spring.model.DNSResolverInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface DNSResolverInstanceRepository extends JpaRepository<DNSResolverInstance, Long>
{

    @Query("select p from DNSResolverInstance p where p.dnsResolverInstanceID = :dnsResolverInstanceID order by p.dnsResolverInstanceID asc")
    public List<DNSResolverInstance> findByDNSResolverInstanceID(long dnsResolverInstanceID);

    @Query("select p from DNSResolverInstance p where p.userID = :userID order by p.dnsResolverInstanceID asc")
    public List<DNSResolverInstance> findByUserID(long userID);

    public DNSResolverInstance save(DNSResolverInstance dnsResovler);


    @Transactional
    @Query("delete from DNSResolverInstance p where p.dnsResolverInstanceID = :dnsResolverInstanceID ")
    public void deleteDNSResolverInstanceByDNSResolverInstanceID(long dnsResolverInstanceID); 

}



