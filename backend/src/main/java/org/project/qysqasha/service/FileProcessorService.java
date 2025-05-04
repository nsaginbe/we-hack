package org.project.qysqasha.service;

import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.project.qysqasha.model.response.FileUploadResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileProcessorService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final Map<String, String> fileIdToPathMap = new HashMap<>();
    private final Map<String, String> fileIdToContentTypeMap = new HashMap<>();

    /**
     * Обрабатывает загруженный файл
     */
    public FileUploadResponse processFile(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (Files.notExists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String ext = getFileExtension(originalFilename);
        String fileId = UUID.randomUUID().toString();
        String storedFilename = fileId + "." + ext;

        Path filePath = uploadPath.resolve(storedFilename);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        fileIdToPathMap.put(fileId, filePath.toString());
        fileIdToContentTypeMap.put(fileId, file.getContentType());

        String extractedText = extractText(
                file,
                filePath.toString(),
                ext
        );

        return FileUploadResponse.builder()
                .fileId(fileId)
                .originalFilename(originalFilename)
                .extractedText(extractedText)
                .build();
    }

    /**
     * Извлекает текст из файла в зависимости от формата
     */
    private String extractText(MultipartFile file, String filePath, String fileExtension) throws IOException {
        switch (fileExtension.toLowerCase()) {
            case "pdf":
                return extractTextFromPdf(filePath);
            case "pptx":
                return extractTextFromPptx(filePath);
            case "txt":
                return new String(file.getBytes());
            default:
                return "Формат файла не поддерживается для извлечения текста.";
        }
    }

    /**
     * Извлекает текст из PDF
     */
    private String extractTextFromPdf(String filePath) throws IOException {
        try (PDDocument document = PDDocument.load(new File(filePath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    /**
     * Извлекает текст из PPTX
     */
    private String extractTextFromPptx(String filePath) throws IOException {
        StringBuilder text = new StringBuilder();

        try (XMLSlideShow ppt = new XMLSlideShow(Files.newInputStream(Paths.get(filePath)))) {
            ppt.getSlides().forEach(slide -> {
                for (XSLFShape shape : slide.getShapes()) {
                    if (shape instanceof XSLFTextShape) {
                        XSLFTextShape textShape = (XSLFTextShape) shape;
                        text.append(textShape.getText()).append("\n");
                    }
                }
                text.append("\n---\n");
            });
        }

        return text.toString();
    }

    /**
     * Получает текст файла по его ID
     */
    public String getFileText(String fileId) {
        String filePath = fileIdToPathMap.get(fileId);
        if (filePath == null) {
            return "";
        }

        try {
            String fileExtension = getFileExtension(filePath);
            switch (fileExtension.toLowerCase()) {
                case "pdf":
                    return extractTextFromPdf(filePath);
                case "pptx":
                    return extractTextFromPptx(filePath);
                case "txt":
                    return Files.readString(Path.of(filePath));
                default:
                    return "Формат файла не поддерживается для извлечения текста.";
            }
        } catch (IOException e) {
            return "Ошибка при чтении файла: " + e.getMessage();
        }
    }

    /**
     * Получает содержимое файла по его ID для скачивания
     */
    public byte[] getFileContent(String fileId) throws IOException {
        String filePath = fileIdToPathMap.get(fileId);
        if (filePath == null) {
            throw new IOException("Файл не найден");
        }

        return Files.readAllBytes(Path.of(filePath));
    }

    /**
     * Получает тип содержимого файла по его ID
     */
    public String getFileContentType(String fileId) {
        return fileIdToContentTypeMap.getOrDefault(fileId, "application/octet-stream");
    }

    /**
     * Получает расширение файла из его имени
     */
    private String getFileExtension(String filename) {
        if (filename == null) return "";
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) return "";
        return filename.substring(lastDotIndex + 1);
    }
}
