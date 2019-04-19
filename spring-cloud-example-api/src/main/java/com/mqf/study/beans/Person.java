package com.mqf.study.beans;

import java.io.Serializable;

/**
 * @ClassName Person
 * @Description TODO
 * @Author mqf
 * @Date 2019/4/15 17:05
 */
public class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 数据库
     */
    private String dbSource;



    public Person() {
    }
    

    public Person(Long id, String name, Integer age, String email, String dbSource) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
        this.dbSource = dbSource;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getDbSource() {
        return dbSource;
    }

    public void setDbSource(String dbSource) {
        this.dbSource = dbSource;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", dbSource='" + dbSource + '\'' +
                '}';
    }
}
