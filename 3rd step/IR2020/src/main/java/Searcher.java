import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
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
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import txtparsing.MyQuery;
import txtparsing.TXTparsing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
            //Running for k=20,30,50
            search(indexSearcher, field,20);
            search(indexSearcher, field,30);
            search(indexSearcher, field,50);
            //Close indexReader
            indexReader.close();

            System.out.println("done");
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Searches the index given a specific user query.
     */
    private void search(IndexSearcher indexSearcher, String field,int n){
        try{
            // define which analyzer to use for the normalization of user's query
            Analyzer query_analyzer =  customAnalyzerForQueryExpansion();
            // create a query parser on the field "contents"
            QueryParser parser = new QueryParser(field, query_analyzer);

            //read queries from txt file
            String queriestxt="docs/queries.txt";
            List<MyQuery> queries=TXTparsing.parseQueries(queriestxt);
            //Delete if there is already file "Newresults.txt"
            File file=new File("docs/Newresults"+n+".txt");
            if (file.exists() && file.isFile())
            {
                file.delete();
            }
            FileWriter fileWriter=new FileWriter("docs/Newresults"+n+".txt",true);
            BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);
            for(MyQuery q:queries){
                // parse the query according to QueryParser

                Query query=parser.parse(q.getQuery());
                System.out.println("Searching for: " + query.toString(field));

                // search the index using the indexSearcher
                TopDocs results = indexSearcher.search(query, n);
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
                    bufferedWriter.write(queryID+"\t0\t"+hitDoc.get("docid")+"\t0\t"+hits[i].score+"\tmyIRmethod");
                    bufferedWriter.newLine();
                }

            }
            bufferedWriter.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private Analyzer customAnalyzerForQueryExpansion() throws IOException {
        Analyzer analyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                Word2Vec vec = WordVectorSerializer.readWord2VecModel("docs/myModel.txt");

                Tokenizer tokenizer = new StandardTokenizer();
                double minAcc = 0.85;
                TokenStream stream =new StandardFilter(tokenizer);
                stream = new LowerCaseFilter(stream);
                stream = new StopFilter(stream, StopAnalyzer.ENGLISH_STOP_WORDS_SET);
                stream = new W2VSynonymFilter(stream, vec, minAcc);


                return new TokenStreamComponents(tokenizer, stream);
            }
        };







        return analyzer;
    }

    public static void main(String [] args){
        Searcher searcher=new Searcher();
    }
}
