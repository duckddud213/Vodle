package com.tes.server.domain.vodle.service;

import com.tes.server.domain.vodle.dto.request.VodleTtsReqDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;


public interface VodleAIService {

    public CompletableFuture<String> asyncStt(MultipartFile audioFile);

    public CompletableFuture<byte[]> asyncTts(VodleTtsReqDto vodleTtsReqDto);

    public CompletableFuture<byte[]> asyncVoiceConversion(String selectedVoice, Integer pitchChange, MultipartFile audioFile);

    public CompletableFuture<String> asyncClassifyText(String text);

}
