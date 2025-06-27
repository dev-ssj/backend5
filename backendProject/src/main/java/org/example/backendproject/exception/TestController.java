package org.example.backendproject.exception;

import io.lettuce.core.ScriptOutputType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exception")
public class TestController {

    @GetMapping
    public ResponseEntity<ErrorResponse> test() {
        throw new RuntimeException("글로벌 예외처리 테스트중~");
    }
}
