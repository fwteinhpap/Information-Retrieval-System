package txtparsing;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TXTparsing {
    public static List<MyDoc> parse(String file){
        try{
            //Parse txt file
            String txt_file = readEntireFileIntoAString(file);
            String[] docs = txt_file.split("///");
            System.out.println("Read: "+docs.length + " docs");
            int count=0;

            //Parse each document from the txt file
            List<MyDoc> parsed_docs= new ArrayList<MyDoc>();
            for (String doc:docs){
                String [] adoc = doc.trim().split(":",2);
                String [] header=adoc[0].split("\n");
                MyDoc mydoc=new MyDoc(Integer.parseInt(header[0].trim()),header[1],adoc[1]);
                parsed_docs.add(mydoc);
            }

            return parsed_docs;
        } catch (Throwable err) {
            err.printStackTrace();
            return null;
        }
    }
    public static List<MyQuery> parseQueries(String file){
        try{
            //Parse txt file
            String txt_file = readEntireFileIntoAString(file);
            String[] queries = txt_file.split("///");
            System.out.println("Read: "+queries.length + " queries");
            int count=0;

            //Parse each query from the txt file
            List<MyQuery> parsed_queries= new ArrayList<MyQuery>();
            for (String q:queries){
                String [] header = q.trim().split("\n");
                MyQuery myquery=new MyQuery(Integer.parseInt(header[0].trim()),header[1]);
                parsed_queries.add(myquery);
            }

            return parsed_queries;
        } catch (Throwable err) {
            err.printStackTrace();
            return null;
        }

    }
    public static String readEntireFileIntoAString(String file) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(file));
        scanner.useDelimiter("\\A"); //\\A stands for :start of a string
        String entireFileText = scanner.next();
        return entireFileText;

    }

    public static void main(String [] args) throws IOException {

        String txt_file = TXTparsing.readEntireFileIntoAString("src/wn_s.pl");
        String [] line =txt_file.split("[\\r\\n]+");

        BufferedWriter out = new BufferedWriter(new FileWriter("src/wn_s1.pl"));
            for(String l : line) {
                if(!l.contains(",v,") && !l.contains((",r,")))
                    out.write(l+"\n");
            }
        out.close();
    }
}
