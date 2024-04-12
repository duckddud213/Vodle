package com.tes.server.global.Base;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class BaseResponseBody<T> {

    // 응답 코드
    private int status;

    // 메시지
    private String message;

    // 응답 코드의 테스트 케이스
    private String code;

    public static <T> BaseResponseBody<T> of(int status, String message, String code) {
        BaseResponseBody<T> body = new BaseResponseBody<>();
        body.setStatus(status);
        body.setMessage(message);
        body.setCode(code);

        return body;
    }

    // status(200_1, 200_2, 200_3, 404), message(success 이유, fail 이유), code(U-001)
    public static BaseResponseBody<Object> error(int status, String code, String message) {
        BaseResponseBody<Object> body = new BaseResponseBody<>();
        body.setStatus(status);
        body.setCode(code);
        body.setMessage(message);

        return body;
    }
}