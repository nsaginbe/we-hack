package org.project.qysqasha.controller;


import lombok.RequiredArgsConstructor;
import org.project.qysqasha.model.response.FileUploadResponse;
import org.project.qysqasha.service.FileProcessorService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileProcessorService fileProcessorService;

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            FileUploadResponse response = fileProcessorService.processFile(file);

            return ResponseEntity.ok(response);
        }
        catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileId) {
        try {
            byte[] fileContent = fileProcessorService.getFileContent(fileId);
            String contentType = fileProcessorService.getFileContentType(fileId);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(fileContent);
        }
        catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
