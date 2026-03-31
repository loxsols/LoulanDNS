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
@Table(name = "SYSTEM_PROPERTIES")
@Entity
public class SystemProperties
{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SYSTEM_PROPERTY_ID", nullable = false)
    private Long systemPropertyID;

    @Column(name = "SYSTEM_PROPERTY_KEY", nullable = false )
    private String systemPropertyKey;

    @Column(name = "SYSTEM_PROPERTY_VALUE", nullable = false )
    private String systemPropertyValue;

    @Column(name = "SYSTEM_PROPERTY_EXPLAIN", nullable = false )
    private String systemPropertyExplain;

    @Column(name = "RECORD_STATUS", nullable = false)
    private long recordStatus;

    @Column(name = "MEMO")
    private String memo;

    @Column(name = "CREATE_DATE" )
    private String createDate;

    @Column(name = "UPDATE_DATE" )
    private String updateDate;

}