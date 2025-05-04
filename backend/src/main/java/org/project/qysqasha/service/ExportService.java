package org.project.qysqasha.service;

import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.xslf.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * Создает материал в формате PDF или PPTX
     */
    public String createMaterial(String content, String format, String fileId) {
        try {
            String filePath;

            switch (format.toLowerCase()) {
                case "pdf":
                    filePath = createPdf(content, fileId);
                    break;
                case "pptx":
                    filePath = createPptx(content, fileId);
                    break;
                default:
                    filePath = createPdf(content, fileId);  // По умолчанию PDF
            }

            return "/api/files/download/" + fileId;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Создает PDF документ
     */
    private String createPdf(String content, String fileId) throws IOException {
        String filePath = uploadDir + File.separator + fileId + ".pdf";

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(50, 700);
                contentStream.setLeading(14.5f);

                // Разбиваем контент на строки
                String[] lines = content.split("\n");
                for (String line : lines) {
                    contentStream.showText(line);
                    contentStream.newLine();
                }

                contentStream.endText();
            }

            document.save(filePath);
        }

        return filePath;
    }

    /**
     * Создает PPTX презентацию
     */
    private String createPptx(String content, String fileId) throws IOException {
        String filePath = uploadDir + File.separator + fileId + ".pptx";

        try (XMLSlideShow ppt = new XMLSlideShow()) {
            // Парсим контент, разделяем на слайды
            // Предполагается, что контент имеет структуру с разделителями слайдов
            List<String> slides = parseContentToSlides(content);

            for (String slideContent : slides) {
                XSLFSlideMaster defaultMaster = ppt.getSlideMasters().get(0);
                XSLFSlideLayout layout = defaultMaster.getLayout(SlideLayout.TITLE_AND_CONTENT);
                XSLFSlide slide = ppt.createSlide(layout);

                // Если есть заголовок и содержимое
                String[] parts = slideContent.split("\n", 2);
                String title = parts[0].trim();
                String body = parts.length > 1 ? parts[1].trim() : "";

                // Добавляем заголовок
                XSLFTextShape titleShape = slide.getPlaceholder(0);
                if (titleShape != null) {
                    titleShape.setText(title);
                }

                // Добавляем содержимое
                XSLFTextShape bodyShape = slide.getPlaceholder(1);
                if (bodyShape != null) {
                    bodyShape.clearText();

                    XSLFTextParagraph paragraph = bodyShape.addNewTextParagraph();
                    String[] bodyLines = body.split("\n");

                    for (String line : bodyLines) {
                        if (line.trim().startsWith("- ")) {
                            // Маркированный список
                            XSLFTextParagraph bulletParagraph = bodyShape.addNewTextParagraph();
                            bulletParagraph.setBullet(true);
                            XSLFTextRun bulletRun = bulletParagraph.addNewTextRun();
                            bulletRun.setText(line.trim().substring(2));
                        } else {
                            // Обычный текст
                            XSLFTextRun run = paragraph.addNewTextRun();
                            run.setText(line);
                            paragraph.addLineBreak();
                        }
                    }
                }
            }

            // Сохраняем презентацию
            try (FileOutputStream out = new FileOutputStream(filePath)) {
                ppt.write(out);
            }
        }

        return filePath;
    }

    /**
     * Разбивает контент на слайды
     */
    private List<String> parseContentToSlides(String content) {
        // Предполагаем, что слайды разделены строкой "--- SLIDE ---" или похожим образом
        String[] slides = content.split("(?i)--- SLIDE ---|===SLIDE===|\\*\\*\\* NEW SLIDE \\*\\*\\*");
        return Arrays.asList(slides);
    }
}
