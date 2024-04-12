package com.tes.server.global.openFeign.multipart;


import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Path;


public class CustomMultipartFile implements MultipartFile {
    private final byte[] fileContent;
    private final String fileName;

    public CustomMultipartFile(byte[] fileContent, String fileName) {
        this.fileContent = fileContent;
        this.fileName = fileName;
    }

    @Override
    public Resource getResource() {
        return MultipartFile.super.getResource();
    }

    @Override
    public void transferTo(Path dest) throws IOException, IllegalStateException {
        MultipartFile.super.transferTo(dest);
    }

    @Override
    public String getName() {
        return fileName;
    }

    @Override
    public String getOriginalFilename() {
        return fileName;
    }

    @Override
    public String getContentType() {
        return "audio/wav"; // MIME 타입을 정확하게 지정하세요.
    }

    @Override
    public boolean isEmpty() {
        return fileContent.length == 0;
    }


    @Override
    public long getSize() {
        return fileContent.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return fileContent;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(fileContent);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(dest)) {
            fileOutputStream.write(fileContent);
        }
    }
}
