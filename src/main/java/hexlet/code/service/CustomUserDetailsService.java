package hexlet.code.service;

import hexlet.code.dto.UserCreateDto;
import hexlet.code.dto.UserDto;
import hexlet.code.dto.UserUpdateDto;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import jakarta.validation.Validator;

import java.util.List;
import java.util.Optional;


@Service
public class CustomUserDetailsService implements UserDetailsManager {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper mapper;

    @Autowired
    private Validator validator;

    //public String encodePassword(String pass) {
    //    return passwordEncoder.encode(pass);
    //}

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = Optional.ofNullable(userRepository.findByEmail(email))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return (UserDetails) user;
    }

    @Override
    public void createUser(UserDetails details) {
        User user = new User();
        user.setEmail(details.getUsername());
        var hashedPassword = passwordEncoder.encode(details.getPassword());
        user.setPasswordDigest(hashedPassword);
        userRepository.save(user);
    }

    public UserDto createUser(UserCreateDto userCreateDto) {
        User user = mapper.map(userCreateDto);
        //Password Digest already mapped through encoder - but it's smell code
        user = userRepository.save(user); //Constraint violation exception catch if e-mail exist already
        return mapper.map(user);
    }


    @Override
    public void updateUser(UserDetails details) {
        User user = Optional.ofNullable(
                        userRepository.findByEmail(details.getUsername()))
                .orElseThrow(() ->
                        new ResourceNotFoundException("User with name " + details.getUsername() + " not found!"));
        user.setEmail(details.getUsername());
        //TODO update password with checking valid authorities
        //user.setPasswordDigest(passwordEncoder.encode(details.getPassword()));
        userRepository.save(user);
    }

    public UserDto updateUser(UserUpdateDto userUpdateDto, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found!"));
        mapper.update(userUpdateDto, user);
        user = userRepository.save(user);
        return mapper.map(user);
    }

    @Override
    public void deleteUser(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteUser'");
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found!"));
        userRepository.delete(user);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'changePassword'");
    }

    @Override
    public boolean userExists(String username) {
        return false;
    }

    public List<UserDto> getAll() {
        return userRepository.findAll()
                .stream().map(mapper::map)
                .toList();
    }

    public User getUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id" + id + " not found!"));
    }
}
