package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.dal.UserRepository;
import ru.yandex.practicum.catsgram.dto.UserDto;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.mapper.UserMapper;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        return users.values()
                .stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }

    public Optional<User> findUserById(Long authorId) {
        return Optional.ofNullable(users.get(authorId));
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
        users.put(user.getId(), user);
        return user;
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            User existingUser = users.get(newUser.getId());
            if (newUser.getEmail() != null &&
                    !existingUser.getEmail().equalsIgnoreCase(newUser.getEmail())) {
                if (users.containsValue(newUser)) {
                    throw new DuplicatedDataException("Данный email уже используется");
                }
                existingUser.setEmail(newUser.getEmail());
            }
            if (newUser.getUsername() != null && !newUser.getUsername().isBlank()) {
                existingUser.setUsername(newUser.getUsername());
            }
            if (newUser.getPassword() != null && !newUser.getPassword().isBlank()) {
                existingUser.setPassword(newUser.getPassword());
            }
            return existingUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    private long getNextId() {
        return users.keySet()
                .stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0) + 1;
    }
}
