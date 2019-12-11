package com.ndk.facedetect;

import org.dao.annotation.Column;
import org.dao.annotation.Table;

import java.io.Serializable;

/**
 * create by TIAN FENG on 2019/12/9
 */
@Table
public class Person implements Serializable {

    @Column
    private String name;
    @Column
    private int label;

    public Person() {
    }

    public Person(String name, int label) {
        this.name = name;
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Person(int label) {
        this.label = label;
    }
}
