package io.github.tomaszpro99.DigitalCookbook.service;


import io.github.tomaszpro99.DigitalCookbook.dto.UserDto;
import io.github.tomaszpro99.DigitalCookbook.entity.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);

    User findByEmail(String email);

    List<UserDto> findAllUsers();

    String confirmEmail(String confirmationToken);
}
