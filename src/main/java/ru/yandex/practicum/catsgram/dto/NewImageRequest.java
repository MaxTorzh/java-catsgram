package ru.yandex.practicum.catsgram.dto;

import lombok.Data;

@Data
public class NewImageRequest {
    private Long postId;
    private String originalFileName;
}
