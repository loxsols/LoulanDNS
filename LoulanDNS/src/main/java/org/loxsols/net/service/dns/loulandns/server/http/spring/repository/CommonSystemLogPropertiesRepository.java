package org.loxsols.net.service.dns.loulandns.server.http.spring.repository;


import java.util.List;

import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;


import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.loxsols.net.service.dns.loulandns.server.http.spring.model.*;


@Repository
public interface CommonSystemLogPropertiesRepository extends JpaRepository<CommonSystemLogProperties, Long>
{
    CommonSystemLogProperties findByCommonSystemLogPropertyID(long commonSystemLogPropertyID);

    /**
     * CommonSystemLogテーブルのID値でプロパティ情報を検索する.
     * 
     * @param commonSystemLogID
     * @return
     */
    @Query("select p from CommonSystemLogProperties p where p.commonSystemLogID = commonSystemLogID order by p.commonSystemLogPropertyID asc")
    List<CommonSystemLogProperties> findByCommonSystemLogID(long commonSystemLogID);


    /**
     * CommonSystemLogテーブルのID値(複数件)をリスト指定し、IN句で一致したプロパティ情報を返す.
     * 
     * @param commonSystemLogIDList
     * @return
     */
    @Query("select p from CommonSystemLogProperties p where p.commonSystemLogID in :commonSystemLogIDList order by p.commonSystemLogID asc, p.commonSystemLogPropertyID asc")
    List<CommonSystemLogProperties> findByCommonSystemLogIDList(@Param("commonSystemLogIDList") List<Long> commonSystemLogIDList);


    /**
     * プロパティ情報のキー値で検索する.
     * 
     * @param commonSystemLogPropertyKey
     * @return
     */
    // @Query("select p from CommonSystemLogProperties p where p.commonSystemLogPropertyKey LIKE %:commonSystemLogPropertyKey% order by p.commonSystemLogPropertyID asc")
    @Query("select p from CommonSystemLogProperties p where p.commonSystemLogPropertyKey LIKE CONCAT(:commonSystemLogPropertyKey, '%') order by p.commonSystemLogPropertyID asc")
    List<CommonSystemLogProperties> findByCommonSystemLogPropertyKey(String commonSystemLogPropertyKey);

}



