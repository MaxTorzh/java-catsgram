package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final Map<String, User> users = new HashMap<>();

    public Collection<User> findAll() {
        return users.values()
                .stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }

    public Optional<User> findUserById(Long id) {
        return users.values()
                .stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    public User create(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }
        if (users.containsKey(user.getEmail())) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        users.put(user.getEmail(), user);
        return user;
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        User existingUser = users.values()
                .stream()
                .filter(user -> user.getId().equals(newUser.getId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + newUser.getId() + " не найден"));

        // Проверка изменения email
        if (newUser.getEmail() != null && !newUser.getEmail().equals(existingUser.getEmail())) {
            if (newUser.getEmail().isBlank()) {
                throw new ConditionsNotMetException("Имейл должен быть указан");
            }
            if (users.containsKey(newUser.getEmail())) {
                throw new DuplicatedDataException("Этот имейл уже используется");
            }
            users.remove(existingUser.getEmail());
            existingUser.setEmail(newUser.getEmail());
            users.put(existingUser.getEmail(), existingUser);
        }
        if (newUser.getUsername() != null) {
            existingUser.setUsername(newUser.getUsername());
        }
        if (newUser.getPassword() != null) {
            existingUser.setPassword(newUser.getPassword());
        }
        return existingUser;
    }

    private long getNextId() {
        return users.values()
                .stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0) + 1;
    }
}
