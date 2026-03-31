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
@Table(name = "COMMON_SYSTEM_LOG")
@Entity
public class CommonSystemLog
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMON_SYSTEM_LOG_ID", nullable = false)
    private Long commonSystemLogID;

    @Column(name = "COMMON_SYSTEM_LOG_TAG", nullable = false )
    private String commonSystemLogTag;

    @Column(name = "COMMON_SYSTEM_LOG_RECORD", nullable = false )
    private String commonSystemLogRecord;

    @Column(name = "COMMON_SYSTEM_LOG_OCCUR_DATE", nullable = false )
    private String commonSystemLogOccurDate;

    @Column(name = "COMMON_SYSTEM_LOG_OPTION1", nullable = true )
    private String commonSystemLogOption1;

    @Column(name = "COMMON_SYSTEM_LOG_OPTION2", nullable = true )
    private String commonSystemLogOption2;

    @Column(name = "COMMON_SYSTEM_LOG_OPTION3", nullable = true )
    private String commonSystemLogOption3;

    @Column(name = "RECORD_STATUS", nullable = false)
    private long recordStatus;

    @Column(name = "MEMO")
    private String memo;

    @Column(name = "CREATE_DATE" )
    private String createDate;

    @Column(name = "UPDATE_DATE" )
    private String updateDate;


}