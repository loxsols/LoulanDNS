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
@Table(name = "USER")
@Entity
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID", nullable = false)
    public Long userID;

    @Column(name = "USER_NAME", nullable = false, unique = true)
    public String userName;

    @Column(name = "USER_PASSWORD", nullable = false)
    public String userPassword;

    @Column(name = "RECORD_STATUS", nullable = false)
    public long recordStatus;

    @Column(name = "MEMO")
    public String memo;

    @Column(name = "CREATE_DATE" )
    public String createDate;

    @Column(name = "UPDATE_DATE" )
    public String updateDate;

}