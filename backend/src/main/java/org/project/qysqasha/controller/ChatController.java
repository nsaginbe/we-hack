package org.project.qysqasha.controller;


import lombok.RequiredArgsConstructor;
import org.project.qysqasha.model.request.ChatRequest;
import org.project.qysqasha.model.response.ChatResponse;
import org.project.qysqasha.service.MaterialGeneratorService;
import org.project.qysqasha.service.NoteGeneratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final NoteGeneratorService noteGeneratorService;
    private final MaterialGeneratorService materialGeneratorService;

    @PostMapping("/message")
    public ResponseEntity<ChatResponse> processMessage(@RequestBody ChatRequest request) {
        ChatResponse response;

        // Обработка различных типов запросов
        switch (request.getType()) {
            case NOTES:
                response = noteGeneratorService.generateNotes(request);
                break;
            case MATERIAL:
                response = materialGeneratorService.generateMaterial(request);
                break;
            default:
                response = ChatResponse.builder()
                        .type(ChatResponse.ResponseType.TEXT)
                        .message("Пожалуйста, выберите тип генерации: конспект или учебный материал.")
                        .build();
        }

        return ResponseEntity.ok(response);
    }
}
