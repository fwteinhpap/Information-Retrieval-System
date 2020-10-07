import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

public class Reader {
    public Reader(){
        try{

            String indexLocation = ("index"); //define where the index is stored            

            //Access the index using indexReader
            IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexLocation))); //IndexReader is an abstract class, providing an interface for accessing an index.

            //Retrieve all docs in the index using the indexReader
            printIndexDocuments(indexReader);

            //Close indexReader
            indexReader.close();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void printIndexDocuments(IndexReader indexReader) {
        try {
            System.out.println("--------------------------");
            System.out.println("Documents in the index...");

            for (int i=0; i<indexReader.maxDoc(); i++) {
                Document doc = indexReader.document(i);
                System.out.println("\ttitle="+doc.getField("title")+"\tdocid:"+doc.get("docid")+"\tcontent:"+doc.get("content"));
            }
        } catch (CorruptIndexException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String [] args){
        Reader r=new Reader();
    }
}
