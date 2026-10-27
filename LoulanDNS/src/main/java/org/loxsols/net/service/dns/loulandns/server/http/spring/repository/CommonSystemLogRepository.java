package org.loxsols.net.service.dns.loulandns.server.http.spring.repository;


import java.util.List;

import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;


import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import org.loxsols.net.service.dns.loulandns.server.http.spring.model.*;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;


@Repository
public interface CommonSystemLogRepository extends JpaRepository<CommonSystemLog, Long>
{
    List<CommonSystemLog> findByCommonSystemLogID(long commonSystemLogPropertyID);

    @Query("select p from CommonSystemLog p where p.commonSystemLogTag LIKE %:commonSystemLogTag% order by p.commonSystemLogID asc")
    List<CommonSystemLog> findByCommonSystemLogTag(String commonSystemLogTag);

    // LIKE '%...%' のように前後をワイルドカードで囲む「中間一致検索」は効率が悪いため、修正する.
    // 
    // [修正前] 
    // 
    // @Query("select p from CommonSystemLog p where p.commonSystemLogTag LIKE %:commonSystemLogTag% AND p.commonSystemLogOccurDate > %:fromDateTime% AND p.commonSystemLogOccurDate < %:untilDateTime% order by p.commonSystemLogOccurDate asc")
    // List<CommonSystemLog> findByCommonSystemLogTagAndFromDateTimeAndUntilDateTime(String commonSystemLogTag, String fromDateTime, String untilDateTime, Pageable pageable);
    // 
    // [修正後] :fromDateTimeと:untilDateTimeから%記号を取り除いた.
    // @Query("select p from CommonSystemLog p where p.commonSystemLogTag LIKE %:commonSystemLogTag% AND p.commonSystemLogOccurDate > :fromDateTime AND p.commonSystemLogOccurDate < :untilDateTime order by p.commonSystemLogOccurDate asc")
    @Query("select p from CommonSystemLog p where p.commonSystemLogTag LIKE CONCAT(:commonSystemLogTag, '%') AND p.commonSystemLogOccurDate > :fromDateTime AND p.commonSystemLogOccurDate < :untilDateTime order by p.commonSystemLogOccurDate asc")    
    List<CommonSystemLog> findByCommonSystemLogTagAndFromDateTimeAndUntilDateTime(String commonSystemLogTag, String fromDateTime, String untilDateTime, Pageable pageable);



    /**
     * JPQLのJOIN ON句を利用して、CommonSystemLogテーブルのタグ値と、CommonSystemLogPropertiesテーブルのプロパティ情報が一致するレコードを取得する.
     * 
     * @param commonSystemLogTag
     * @param propertyKey
     * @param propertyValue
     * @return
     */
    // @Query("select p, o from CommonSystemLog p inner join CommonSystemLogProperties o ON p.commonSystemLogID = o.commonSystemLogID where p.commonSystemLogTag LIKE %:commonSystemLogTag% and o.commonSystemLogPropertyKey LIKE %:propertyKey% and o.commonSystemLogPropertyValue LIKE %:propertyValue% order by p.commonSystemLogID asc")
    @Query("select p, o from CommonSystemLog p inner join CommonSystemLogProperties o ON p.commonSystemLogID = o.commonSystemLogID where p.commonSystemLogTag LIKE %:commonSystemLogTag% and o.commonSystemLogPropertyKey LIKE CONCAT(:propertyKey, '%') and o.commonSystemLogPropertyValue LIKE CONCAT(:propertyValue, '%') order by p.commonSystemLogID asc")
    List<CommonSystemLog> findByCommonSystemLogTagAndProperty(String commonSystemLogTag, String propertyKey, String propertyValue);


}



