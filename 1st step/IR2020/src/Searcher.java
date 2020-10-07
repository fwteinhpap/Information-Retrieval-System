import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;
import txtparsing.MyQuery;
import txtparsing.TXTparsing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.List;


public class Searcher {

    public Searcher(){
        try{
            String indexLocation = ("index"); //define where the index is stored
            String field = "contents"; //define which field will be searched

            //Access the index using indexReaderFSDirectory.open(Paths.get(index))
            IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexLocation))); //IndexReader is an abstract class, providing an interface for accessing an index.
            IndexSearcher indexSearcher = new IndexSearcher(indexReader); //Creates a searcher searching the provided index, Implements search over a single IndexReader.
            indexSearcher.setSimilarity(new BM25Similarity());

            //Search the index using indexSearcher
            search(indexSearcher, field);

            //Close indexReader
            indexReader.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Searches the index given a specific user query.
     */
    private void search(IndexSearcher indexSearcher, String field){
        try{
            // define which analyzer to use for the normalization of user's query
            Analyzer analyzer = new EnglishAnalyzer();

            // create a query parser on the field "contents"
            QueryParser parser = new QueryParser(field, analyzer);
            //read queries from txt file
            String queriestxt="docs/queries.txt";
            List<MyQuery> queries=TXTparsing.parseQueries(queriestxt);
            FileWriter fileWriter=new FileWriter("docs/results20.txt",true);
            BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);
            for(MyQuery q:queries){
                // parse the query according to QueryParser
                Query query = parser.parse(q.getQuery());
                System.out.println("Searching for: " + query.toString(field));

                // search the index using the indexSearcher
                TopDocs results = indexSearcher.search(query, 20);
                ScoreDoc[] hits = results.scoreDocs;
                long numTotalHits = results.totalHits;
                System.out.println(numTotalHits + " total matching documents");
                String queryID;
                if(q.getQueryid()<10) queryID="Q0"+q.getQueryid();
                else queryID="Q"+q.getQueryid();
                //display results
                for(int i=0; i<hits.length; i++){
                    Document hitDoc = indexSearcher.doc(hits[i].doc);
                    System.out.println("\tScore "+hits[i].score +"\ttitle="+hitDoc.get("title")+"\tdocid:"+hitDoc.get("docid"));
                    //save to file results.txt
                    bufferedWriter.write(queryID+"\t0\t"+hitDoc.get("docid")+"\t0\t"+hits[i].score+"\tmyIRmethod");
                    bufferedWriter.newLine();
                }

            }
            bufferedWriter.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String [] args){
        Searcher searcher=new Searcher();
    }
}
