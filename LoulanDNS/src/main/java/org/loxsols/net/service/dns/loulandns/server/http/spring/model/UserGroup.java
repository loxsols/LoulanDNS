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
@Table(name = "USER_GROUP")
@Entity
public class UserGroup
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_GROUP_ID", nullable = false)
    private Long userGroupID;


    @Column(name = "USER_GROUP_NAME", nullable = false, unique = true)
    private String userGroupName;

    @Column(name = "RECORD_STATUS", nullable = false)
    private long recordStatus;

    @Column(name = "MEMO")
    private String memo;

    @Column(name = "CREATE_DATE" )
    private String createDate;

    @Column(name = "UPDATE_DATE" )
    private String updateDate;

}