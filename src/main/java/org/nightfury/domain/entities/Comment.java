package org.nightfury.domain.entities;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.nightfury.domain.util.DatabaseManager;

public class Comment extends Entity {

    private String content;
    private Article article;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    public Comment(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    @Override
    protected String getTableName() {
        return "comments";
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public LocalDateTime getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.created_at = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updated_at;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updated_at = updatedAt;
    }

    public static List<Comment> findByArticleId(int articleId, DatabaseManager databaseManager) {
        List<Comment> comments = new ArrayList<>();
        String query = "SELECT * FROM comments WHERE article_id = ?";

        try (ResultSet rs = databaseManager.executeQuery(query, articleId)) {
            Article article = Article.findById(Article.class, articleId, databaseManager);

            while (rs.next()) {
                Comment comment = new Comment(databaseManager);
                comment.setId(rs.getInt("id"));
                comment.setContent(rs.getString("content"));
                comment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                comment.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                comment.setArticle(article);
                comments.add(comment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return comments;
    }

    @Override
    public String toString() {
        return "Comment{" +
            "id=" + id +
            "content='" + content + '\'' +
            ", article=" + article +
            ", created_at=" + created_at +
            ", updated_at=" + updated_at +
            '}';
    }
}

