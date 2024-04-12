package com.tes.server.global.Base;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataResponseBody<T> extends BaseResponseBody {

    // 바디 데이터
    private T data;

    public static <T> DataResponseBody<T> of(int status, String message, String code, T data) {
        DataResponseBody<T> body = new DataResponseBody<>();
        body.setStatus(status);
        body.setMessage(message);
        body.setCode(code);
        body.setData(data);

        return body;
    }
}