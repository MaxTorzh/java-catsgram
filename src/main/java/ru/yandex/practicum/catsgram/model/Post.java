package ru.yandex.practicum.catsgram.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Data
@EqualsAndHashCode(of = {"id"})
public class Post {
    private Long id;
    private Long authorId;
    private String description;
    private Instant postDate;
    @JsonIgnore
    private User author;
}
