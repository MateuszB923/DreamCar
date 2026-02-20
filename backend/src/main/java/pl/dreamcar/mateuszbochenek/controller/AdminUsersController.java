package pl.dreamcar.mateuszbochenek.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.dreamcar.mateuszbochenek.dto.AdminUsersResponse;
import pl.dreamcar.mateuszbochenek.dto.ResetPasswordResponse;
import pl.dreamcar.mateuszbochenek.service.AdminUsersService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users")
public class AdminUsersController {

    private final AdminUsersService adminUsersService;

    @GetMapping
    public List<AdminUsersResponse> listAllUsers() {
        return adminUsersService.listUsers();
    }

    @PatchMapping("/{id}/block")
    public ResponseEntity<?> block(@PathVariable Long id, Authentication authentication) {
        adminUsersService.blockUser(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/unblock")
    public ResponseEntity<?> unblock(@PathVariable Long id) {
        adminUsersService.unblockUser(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, Authentication authentication) {
        adminUsersService.deleteUser(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reset-password")
    public ResetPasswordResponse resetPassword(@PathVariable Long id) {
        return adminUsersService.resetPassword(id);
    }
}
