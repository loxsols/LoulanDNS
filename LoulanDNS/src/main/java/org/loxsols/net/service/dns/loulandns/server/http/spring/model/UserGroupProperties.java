package org.loxsols.net.service.dns.loulandns.server.http.spring.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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
@Table(name = "USER_GROUP_PROPERTIES")
@Entity
public class UserGroupProperties
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_GROUP_PROPERTY_ID", nullable = false)
    public Long userGroupPropertyID;

    @Column(name = "USER_GROUP_ID", nullable = false)
    public Long userGroupID;

    @Column(name = "USER_GROUP_PROPERTY_KEY", nullable = false)
    public String userGroupPropertyKey;

    @Column(name = "USER_GROUP_PROPERTY_VALUE", nullable = false)
    public String userGroupPropertyValue;

    @Column(name = "USER_GROUP_PROPERTY_EXPLAIN", nullable = false)
    public String userGroupPropertyExplain;

    @Column(name = "RECORD_STATUS", nullable = false)
    public long recordStatus;

    @Column(name = "MEMO")
    public String memo;

    @Column(name = "CREATE_DATE" )
    public String createDate;

    @Column(name = "UPDATE_DATE" )
    public String updateDate;

}