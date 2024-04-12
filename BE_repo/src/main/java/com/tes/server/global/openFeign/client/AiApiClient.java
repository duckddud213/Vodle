package com.tes.server.global.openFeign.client;

import com.tes.server.global.openFeign.config.OpenFeignConfig;
import com.tes.server.domain.vodle.dto.request.VodleTtsReqDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(url = "${ai.api.url}", name = "ai", configuration = OpenFeignConfig.class)
public interface AiApiClient {


    @PostMapping(value = "/api/ai/stt",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String stt(@RequestPart("audio_file") MultipartFile audioFile);


    @PostMapping(value = "/api/ai/tts",consumes = "application/json", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    byte[] tts(@RequestBody VodleTtsReqDto ttsReqDto);


    @PostMapping(value = "/api/ai/conversion/{selected_voice}/{pitch_change}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    byte[] voiceConversion(@PathVariable("selected_voice") String selectedVoice,
                           @PathVariable("pitch_change") Integer pitchChange,
                           @RequestPart("audio_file") MultipartFile audioFile);

    @PostMapping("api/ai/classification")
    String classifyText(@RequestParam("text") String text);
}
