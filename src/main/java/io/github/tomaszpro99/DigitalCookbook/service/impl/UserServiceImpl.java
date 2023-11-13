package io.github.tomaszpro99.DigitalCookbook.service.impl;


import io.github.tomaszpro99.DigitalCookbook.dto.UserDto;
import io.github.tomaszpro99.DigitalCookbook.entity.ConfirmationToken;
import io.github.tomaszpro99.DigitalCookbook.entity.Role;
import io.github.tomaszpro99.DigitalCookbook.entity.User;
import io.github.tomaszpro99.DigitalCookbook.repository.ConfirmationTokenRepository;
import io.github.tomaszpro99.DigitalCookbook.repository.RoleRepository;
import io.github.tomaszpro99.DigitalCookbook.repository.UserRepository;
import io.github.tomaszpro99.DigitalCookbook.service.EmailService;
import io.github.tomaszpro99.DigitalCookbook.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    EmailService emailService;
    @Autowired
    UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getFirstName() + " " + userDto.getLastName());
        user.setEmail(userDto.getEmail());

        //encrypt the password once we integrate spring security
        //user.setPassword(userDto.getPassword());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        Role role = roleRepository.findByName("ROLE_NEW");
        if(role == null){ role = createRole("ROLE_NEW"); }
        user.setRoles(Arrays.asList(role));
        userRepository.save(user);

        //verifyMail
        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        confirmationTokenRepository.save(confirmationToken);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("DigitalCookbook rejestracja");
        mailMessage.setText("Konto utworzone! "+ user.getName() +", zaloguj się za pomocą tego linku: "
                +"http://localhost:8080/confirm-account?token="+confirmationToken.getConfirmationToken());
        emailService.sendEmail(mailMessage);
    }
    @Override
    public String confirmEmail(String confirmationToken) {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

        if(token != null)
        {
            User user = userRepository.findByEmailIgnoreCase(token.getUserEntity().getEmail());
            user.setVerify(true);
            Role role = roleRepository.findByName("ROLE_USER");
            if(role == null){ role = createRole("ROLE_USER"); }
            user.getRoles().clear();
            user.getRoles().add(role);
            userRepository.save(user);
            //return ResponseEntity.ok("Email zweryfikowany!");
        }
        //return ResponseEntity.badRequest().body("Error: Nie można zweryfikować Email");
        return "Error: Nie można zweryfikować Email";
    }

    @Override
    public User findByEmail(String email) { return userRepository.findByEmail(email); }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map((user) -> convertEntityToDto(user))
                .collect(Collectors.toList());
    }
    private UserDto convertEntityToDto(User user){
        UserDto userDto = new UserDto();
        String[] name = user.getName().split(" ");
        userDto.setFirstName(name[0]);
        userDto.setLastName(name[1]);
        userDto.setEmail(user.getEmail());
        return userDto;
    }
    private Role createRole(String newRole) {
        Role role = new Role();
        role.setName(newRole);
        return roleRepository.save(role);
    }
}
