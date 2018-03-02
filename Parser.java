import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Created by camer on 2/26/2018.
 */

public class Parser implements Runnable {
    ArrayList<File> files;
    ArrayList<Book> books = new ArrayList<>();
    ArrayList<File> failed_files = new ArrayList<>();
    Saver saver;
    public Parser( ArrayList<File> Files, String destination){
        this.files = Files;
        saver = new Saver(books, destination);
    }

    public void run(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(Thread.currentThread().getName()+ "started" +dateFormat.format(date)); //2016/11/16 12:08:43
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
                            if(!myScanner.hasNext()){
                                throw new InputMismatchException("Failed after Title");
                            }
                            bookName = myScanner.next();
                            temp = myScanner.next();
                            while (temp.indexOf(':') == -1 && !temp.equals("Release")) {
                                if(!myScanner.hasNext()){
                                    throw new InputMismatchException("Failed Loading Title");
                                }
                                bookName += " " + temp;
                                temp = myScanner.next();
                            }
                            if (temp.equals("Author:")) {
                                if(!myScanner.hasNext()){
                                    throw new InputMismatchException("Failed after Author");
                                }
                                author = myScanner.next();
                                temp = myScanner.next();
                                while (temp.indexOf(':') == -1 && !temp.equals("Release")) {
                                    if(!myScanner.hasNext()){
                                        throw new InputMismatchException("Failed loading Author");
                                    }
                                    author += " " + temp;
                                    if(myScanner.hasNextLine())
                                        temp = myScanner.nextLine();
                                    else
                                        throw new InputMismatchException("Failed after loading Author");
                                }
                            }
                        }
                    }
                    while ((!temp.contains("START"))&& myScanner.hasNextLine()) {
                        temp = myScanner.nextLine();
                    }
                    if(myScanner.hasNextLine())
                        temp = myScanner.nextLine();
                    while(temp.contains("***") && myScanner.hasNextLine()){
                        temp = myScanner.nextLine();
                    }
                    if(myScanner.hasNext())
                        temp = myScanner.nextLine();
                    else{
                        throw new InputMismatchException("Failed loading content");
                    }
                    StringBuffer content = new StringBuffer();
                    while (!temp.contains("END OF") && myScanner.hasNextLine()) {
                        content.append(temp + "\n");
                        temp = myScanner.nextLine();
                    }
                    if(content.length() != 0) {
                        if(books.size() == 1200) {
                            saver.setBooks(books);
                            books = new ArrayList<>();
                            new Thread(saver, "saver").run();
                        }
                        books.add(new Book(author, bookName, content.toString()));
                    }
                    else{
                        failed_files.add(file);
                        throw new InputMismatchException("content empty");
                    }
                }
            }
            catch(FileNotFoundException e){
                System.out.println("File Not Found");
            }
            catch(Exception e){
                failed_files.add(file);
                System.out.println(Thread.currentThread().getName());
                System.out.println(file.getName());
                System.out.println(e.getMessage());
            }
        }
        saver.setBooks(books);
        new Thread(saver, Thread.currentThread().getName() + "'s saver").run();
        date = new Date();
        System.out.println( Thread.currentThread().getName()+" finished " + dateFormat.format(date)); //2016/11/16 12:08:43
        System.out.println("Failed Files :" + failed_files.size());
    }
}
