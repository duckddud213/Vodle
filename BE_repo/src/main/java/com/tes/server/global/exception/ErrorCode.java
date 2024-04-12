package com.tes.server.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 인증
    INVALID_ACCESSTOKEN(HttpStatus.FORBIDDEN, "A-001", "유효하지 않은 AccessToken"),
    EXPIRED_ACCESSTOKEN(HttpStatus.FORBIDDEN, "A-002", "만료된 AccessToken"),
    EXPIRED_REFRESHTOKEN(HttpStatus.FORBIDDEN, "A-003", "만료된 RefreshToken"),
    AUTO_LOGIN_FAIL(HttpStatus.FORBIDDEN, "A-004", "자동 로그인 실패"),

    // 회원
    USER_NOT_EXIST(HttpStatus.BAD_REQUEST, "U-001", "존재하지 않는 회원"),
    USER_INVALID_HMAC(HttpStatus.BAD_REQUEST, "U-002", "올바르지 않은 HMAC"),
    USER_INVALID_SECRET_KEY(HttpStatus.BAD_REQUEST, "U-003", "올바르지 않은 비밀키"),
    USER_DISCONNECTED_DB(HttpStatus.BAD_REQUEST, "U-004", "회원 디비 연결 실패"),
    USER_CANT_DELETE(HttpStatus.BAD_REQUEST, "U-005", "사용자를 삭제 실패"),

    // 음성 파일
    FILE_UPLOAD_FAIL(HttpStatus.BAD_REQUEST, "F-001", "음성 낙서 등록이 실패"),
    FILE_DOWNLOAD_FAIL(HttpStatus.BAD_REQUEST, "F-002", "음성 낙서 다운로드를 실패"),

    // 음성 기록
    VODLE_NOT_EXIST_DB(HttpStatus.BAD_REQUEST, "V-001", "음성 낙서 데이터가 존재하지 않음"),

    // AI서버
    CONNECT_FAIL(HttpStatus.BAD_REQUEST, "AI-001", "AI서버 호출 실패"),
    CONVERSION_PROCESSING_FAIL(HttpStatus.BAD_REQUEST, "AI-002", "변조처리 실패"),
    TTS_PROCESSING_FAIL(HttpStatus.BAD_REQUEST, "AI-003", "TTS처리 실패"),
    STT_PROCESSING_FAIL(HttpStatus.BAD_REQUEST, "AI-004", "STT처리 실패"),
    CLASSIFY_PROCESSING_FAIL(HttpStatus.BAD_REQUEST, "AI-005", "주제선정처리 실패"),
    ASYNC_PROCESSING_FAIL(HttpStatus.BAD_REQUEST, "AI-006", "비동기처리 실패"),
    RESULT_NOT_EXIST(HttpStatus.BAD_REQUEST,"AI-007", "결과가 존재하지 않음");


    // 상태, 에러 코드, 메시지
    private HttpStatus httpStatus;
    private String errorCode;
    private String message;

    // 생성자
    ErrorCode(HttpStatus httpStatus, String errorCode, String message) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.message = message;
    }
}