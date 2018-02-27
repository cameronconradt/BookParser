import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

/**
 * Created by camer on 2/26/2018.
 */

public class Parser implements Runnable {
    ArrayList<File> files;
    ArrayList<Book> books = new ArrayList<>();
    Saver saver;
    public Parser( ArrayList<File> Files, String destination){
        this.files = Files;
        saver = new Saver(books, destination);
    }

    public void run(){
        System.out.println("Parsing");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
        if(files.size() <= 0){
            throw new IllegalArgumentException("No Files to Parse");
        }
        for(File file : files){
            try {
                Scanner myScanner = new Scanner(file);
                String author = null;
                String bookName = null;
                String temp = null;
                if (myScanner.hasNext()) {
                    while (bookName == null && myScanner.hasNext()) {
                        temp = myScanner.next();
                        if (temp.equals("Title:")) {
                            bookName = myScanner.next();
                            temp = myScanner.next();
                            while (temp.indexOf(':') == -1 && !temp.equals("Release")) {
                                bookName += " " + temp;
                                temp = myScanner.next();
                            }
                            if (temp.equals("Author:")) {
                                author = myScanner.next();
                                temp = myScanner.next();
                                while (temp.indexOf(':') == -1 && !temp.equals("Release")) {
                                    author += " " + temp;
                                    temp = myScanner.next();
                                }
                            }
                        }
                    }
                    for(int i = 0; i < 2 && myScanner.hasNext();i++){
                        while (!temp.equals("***") && myScanner.hasNext()) {
                            temp = myScanner.next();
                        }
                        if(myScanner.hasNext())
                            temp = myScanner.next();
                    }
                    StringBuffer content = new StringBuffer();
                    while (!temp.equals("***") && myScanner.hasNext()) {
                        content.append(temp + " ");
                        temp = myScanner.next();
                    }
                    if(content.length() != 0) {
                        if(books.size() == 2500) {
                            saver.setBooks(books);
                            books = new ArrayList<>();
                            new Thread(saver, "saver").run();
                        }
                        books.add(new Book(author, bookName, content.toString()));
                    }
                }
            }
            catch(FileNotFoundException e){
                System.out.println("File Not Found");
            }
            catch(Exception e){
                System.out.println(file.getName());
                e.printStackTrace();
            }
        }
        date = new Date();
        System.out.println( Thread.currentThread().getName()+" finished " + dateFormat.format(date)); //2016/11/16 12:08:43
    }
}
