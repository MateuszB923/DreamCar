package pl.dreamcar.mateuszbochenek.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.dreamcar.mateuszbochenek.dto.AdminUsersResponse;
import pl.dreamcar.mateuszbochenek.dto.ResetPasswordResponse;
import pl.dreamcar.mateuszbochenek.exception.NotFoundException;
import pl.dreamcar.mateuszbochenek.model.User;
import pl.dreamcar.mateuszbochenek.repository.UserRepository;
import java.security.SecureRandom;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUsersService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final SecureRandom RNG = new SecureRandom();

    @Transactional(readOnly = true)
    public List<AdminUsersResponse> listUsers() {

        return userRepository.findAllByOrderByIdAsc().stream()
                .map(user -> new AdminUsersResponse(user.getId(), user.getEmail(), user.getRole(), user.isLocked()))
                .toList();
    }

    @Transactional
    public void blockUser(Long id, String adminEmail) {
        User user = getUserOrThrow(id);
        if (user.getEmail().equalsIgnoreCase(adminEmail)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nie możesz zablokować swojego konta");
        }
        user.setLocked(true);
        userRepository.save(user);
    }

    @Transactional
    public void unblockUser(Long id) {
        User user = getUserOrThrow(id);
        user.setLocked(false);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id, String adminEmail) {
        User user = getUserOrThrow(id);
        if (user.getEmail().equalsIgnoreCase(adminEmail)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nie możesz usunąć swojego konta");
        }
        userRepository.delete(user);
    }

    @Transactional
    public ResetPasswordResponse resetPassword(Long id) {
        User user = getUserOrThrow(id);

        String tmpPassword = generateTemporaryPassword(10);

        if (!AuthService.isValidPassword(tmpPassword)) {
            tmpPassword = "TmpA1!" + randomLetters(6);
        }

        user.setPasswordHash(passwordEncoder.encode(tmpPassword));
        userRepository.save(user);

        return new ResetPasswordResponse(tmpPassword);
    }

    private static String generateTemporaryPassword(int length) {
        if (length < 8) length = 8;

        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&*";

        String all = upper + lower + digits + special;

        StringBuilder sb = new StringBuilder(length);
        sb.append(pick(upper));
        sb.append(pick(digits));
        sb.append(pick(special));

        while (sb.length() < length) {
            sb.append(pick(all));
        }

        char[] chars = sb.toString().toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = RNG.nextInt(i + 1);
            char tmp = chars[i];
            chars[i] = chars[j];
            chars[j] = tmp;
        }
        return new String(chars);
    }

    private static char pick(String s) {
        return s.charAt(RNG.nextInt(s.length()));
    }

    private static String randomLetters(int n) {
        String lower = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            sb.append(pick(lower));
        }
        return sb.toString();
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("user not found: " + id));
    }

}
