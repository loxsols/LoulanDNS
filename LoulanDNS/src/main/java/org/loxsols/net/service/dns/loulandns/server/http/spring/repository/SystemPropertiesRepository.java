package org.loxsols.net.service.dns.loulandns.server.http.spring.repository;


import java.util.List;

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

    // 2026/10/27 JPA関数修正 : LIKE演算子に指定する文字列を後方一致とするため、%記号を後方にのみ配置する.
    // [修正前]
    // @Query("select p from SystemProperties p where p.systemPropertyKey LIKE %:propertyKey% order by p.systemPropertyID asc")
    //
    // [修正後]
    @Query("select p from SystemProperties p where p.systemPropertyKey LIKE CONCAT(:propertyKey,'%') order by p.systemPropertyID asc")    
    List<SystemProperties> findBySystemPropertyKey(String propertyKey);

}



