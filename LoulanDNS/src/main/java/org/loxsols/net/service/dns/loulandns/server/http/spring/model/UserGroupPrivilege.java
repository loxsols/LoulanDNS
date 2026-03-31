package org.loxsols.net.service.dns.loulandns.server.http.spring.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;



import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USER_GROUP_PRIVILEGE")
@Entity
public class UserGroupPrivilege
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_GROUP_PRIVILEGE_ID", nullable = false)
    private Long userGroupPrivilegeID;


    // ここで@Idを定義するとSpringJPAのリポジトリクラスがバグるので定義しないこと.
    // DB側のDDLではUSER_GROUP_ID列を含む複数列での主キー制約をかけてあるが、特に不具合はないだろう.
    @Column(name = "USER_GROUP_ID", nullable = false, unique = true)
    private String userGroupID;

    @Column(name = "DNS_INFO_MASK_VALUE", nullable = false)
    private long dnsInfoMaskValue;

    @Column(name = "USER_INFO_MASK_VALUE", nullable = false)
    private long userInfoMaskValue;

    @Column(name = "SYSTEM_INFO_MASK_VALUE", nullable = false)
    private long systemInfoMaskValue;

    @Column(name = "RECORD_STATUS", nullable = false)
    private long recordStatus;

    @Column(name = "MEMO")
    private String memo;

    @Column(name = "CREATE_DATE" )
    private String createDate;

    @Column(name = "UPDATE_DATE" )
    private String updateDate;

}