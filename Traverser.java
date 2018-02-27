import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

/**
 * Created by camer on 2/13/2018.
 */

public class Traverser {
    ArrayList<File> myFiles = new ArrayList<>();
    ArrayList<Book> myBooks = new ArrayList<>();
    String destination;
    ArrayList<Book> failedBooks = new ArrayList<>();
    private int files_processed = 1;
    public static void main(String[] args){
        Traverser myTraverser = new Traverser(args[0], args[1]);
    }
    public Traverser(String Start, String End){
        File currentDir = new File(Start);
        destination = End;
        Traverse(currentDir);
        ArrayList<File> head = new ArrayList<>();
        ArrayList<File> tail = new ArrayList<>();
        for(int i = 0; i < myFiles.size(); i++){
            if(i < myFiles.size()/2){
                head.add(myFiles.get(i));
            }
            else
                tail.add(myFiles.get(i));
        }
         Parser parser = new Parser(head,destination);
        new Thread(parser, "parser").start();

        Parser parser1 = new Parser(tail, destination);
        new Thread(parser1, "parser1").start();
    }
    private void Traverse(File Start){
            File[] files = Start.listFiles();
            for(File file : files){
                if(file.isDirectory()){
                    Traverse(file);
                }
                else if(file.getName().endsWith(".txt") && file.getName().indexOf('-') == -1){
                    myFiles.add(file);
                }
            }
    }
    private void parse(){
        System.out.println("Parsing");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
        if(myFiles.size() <= 0){
            throw new IllegalArgumentException("No Files to Parse");
        }
        for(File file : myFiles){
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
                        if(myBooks.size() == 5000) {
                            save();
                            System.out.println("5000 Books Processed");
                            date = new Date();
                            System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
                            myBooks.clear();
                        }
                        myBooks.add(new Book(author, bookName, content.toString()));
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
    }
    private void save(){
        System.out.println("Saving");
        for(Book book : myBooks){
            String author = book.getAuthor();
            String title = book.getName();
            String content = book.getContent();
            File newFile = new File(destination + files_processed + ".txt");
            files_processed++;
            try {
                if(!newFile.exists()) {
                    PrintWriter output = new PrintWriter(newFile);
                    output.append("Author: " + author + "\n");
                    output.append("Title: " + title + "\n");
                    output.append(content);
                    output.close();
                    System.out.println("File " + newFile.getName() + " created");
                }
            }
            catch(FileNotFoundException e){
                failedBooks.add(book);
            }
        }


    }
}
