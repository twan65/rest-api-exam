package com.rest.api.controller.v1;

import com.rest.api.entity.User;
import com.rest.api.repo.UserJpaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // 結果をJSONで出力する。
@RequestMapping(value = "/v1")
public class UserController {

    @Autowired
    private UserJpaRepo userJpaRepo;

    @GetMapping(value = "/user")
    public List<User> findAllUser() {
        return userJpaRepo.findAll();
    }

    @PostMapping(value = "/user")
    public User save() {
        User user = User.builder()
                .uid("yumi@naver.com")
                .name("유미")
                .build();
        return userJpaRepo.save(user);
    }
}
