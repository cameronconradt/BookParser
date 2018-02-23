/**
 * Created by camer on 2/13/2018.
 */

public class Book {
    private String author;
    private String name;
    private String content;
    public Book(String author, String name, String content){
        this.author = author;
        this.content = content;
        this.name = name;
    }
    public Book(String name, String content){
        this.author = "";
        this.content = content;
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }
}
