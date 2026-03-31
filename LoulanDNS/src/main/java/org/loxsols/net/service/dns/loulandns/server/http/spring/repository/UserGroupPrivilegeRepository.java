package org.loxsols.net.service.dns.loulandns.server.http.spring.repository;


import java.util.List;

import org.loxsols.net.service.dns.loulandns.server.http.spring.model.UserGroupPrivilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface UserGroupPrivilegeRepository extends JpaRepository<UserGroupPrivilege, Long>
{

    List<UserGroupPrivilege> findByUserGroupPrivilegeID(long userGroupPrivilegeID);

    @Query("select p from UserGroupPrivilege p where p.userGroupID = :userGroupID order by p.userGroupPrivilegeID asc")
    List<UserGroupPrivilege> findByUserGroupID(long userGroupID);


}



