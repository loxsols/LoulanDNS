package org.loxsols.net.service.dns.loulandns.server.http.spring.repository;


import java.util.List;

import org.loxsols.net.service.dns.loulandns.server.http.spring.model.UserGroupMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface UserGroupMappingRepository extends JpaRepository<UserGroupMapping, Long>
{

    @Query("select m from UserGroupMapping m where m.userID = :userID order by m.userID asc")
    List<UserGroupMapping> findByUserID(long userID);

    @Query("select m from UserGroupMapping m where m.userGroupID = :userGroupID order by m.userGroupID asc")
    List<UserGroupMapping> findByUserGroupID(long userGroupID);


}



