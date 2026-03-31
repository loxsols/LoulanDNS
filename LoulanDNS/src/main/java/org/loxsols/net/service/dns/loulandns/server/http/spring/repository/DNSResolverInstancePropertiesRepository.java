package org.loxsols.net.service.dns.loulandns.server.http.spring.repository;


import java.util.List;

import org.loxsols.net.service.dns.loulandns.server.http.spring.model.DNSResolverInstanceProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface DNSResolverInstancePropertiesRepository extends JpaRepository<DNSResolverInstanceProperties, Long>
{
    
    @Query("select p from DNSResolverInstanceProperties p where p.dnsResolverInstancePropertyID = :dnsResolverInstancePropertyID order by p.dnsResolverInstancePropertyID asc")
    public List<DNSResolverInstanceProperties> findByDNSResolverInstancePropertyID(long dnsResolverInstancePropertyID);

    @Query("select p from DNSResolverInstanceProperties p where p.dnsResolverInstanceID = dnsResolverInstanceID order by p.dnsResolverInstancePropertyID asc")
    public List<DNSResolverInstanceProperties> findByDNSResolverInstanceID(long dnsResolverInstanceID);

    public DNSResolverInstanceProperties save(DNSResolverInstanceProperties dnsResolverInstanceProperties);


    @Query("delete from DNSResolverInstanceProperties p where p.dnsResolverInstancePropertyID = :dnsResolverInstancePropertyID ")   
    public void deleteDNSResolverInstancePropertiesByDNSResolverInstancePropertyID(long dnsResolverInstancePropertyID);

    @Query("delete from DNSResolverInstanceProperties p where p.dnsResolverInstanceID = :dnsResolverInstanceID ")   
    public void deleteDNSResolverInstancePropertiesByDNSResolverInstanceID(long dnsResolverInstanceID);

}



