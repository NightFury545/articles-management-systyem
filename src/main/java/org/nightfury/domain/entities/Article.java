package org.nightfury.domain.entities;

import java.time.LocalDateTime;
import java.util.List;
import org.nightfury.domain.util.DatabaseManager;

public class Article extends Entity {

    private String content;
    private String image_url;
    private List<Comment> comments;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    public Article(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    @Override
    protected String getTableName() {
        return "articles";
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return image_url;
    }

    public void setImageUrl(String imageUrl) {
        this.image_url = imageUrl;
    }

    public List<Comment> getComments() {
        if (comments == null) {
            comments = Comment.findByArticleId(id, databaseManager);
        }
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
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

    @Override
    public String toString() {
        return "Article{" +
            "id=" + id +
            "content='" + content + '\'' +
            ", image_url='" + image_url + '\'' +
            ", comments=" + comments +
            ", created_at=" + created_at +
            ", updated_at=" + updated_at +
            '}';
    }
}
