package hexlet.code.controller.api;

import hexlet.code.dto.AuthRequest;
import hexlet.code.utils.JWTUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTUtils jwtUtils;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public String login(@Valid @RequestBody AuthRequest request) {
        var auth = new UsernamePasswordAuthenticationToken(
                request.getUsername(), request.getPassword());
        authenticationManager.authenticate(auth);

        return jwtUtils.generateToken(request.getUsername());
    }
}
