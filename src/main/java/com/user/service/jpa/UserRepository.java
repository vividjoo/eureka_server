package com.user.service.jpa;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, Long> {

//    JPA는 메서드 명을 find***로 네이밍 하면은 SELECT query 문으로 자동 생성해 준다.
    UserEntity findByUserId(String userId);
    UserEntity findByEmail(String username);
}
