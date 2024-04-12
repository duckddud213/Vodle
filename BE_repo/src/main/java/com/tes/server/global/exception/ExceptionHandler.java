package com.tes.server.global.exception;

import com.tes.server.global.Base.BaseResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// RestController 모든 예외 처리 관리
@RestControllerAdvice
public class ExceptionHandler {

    // Exceptions 예외 처리 응답
    @org.springframework.web.bind.annotation.ExceptionHandler(Exceptions.class)
    public ResponseEntity<? extends BaseResponseBody> exceptions(Exceptions e) {

        // 정의한 에러 코드 및 메시지 적용
        BaseResponseBody errorResponse = BaseResponseBody.error(e.getErrorCode().getHttpStatus().value(), e.getErrorCode().getErrorCode(), e.getMessage());

        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(errorResponse);
    }
}