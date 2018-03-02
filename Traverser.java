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
        Traverse(currentDir);/*
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
        new Thread(parser1, "parser1").start();*/
        SplitAndStart(myFiles,Runtime.getRuntime().availableProcessors()/2, 0);
    }
    private void Traverse(File Start){
            File[] files = Start.listFiles();
            if(Start.isDirectory()) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        Traverse(file);
                    } else if (file.getName().endsWith(".txt") && file.getName().indexOf('-') == -1) {
                        myFiles.add(file);
                    }
                }
            }
            else if(Start.getName().endsWith(".txt")){
                myFiles.add(Start);
            }
    }
    private void SplitAndStart(ArrayList<File> files, int toSplit, int id){
        if(toSplit == 0){
            Parser parser = new Parser(files,destination);
            new Thread(parser, "parser" + id).start();
        }
        else{
            ArrayList<File> head = new ArrayList<>();
            ArrayList<File> tail = new ArrayList<>();
            for(int i = 0; i < myFiles.size(); i++){
                if(i < myFiles.size()/2){
                    head.add(myFiles.get(i));
                }
                else
                    tail.add(myFiles.get(i));
            }
            SplitAndStart(head, toSplit-1, id);
            SplitAndStart(tail,toSplit-1,id+toSplit);
        }
    }
}
