package ru.yandex.practicum.catsgram.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.catsgram.model.Post;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public class PostRepository extends BaseRepository<Post> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM posts";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM posts WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO posts(author_id, description, post_date)" +
            "VALUES(?, ?, ?) returning id";
    public static final String UPDATE_QUERY = "UPDATE posts SET description = ?, post_date = ? WHERE id = ?";
    public static final String DELETE_QUERY = "DELETE FROM posts WHERE id = ?";

    public PostRepository(JdbcTemplate jdbc, RowMapper<Post> mapper) {
        super(jdbc, mapper);
    }

    public List<Post> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Post> findById(Long userId) {
        return findOne(FIND_BY_ID_QUERY, userId);
    }

    public Post save(Post post) {
        Long id = insert(
                INSERT_QUERY,
                post.getAuthorId(),
                post.getDescription(),
                Timestamp.from(post.getPostDate())
        );
        post.setId(id);
        return post;
    }

    public Post update(Post post) {
        update(
                UPDATE_QUERY,
                post.getDescription(),
                Timestamp.from(post.getPostDate()),
                post.getId()
        );
        return post;
    }

    public boolean delete(Long postId) {
        return delete(DELETE_QUERY, postId);
    }
}
