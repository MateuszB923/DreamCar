package pl.dreamcar.mateuszbochenek.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dreamcar.mateuszbochenek.dto.AdminMessageResponse;
import pl.dreamcar.mateuszbochenek.dto.UpdateMessageStatusRequest;
import pl.dreamcar.mateuszbochenek.model.MessageStatus;
import pl.dreamcar.mateuszbochenek.service.AdminMessagesService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/messages")
public class AdminMessageController {

    private final AdminMessagesService adminMessagesService;

    @GetMapping
    public List<AdminMessageResponse> listMsgs(@RequestParam(required = false) MessageStatus status) {
        return adminMessagesService.listMessages(status);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateMessageStatusRequest request) {
        adminMessagesService.updateMessageStatus(id, request.status());
        return ResponseEntity.noContent().build();
    }
}
