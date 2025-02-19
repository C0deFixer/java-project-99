package hexlet.code.service;

import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import jakarta.validation.Validator;
import java.util.Optional;


@Service
public class CustomUserDetailsService implements UserDetailsManager {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Validator validator;

    public String encodePassword(String pass) {
        return passwordEncoder.encode(pass);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = Optional.ofNullable(userRepository.findByEmail(email))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return (UserDetails) user;
    }

    @Override
    public void createUser(UserDetails details) {
        User user = new User();
        user.setFirstName("Unnamed");
        var passHash = encodePassword(details.getPassword());
        user.setPasswordDigest(passHash);
        userRepository.save(user);
    }

    @Override
    public void updateUser(UserDetails details) {
        User user = Optional.ofNullable(userRepository.findByEmail(details.getUsername()))
                .orElseThrow(() -> new ResourceNotFoundException("User with name " + details.getUsername() + " not found!"));
        user.setEmail(details.getUsername());
        //user.setPasswordDigest(passwordEncoder.encode(details.getPassword()));
        userRepository.save(user);
    }

    @Override
    public void deleteUser(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteUser'");
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

}
