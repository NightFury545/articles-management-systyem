package org.nightfury;

import org.nightfury.domain.entities.Article;
import org.nightfury.domain.entities.Comment;
import org.nightfury.domain.util.DatabaseManager;
import org.nightfury.infrastructure.database.DatabaseInit;
import org.nightfury.infrastructure.database.SQLiteDatabaseManager;

public class Main {

    public static void main(String[] args) {
        DatabaseManager sqLiteDatabaseManager = new SQLiteDatabaseManager();
        DatabaseInit databaseInit = new DatabaseInit(sqLiteDatabaseManager);
        databaseInit.initializeDatabase();

        Article article = new Article(sqLiteDatabaseManager);
        article.setContent("This is a new article about nature!");
        article.setImageUrl("https://example.com/natureImg.png");
        article.save();

        Comment comment = new Comment(sqLiteDatabaseManager);
        comment.setContent("Чудова стаття!");
        comment.setArticle(article);
        comment.save();

        Comment comment2 = new Comment(sqLiteDatabaseManager);
        comment2.setContent("Дуже повчальна стаття!");
        comment2.setArticle(article);
        comment2.save();

        article.getComments().forEach(System.out::println);

        Comment.findAll(Comment.class, sqLiteDatabaseManager).forEach(System.out::println);
        Article.findAll(Article.class, sqLiteDatabaseManager).forEach(System.out::println);

        System.out.println(Comment.findById(Comment.class, 1, sqLiteDatabaseManager));
    }
}