package com.tes.server.domain.vodle.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.tes.server.domain.vodle.dto.request.VodleCreateReqDto;
import com.tes.server.domain.vodle.dto.request.VodleLocationReqDto;
import com.tes.server.domain.vodle.dto.response.*;
import com.tes.server.domain.vodle.service.VodleService;
import com.tes.server.domain.user.detail.CustomUserDetails;
import com.tes.server.global.Base.BaseResponseBody;
import com.tes.server.global.Base.DataResponseBody;
import com.tes.server.global.exception.ErrorCode;
import com.tes.server.global.exception.Exceptions;
import com.tes.server.domain.vodle.dto.request.VodleTtsReqDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Tag(name = "2. 음성", description = "음성 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/vodle")
@Slf4j
public class VodleController {

    private final VodleService vodleService;

    // 음성 등록
    @Operation(summary = "음성 낙서 등록", description = "\n\n사용자는 해당 위치에서 음성 낙서를 등록 가능하다.")
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(
            responseCode = "201", description = "성공 \n\n Success 반환",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BaseResponseBody.class),
                    examples = {
                            @ExampleObject(name = "음성 낙서 등록 성공", description = "사용자는 음성 낙서 등록 요청을 통해 위와 같은 응답 데이터를 받는다.", value = """
                                    {
                                        "status": 201,
                                        "message": "음성 낙서 등록 완료",
                                        "code": "SUCCESS"
                                    }
                                    """),
                            @ExampleObject(name = "음성 낙서 등록 실패", description = "사용자는 음성 낙서 등록 실패시 위와 같은 응답 데이터를 받는다.", value = """
                                    {
                                        "status": 400,
                                        "message": "음성 낙서 등록이 실패",
                                        "code": "F-001"
                                    }
                                    """),
                    })
    )
    public ResponseEntity<? extends BaseResponseBody> createVodle(
            @Parameter(name = "soundFile", description = "음성 낙서 파일")
            @RequestPart(value = "soundFile") MultipartFile soundFile,
            @Parameter(name = "vodleCreateReqDto", description = """
                    음성 낙서에 대한 정보
                                
                    1.작성자에 대한 정보 : String writer
                                        
                    2.기록 종류에 대한 정보 : String recordType ex) "TTS", "STS", "NONE"
                                                
                    3.변조된 음성 스트리밍 URL : String streamingURL
                                
                    4.위도에 대한 정보 : Double longitude
                                        
                    5.경도에 대한 정보 : Double latitude
                    """)
            @RequestPart(value = "vodleCreateReqDto") VodleCreateReqDto vodleCreateReqDto,
            @AuthenticationPrincipal CustomUserDetails user) {
        // 만약 공백이거나 공백으로 시작하게 된다면, 익명으로 생성
        if(vodleCreateReqDto.getWriter().equals("") || vodleCreateReqDto.getWriter().startsWith(" ")) vodleCreateReqDto.setWriter("익명");

        log.info("음성 낙서 등록 URL 매핑 : {}", "OK");

        log.info("음성 낙서 등록 시작 : {}", "START");
        vodleService.createVodle(user.getUserEntity().getUserCode(), vodleCreateReqDto, soundFile);
        log.info("음성 낙서 등록 완료 : {}", "COMPLETE");

        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponseBody.of(201, "음성 낙서 등록 완료", "SUCCESS"));
    }

    // 음성 다운로드
    @Operation(summary = "음성 낙서 다운로드", description = "\n\n사용자는 해당 위치에서 음성 낙서를 다운로드 가능하다.")
    @GetMapping("/{contentId}/download")
    @ApiResponse(responseCode = "200", description = "성공 \n\n 음성 파일 반환",
            content = @Content(mediaType = "application/octet-stream",
                    schema = @Schema(implementation = Object.class),
                    examples = {
                            @ExampleObject(name = "음성 낙서 다운로드 성공", description = "사용자는 음성 다운로드 요청을 통해 위와 같은 응답 데이터를 받는다.", value = "음성 파일 다운로드 수행 (바이트 배열)"),
                            @ExampleObject(name = "음성 낙서 다운로드 실패", description = "사용자는 음성 다운로드 요청 실패시 통해 위와 같은 응답 데이터를 받는다.", value = """
                                    {
                                        "status": 400,
                                        "message": "음성 낙서 다운로드를 실패.",
                                        "code": "F-002
                                    }
                                    """),
                            @ExampleObject(name = "음성 낙서 조회 실패", description = "사용자는 음성 조회 실패시 위와 같은 응답 데이터를 받는다.", value = """
                                    {
                                        "status": 400,
                                        "message": "음성 낙서 데이터가 존재하지 않음",
                                        "code": "V-001
                                    }
                                    """)
                    })
    )
    public ResponseEntity<byte[]> downloadVodle(
            @Parameter(name = "contentId", description = "다운로드하려는 파일의 식별 ID")
            @PathVariable(name = "contentId") Long contentId) throws IOException {

        log.info("음성 낙서 다운로드 URL 매핑 : {}", "OK");

        log.info("음성 낙서 다운로드 시작 : {}", "START");
        VodleDownloadResDto vodleDownloadResDto = vodleService.downloadVodle(contentId);

        log.info("음성 낙서 다운로드 HTTP 헤더 설정 시작 : {}", "... ING ...");
        // HTTP 헤더 값 설정
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentLength(vodleDownloadResDto.getDownloadSoundBytes().length);
        httpHeaders.setContentDispositionFormData("attachment", vodleDownloadResDto.getDownloadSoundName());
        log.info("음성 낙서 다운로드 HTTP 헤더 설정 완료 : {}", "... COMPLETE ...");

        log.info("음성 낙서 다운로드 완료 : {}", "COMPLETE");

        return ResponseEntity.status(HttpStatus.OK).headers(httpHeaders).
                body(vodleDownloadResDto.getDownloadSoundBytes());
    }

    // 음성 스트리밍
    @Operation(summary = "음성 낙서 스트리밍", description = "\n\n사용자는 해당 위치에서 음성 낙서를 스트리밍 가능하다.")
    @GetMapping("/{contentId}/streaming")
    @ApiResponse(responseCode = "200", description = "성공 \n\n 음성 스트리밍 URL 반환",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DataResponseBody.class),
                    examples = {
                            @ExampleObject(name = "음성 낙서 스트리밍 성공", description = "사용자는 음성 스트리밍 요청을 통해 위와 같은 응답 데이터를 받는다.", value = """
                                    {
                                        "status": 200,
                                        "message": "음성 낙서 스트리밍 완료",
                                        "code": "SUCCESS",
                                        "data": "https://ssafy.com/vodle-streaming.m3u8"
                                    }
                                    """),
                            @ExampleObject(name = "음성 낙서 조회 실패", description = "사용자는 음성 낙서 조회 실패시 위와 같은 응답 데이터를 받는다.", value = """
                                    {
                                            "status": 400,
                                            "message": "음성 낙서 데이터가 존재하지 않음",
                                            "code": "V-001"
                                    }
                                    """)
                    })
    )
    public ResponseEntity<? extends DataResponseBody> getStreamingURLVodle(
            @Parameter(name = "contentId", description = "스트리밍하려는 파일의 식별 ID")
            @PathVariable(name = "contentId") Long contentId) {

        log.info("음성 낙서 스트리밍 URL 매핑 : {}", "OK");

        log.info("음성 낙서 스트리밍 URL 경로 찾기 시작 : {}", "START");
        String streamingURL = vodleService.getStreamingURLVodle(contentId);
        log.info("음성 낙서 스트리밍 URL 경로 찾기 완료 : {}", "COMPLETE");

        return ResponseEntity.status(HttpStatus.OK).body(DataResponseBody.of(200, "음성 낙서 스트리밍 완료", "SUCCESS", streamingURL));
    }

    // 모든 음성 조회
    @Operation(summary = "모든 음성 낙서 리스트 조회", description = "\n\n사용자는 모든 음성 낙서 리스트 조회 가능하다.")
    @GetMapping("/all")
    @ApiResponse(responseCode = "200", description = "성공 \n\n 모든 음성 낙서 리스트 반환",
        content = {@Content(mediaType = "application/json",
            schema = @Schema(implementation = DataResponseBody.class),
            examples = {
                @ExampleObject(name = "모든 음성 낙서 리스트 조회 성공", description = "사용자는 모든 음성 조회를 통해 위와 같은 응답 데이터를 받는다.", value = """
                    {
                        "status": 200,
                        "message": "모든 음성 낙서 리스트 조회 완료",
                        "code": "SUCCESS",
                        "data":
                            [
                                {
                                    "vodleId": 10,
                                    "writer": "신영한",
                                    "contentType": "SMALLTALK",
                                    "fileOriginName": "그거알아?.m4a",
                                    "streamingURL": "https://STREAMING_URL.net/SOUND_FILE.m3u8",
                                    "address": "경북 구미시 진평길 79-3",
                                    "latitude": 128.406,
                                    "longitude": 36.1075,
                                    "createdDate": "2021년 11월 8일 11시 44분"
                                }
                            ]
                    }
                    """),
                @ExampleObject(name = "음성 낙서 조회 실패", description = "사용자는 음성 낙서 조회 실패시 위와 같은 응답 데이터를 받는다.", value = """
                    {
                        "status": 400,
                        "message": "음성 낙서 데이터가 존재하지 않음",
                        "code": "V-001"
                    }
                    """)
            })
        })
    public ResponseEntity<? extends BaseResponseBody> getAllListVodle(
    ) throws IOException {
        log.info("모든 음성 낙서 리스트 조회 URL 매핑 : {}", "OK");
        log.info("모든 음성 낙서 리스트 조회 시작 : {}", "START");

        List<VodleGetResDto> vodleGetResDtoList = vodleService.getAllListVodle();

        log.info("모든 음성 낙서 리스트 조회 완료 : {}", "COMPLETE");

        if (vodleGetResDtoList.size() == 0) throw new Exceptions(ErrorCode.VODLE_NOT_EXIST_DB);

        return ResponseEntity.status(HttpStatus.OK).body(DataResponseBody.of(200, "모든 음성 낙서 리스트 조회 완료", "SUCCESS", vodleGetResDtoList));
    }


    @Operation(summary = "tts api 호출", description = "ai서버의 tts api를 호출한다")
    @ApiResponse(responseCode = "200", description = "성공 \n\n tts 성공",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = DataResponseBody.class),
                    examples = {
                            @ExampleObject(name = "tts 성공", description = "사용자는 tts 음성파일&카테고리를 받는다.", value = """
                {
                    "status": 200,
                    "message": "tts 성공",
                    "code": "SUCCESS",
                    "data": {
                        "convertedFileUrl": "https://STREAMING_URL.net/SOUND_FILE.m3u8",
                        "selectedVoice": "ahri"
                    }
                }
                """),
                            @ExampleObject(name = "tts 실패", description = "사용자는 tts 실패시 위와 같은 응답 데이터를 받는다.", value = """
                {
                    "status": 400,
                    "message": "결과가 존재하지 않음",
                    "code": "AI-007"
                }
                """)
                    })
            })
    @PostMapping(value = "/tts")
    public ResponseEntity<? extends DataResponseBody> callTtsApi(
            @RequestBody VodleTtsReqDto vodleTtsReqDto) throws IOException {

        VodleTtsResDto vodleTtsResDtos = vodleService.callTts(vodleTtsReqDto);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(DataResponseBody.of(200, "OK", "SUCCESS", vodleTtsResDtos));
    }

    @Operation(summary = "conversion api 호출", description = "ai서버의 conversion api를 호출한다")
    @ApiResponse(responseCode = "200", description = "성공 \n\n 음성변조 성공",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = DataResponseBody.class),
                    examples = {
                            @ExampleObject(name = "음성변조 성공", description = "사용자는 음성변조를하고 음성파일&카테고리를 받는다.", value = """
                {
                    "status": 200,
                    "message": "음성변조 성공",
                    "code": "SUCCESS",
                    "data": {
                        "convertedFileUrl": "https://STREAMING_URL.net/SOUND_FILE.m3u8",
                        "selectedVoice": "ahri"
                    }       
                }
                """),
                            @ExampleObject(name = "음성변조 실패", description = "사용자는 음성변조 실패시 위와 같은 응답 데이터를 받는다.", value = """
                {
                    "status": 400,
                    "message": "결과가 존재하지 않음",
                    "code": "AI-007"
                }
                """)
                    })
            })
    @PostMapping(value = "/conversion/{selected_voice}/{gender}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<? extends DataResponseBody> callConversionApi(
            @Parameter(name = "selected_voice", description = "목소리의 종류는 ahri, mundo, trump, optimusPrime, elsa 입니다.")
            @PathVariable("selected_voice") String selectedVoice,
            @Parameter(name = "gender", description = "성별은 male, female 둘 중 하나를 받는다")
            @PathVariable("gender") String gender,
            @RequestPart("sound_file") MultipartFile soundFile) {

        VodleConversioinResDto vodleConversioinResDto = vodleService.callConversion(selectedVoice, gender, soundFile);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(DataResponseBody.of(200, "음성 변조 성공", "SUCCESS", vodleConversioinResDto));
    }


    @Operation(summary = "tts api 호출", description = "ai서버의 tts api를 비동기로 호출한다")
    @ApiResponse(responseCode = "200", description = "성공 \n\n tts 성공",
        content = {@Content(mediaType = "application/json",
            schema = @Schema(implementation = DataResponseBody.class),
            examples = {
                @ExampleObject(name = "tts 성공", description = "사용자는 tts 음성파일&카테고리를 받는다.", value = """
                    {
                        "status": 200,
                        "message": "tts 성공",
                        "code": "SUCCESS",
                        "data":
                            [
                                {
                                    "convertedFileUrl": "https://STREAMING_URL.net/SOUND_FILE.m3u8",
                                    "selectedVoice": "ahri",
                                },
                                {
                                    "convertedFileUrl": "https://STREAMING_URL.net/SOUND_FILE.m3u8",
                                    "selectedVoice": "mundo",
                                },
                                {
                                    "convertedFileUrl": "https://STREAMING_URL.net/SOUND_FILE.m3u8",
                                    "selectedVoice": "elsa",
                                },
                                {
                                    "convertedFileUrl": "https://STREAMING_URL.net/SOUND_FILE.m3u8",
                                    "selectedVoice": "optimusPrime",
                                },
                                {
                                    "convertedFileUrl": "https://STREAMING_URL.net/SOUND_FILE.m3u8",
                                    "selectedVoice": "trump",
                                }
                            ]
                    }
                    """),
                @ExampleObject(name = "tts 실패", description = "사용자는 tts 실패시 위와 같은 응답 데이터를 받는다.", value = """
                    {
                        "status": 400,
                        "message": "결과가 존재하지 않음",
                        "code": "AI-007"
                    }
                    """)
            })
        })
    @PostMapping(value = "/tts/list")
    public ResponseEntity<? extends DataResponseBody> callTtsAsyncApi(
            @Parameter(name = "content", description = "TTS를 실행할 텍스트")
            @RequestBody VodleTtsReqDto vodleTtsReqDto) throws IOException {

        List<VodleTtsResDto> vodleTtsResDtos = vodleService.callTtsAsync(vodleTtsReqDto);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(DataResponseBody.of(200, "OK", "SUCCESS", vodleTtsResDtos));
    }

    @Operation(summary = "conversion api 호출", description = "ai서버의 conversion api를 비동기로 호출한다")
    @ApiResponse(responseCode = "200", description = "성공 \n\n 음성변조 성공",
        content = {@Content(mediaType = "application/json",
            schema = @Schema(implementation = DataResponseBody.class),
            examples = {
                @ExampleObject(name = "음성변조 성공", description = "사용자는 음성변조를하고 음성파일&카테고리를 받는다.", value = """
                    {
                        "status": 200,
                        "message": "음성변조 성공",
                        "code": "SUCCESS",
                        "data":
                            [
                                {
                                    "convertedFileUrl": "https://STREAMING_URL.net/SOUND_FILE.m3u8",
                                    "selectedVoice": "ahri",
                                },
                                {
                                    "convertedFileUrl": "https://STREAMING_URL.net/SOUND_FILE.m3u8",
                                    "selectedVoice": "mundo",
                                },
                                {
                                    "convertedFileUrl": "https://STREAMING_URL.net/SOUND_FILE.m3u8",
                                    "selectedVoice": "elsa",
                                },
                                {
                                    "convertedFileUrl": "https://STREAMING_URL.net/SOUND_FILE.m3u8",
                                    "selectedVoice": "optimusPrime",
                                },
                                {
                                    "convertedFileUrl": "https://STREAMING_URL.net/SOUND_FILE.m3u8",
                                    "selectedVoice": "trump",
                                }
                            ]
                    }
                    """),
                @ExampleObject(name = "음성변조 실패", description = "사용자는 음성변조 실패시 위와 같은 응답 데이터를 받는다.", value = """
                    {
                        "status": 400,
                        "message": "결과가 존재하지 않음",
                        "code": "AI-007"
                    }
                    """)
            })
        })
    @PostMapping(value = "/conversion/list/{gender}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<? extends DataResponseBody> callConversionApiTest(
            @Parameter(name = "gender", description = "성별은 male, female 둘 중 하나를 받는다")
            @PathVariable("gender") String gender,
            @RequestPart("sound_file") MultipartFile soundFile) {

        List<VodleConversioinResDto> vodleConversioinResDtos = vodleService.callConversionAsync(gender, soundFile);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(DataResponseBody.of(200, "음성 변조 성공", "SUCCESS", vodleConversioinResDtos));
    }


    // 범위 내 음성 조회
    @Operation(summary = "범위 내 음성 낙서 리스트 조회", description = "\n\n사용자는 범위 내 음성 낙서 리스트 조회가 가능하다.")
    @PostMapping("/search")
    @ApiResponse(responseCode = "200", description = "성공 \n\n 범위 내 음성 낙서 리스트 반환",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = DataResponseBody.class),
                    examples = {
                            @ExampleObject(name = "범위 내 음성 낙서 리스트 조회 성공", description = "사용자는 범위 내 음성 낙서 리스트 조회를 통해 위와 같은 응답 데이터를 받는다.", value = """
                                    {
                                        "status": 200,
                                        "message": "범위 내 음성 낙서 리스트 조회 완료",
                                        "code": "SUCCESS",
                                        "data": [
                                                    {
                                                        "vodleId": 10,
                                                        "writer": "신영한",
                                                        "contentType": "SMALLTALK",
                                                        "fileOriginName": "그거알아?.m4a",
                                                        "streamingURL": "https://STREAMING_URL.net/SOUND_FILE.m3u8",
                                                        "address": "경북 구미시 진평길 79-3",
                                                        "latitude": 128.406,
                                                        "longitude": 36.1075,
                                                        "createdDate": "2021년 11월 8일 11시 44분"
                                                    }
                                                ]
                                    }
                                    """),
                            @ExampleObject(name = "음성 낙서 조회 실패", description = "사용자는 음성 낙서 조회 실패시 위와 같은 응답 데이터를 받는다.", value = """
                                    {
                                            "status": 400,
                                            "message": "음성 낙서 데이터가 존재하지 않음",
                                            "code": "V-001"
                                    }
                                    """)
                    })

    })
    public ResponseEntity<? extends DataResponseBody> getListVodle(
            @RequestBody VodleLocationReqDto vodleLocationReqDto) {

        log.info("범위 내 음성 낙서 리스트 조회 URL 매핑 : {}", "OK");
        log.info("범위 내 음성 낙서 리스트 조회 시작 : {}", "START");

        List<VodleGetResDto> vodleGetResDtoList = vodleService.getListVodle(vodleLocationReqDto);

        log.info("범위 내 음성 낙서 리스트 조회 완료 : {}", "COMPLETE");

        return ResponseEntity.status(HttpStatus.OK).body(DataResponseBody.of(200, "범위 내 음성 낙서 리스트 조회 완료", "SUCCESS", vodleGetResDtoList));
    }
}