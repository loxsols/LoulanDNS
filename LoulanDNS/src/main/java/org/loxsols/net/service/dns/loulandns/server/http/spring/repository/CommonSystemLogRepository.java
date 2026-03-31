package org.loxsols.net.service.dns.loulandns.server.http.spring.repository;


import java.util.List;

import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;


import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import org.loxsols.net.service.dns.loulandns.server.http.spring.model.*;


@Repository
public interface CommonSystemLogRepository extends JpaRepository<CommonSystemLog, Long>
{
    List<CommonSystemLog> findByCommonSystemLogID(long commonSystemLogPropertyID);

    @Query("select p from CommonSystemLog p where p.commonSystemLogTag LIKE %:commonSystemLogTag% order by p.commonSystemLogID asc")
    List<CommonSystemLog> findByCommonSystemLogTag(String commonSystemLogTag);


    /**
     * JPQLのJOIN ON句を利用して、CommonSystemLogテーブルのタグ値と、CommonSystemLogPropertiesテーブルのプロパティ情報が一致するレコードを取得する.
     * 
     * @param commonSystemLogTag
     * @param propertyKey
     * @param propertyValue
     * @return
     */
    @Query("select p, o from CommonSystemLog p inner join CommonSystemLogProperties o ON p.commonSystemLogID = o.commonSystemLogID where p.commonSystemLogTag LIKE %:commonSystemLogTag% and o.commonSystemLogPropertyKey LIKE %:propertyKey% and o.commonSystemLogPropertyValue LIKE %:propertyValue% order by p.commonSystemLogID asc")
    List<CommonSystemLog> findByCommonSystemLogTagAndProperty(String commonSystemLogTag, String propertyKey, String propertyValue);


}



