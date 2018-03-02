import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by camer on 2/26/2018.
 */

public class Saver implements Runnable {
    ArrayList<Book> books;
    ArrayList<Book> failedBooks;
    int files_processed = 0;
    String destination;
    public Saver (ArrayList<Book> books, String destination){
        this.books = books;
        this.destination = destination;
    }
    public void setBooks(ArrayList<Book> books){
        this.books = books;
    }
    public void run(){
        for(Book book : books){
            String author = book.getAuthor();
            String title = book.getName();
            String content = book.getContent();
            File newFile = new File(destination + files_processed + ".txt");
            try {
                if(!newFile.exists()) {
                    files_processed++;
                    PrintWriter output = new PrintWriter(newFile);
                    output.append("Author: " + author + "\n");
                    output.append("Title: " + title + "\n");
                    output.append(content);
                    output.close();
                    books.remove(book);
                }
                else{
                    while(newFile.exists()){
                        files_processed++;
                        newFile = new File(destination + files_processed + ".txt");
                    }
                    PrintWriter output = new PrintWriter(newFile);
                    output.append("Author: " + author + "\n");
                    output.append("Title: " + title + "\n");
                    output.append(content);
                    output.close();
                }
            }
            catch(FileNotFoundException e){
                failedBooks.add(book);
            }
        }
        books.clear();
    }
}
