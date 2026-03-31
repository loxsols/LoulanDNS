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
@Table(name = "COMMON_SYSTEM_LOG_PROPERTIES")
@Entity
public class CommonSystemLogProperties
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMON_SYSTEM_LOG_PROPERTY_ID", nullable = false)
    private Long commonSystemLogPropertyID;

    @Column(name = "COMMON_SYSTEM_LOG_ID", nullable = false)
    private Long commonSystemLogID;

    @Column(name = "COMMON_SYSTEM_LOG_PROPERTY_KEY", nullable = false )
    private String commonSystemLogPropertyKey;

    @Column(name = "COMMON_SYSTEM_LOG_PROPERTY_VALUE", nullable = false )
    private String commonSystemLogPropertyValue;

    @Column(name = "COMMON_SYSTEM_LOG_PROPERTY_OPTION1", nullable = true )
    private String commonSystemLogPropertyOption1;

    @Column(name = "COMMON_SYSTEM_LOG_PROPERTY_OPTION2", nullable = true )
    private String commonSystemLogPropertyOption2;

    @Column(name = "COMMON_SYSTEM_LOG_PROPERTY_OPTION3", nullable = true )
    private String commonSystemLogPropertyOption3;

    @Column(name = "RECORD_STATUS", nullable = false)
    private long recordStatus;

    @Column(name = "MEMO")
    private String memo;

    @Column(name = "CREATE_DATE" )
    private String createDate;

    @Column(name = "UPDATE_DATE" )
    private String updateDate;


}