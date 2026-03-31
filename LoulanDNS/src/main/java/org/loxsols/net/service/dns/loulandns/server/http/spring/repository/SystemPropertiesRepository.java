package org.loxsols.net.service.dns.loulandns.server.http.spring.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;


import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import org.loxsols.net.service.dns.loulandns.server.http.spring.model.*;


@Repository
public interface SystemPropertiesRepository extends JpaRepository<SystemProperties, Long>
{
    List<SystemProperties> findBySystemPropertyID(long systemPropertyID);

    @Query("select p from SystemProperties p where p.systemPropertyKey LIKE %:propertyKey% order by p.systemPropertyID asc")
    List<SystemProperties> findBySystemPropertyKey(String propertyKey);

}



