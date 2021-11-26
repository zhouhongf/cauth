package com.myworld.cauth.sitecom.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@DynamicInsert
@DynamicUpdate
public class BankLite implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keyWordOne;
    private String keyWordTwo;
    private String keyWordThree;
    private String keyWordFour;

    private String name;
    private String alias;
    private String type;

    private String province;
    private String city;
    private String county;

    public BankLite() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKeyWordOne() {
        return keyWordOne;
    }

    public void setKeyWordOne(String keyWordOne) {
        this.keyWordOne = keyWordOne;
    }

    public String getKeyWordTwo() {
        return keyWordTwo;
    }

    public void setKeyWordTwo(String keyWordTwo) {
        this.keyWordTwo = keyWordTwo;
    }

    public String getKeyWordThree() {
        return keyWordThree;
    }

    public void setKeyWordThree(String keyWordThree) {
        this.keyWordThree = keyWordThree;
    }

    public String getKeyWordFour() {
        return keyWordFour;
    }

    public void setKeyWordFour(String keyWordFour) {
        this.keyWordFour = keyWordFour;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }
}

