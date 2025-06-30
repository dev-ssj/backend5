package org.example.backendproject.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
//스프링에서 모든 컨트롤러의 예외를 한곳에서 처리하기 위한 어노테이션
//글로벌 예외처리기가 없으면 우리가 이전에 보던 장황한 에러코드가 모두 보임
public class GlobalExceptionHandler {

    //컨트롤러에서 RuntimeException 에러가 발생했을때 이 메서드가 대신 처리하도록 매핑
//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e){
//        log.warn("런타임 예외 처리: {}", e.getMessage());
//        log.error("❗에러라구용!!! 유저업따구요!");
//        ErrorResponse errorResponse = new ErrorResponse(400, "에러 전달 메시지", e.getMessage());
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
//    }

        //커스텀 에러용ㄴ
//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e){
//        log.warn("런타임 예외 처리: {}", e.getMessage());
//        return ResponseEntity
//                .status(HttpStatus.UNAUTHORIZED)
//                .body(ErrorResponse.of(ErrorCode.INTERNAL_ERROR, e.getMessage()));
//    }


    // 400: 파라미터 타입 오류, JSON 파싱 오류 등
    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<?> handleBadRequest(Exception e) {
        log.warn("[BAD_REQUEST] {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다: " + e.getMessage());
    }

    // 400: DTO validation(@Valid) 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException e) {
        log.warn("[VALIDATION_FAIL] {}", e.getMessage());

        //유효성 검증 실패한 모든 필드 오류 리스트를 가져옴
        //유효성 검증 실패한 필드명과 이유를 콤마로 연결해서 한 줄 메시지로 만들어줌
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .reduce((m1, m2) -> m1 + ", " + m2)
                .orElse("유효성 검사 실패");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
    }

    //인증 실패  401
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException e) {
        log.warn("[LOGIN_FAIL] {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    // 403: 인가(권한) 실패
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException e) {
        log.warn("[ACCESS_DENIED] {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
    }

    //그 외 모든 예외 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        log.error("[EXCEPTION][UNHANDLED] ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 내부 오류가 발생했습니다.");
    }
}
