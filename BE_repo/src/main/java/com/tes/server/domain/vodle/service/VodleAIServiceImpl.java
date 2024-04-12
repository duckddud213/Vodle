package com.tes.server.domain.vodle.service;

import com.tes.server.global.exception.ErrorCode;
import com.tes.server.global.exception.Exceptions;
import com.tes.server.global.openFeign.client.AiApiClient;
import com.tes.server.domain.vodle.dto.request.VodleTtsReqDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class VodleAIServiceImpl implements VodleAIService{

    private final AiApiClient aiApiClient;

    @Async
    public CompletableFuture<String> asyncStt(MultipartFile audioFile) {
        log.info("STT 호출");
        return CompletableFuture.supplyAsync(() -> {
            try {
                return aiApiClient.stt(audioFile);
            } catch (Exception e) {
                log.error("STT 호출 중 에러 발생", e);
                throw new Exceptions(ErrorCode.STT_PROCESSING_FAIL);
            }
        }).exceptionally(e -> {
            // 여기에서 예외를 처리하고 대체 결과를 제공할 수 있습니다.
            log.error("비동기 STT 처리 중 예외 발생", e);
            throw new Exceptions(ErrorCode.ASYNC_PROCESSING_FAIL);
        });
    }

    @Async
    public CompletableFuture<byte[]> asyncTts(VodleTtsReqDto vodleTtsReqDto) {
        log.info("TTS 호출: selected_voice = {}", vodleTtsReqDto.getSelectedVoice());
        return CompletableFuture.supplyAsync(() -> {
            try {
                return aiApiClient.tts(vodleTtsReqDto);
            } catch (Exception e) {
                log.error("TTS 호출 중 에러 발생", e);
                throw new Exceptions(ErrorCode.TTS_PROCESSING_FAIL);
            }
        }).exceptionally(e -> {
            // 예외 처리 및 대체 결과 제공
            log.error("비동기 TTS 처리 중 예외 발생", e);
            throw new Exceptions(ErrorCode.ASYNC_PROCESSING_FAIL);
        });
    }

    @Async
    public CompletableFuture<byte[]> asyncVoiceConversion(String selectedVoice, Integer pitchChange, MultipartFile audioFile) {
        log.info("Voice 변환 호출");
        return CompletableFuture.supplyAsync(() -> {
            try {
                return aiApiClient.voiceConversion(selectedVoice, pitchChange, audioFile);
            } catch (Exception e) {
                log.error("Voice 변환 호출 중 에러 발생", e);
                throw new Exceptions(ErrorCode.CONVERSION_PROCESSING_FAIL);
            }
        }).exceptionally(e -> {
            // 예외를 처리하고 빈 배열을 반환하거나 새로운 예외를 던집니다.
            log.error("비동기 Voice 변환 처리 중 예외 발생", e);
            throw new Exceptions(ErrorCode.ASYNC_PROCESSING_FAIL);
        });
    }


    @Async
    public CompletableFuture<String> asyncClassifyText(String text) {
        log.info("분류 호출: text = {}", text);
        return CompletableFuture.supplyAsync(() -> {
            try {
                return aiApiClient.classifyText(text);
            } catch (Exception e) {
                log.error("분류 호출 중 에러 발생", e);
                throw new Exceptions(ErrorCode.CLASSIFY_PROCESSING_FAIL);
            }
        }).exceptionally(e -> {
            // 예외 처리 및 대체 결과 제공
            log.error("비동기 분류 처리 중 예외 발생", e);
            throw new Exceptions(ErrorCode.ASYNC_PROCESSING_FAIL);
        });
    }
}
