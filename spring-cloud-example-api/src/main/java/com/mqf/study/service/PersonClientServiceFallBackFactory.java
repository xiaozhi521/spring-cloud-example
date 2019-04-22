package com.mqf.study.service;

import com.mqf.study.beans.Person;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName PersonClientServiceFallBackFactory
 * @Description TODO
 * @Author mqf
 * @Date 2019/4/22 16:39
 */
@Component
public class PersonClientServiceFallBackFactory implements FallbackFactory<PersonClientService> {
    @Override
    public PersonClientService create(Throwable throwable) {
        return new PersonClientService() {
            @Override
            public Person get(Long id) {
                return new Person().setId(id).setName("该ID：" + id + "没有没有对应的信息,Consumer客户端提供的降级信息,此刻服务Provider已经关闭")
                        .setDbSource("no this database in MySQL");
            }

            @Override
            public List<Person> list() {
                return null;
            }

            @Override
            public boolean add(Person dept) {
                return false;
            }
        };
    }
}
