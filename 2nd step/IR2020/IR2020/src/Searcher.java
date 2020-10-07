import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.FlattenGraphFilterFactory;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceTokenizerFactory;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.en.*;
import org.apache.lucene.analysis.miscellaneous.HyphenatedWordsFilterFactory;
import org.apache.lucene.analysis.miscellaneous.RemoveDuplicatesTokenFilterFactory;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilterFactory;
import org.apache.lucene.analysis.ngram.EdgeNGramFilterFactory;
import org.apache.lucene.analysis.ngram.NGramFilterFactory;
import org.apache.lucene.analysis.payloads.TypeAsPayloadTokenFilterFactory;
import org.apache.lucene.analysis.shingle.ShingleFilterFactory;
import org.apache.lucene.analysis.snowball.SnowballPorterFilterFactory;
import org.apache.lucene.analysis.standard.StandardFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.apache.lucene.analysis.synonym.*;
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
import org.apache.lucene.store.InputStreamDataInput;
import txtparsing.MyQuery;
import txtparsing.TXTparsing;

import java.io.*;
import java.io.Reader;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
            //Analyzer analyzer = new EnglishAnalyzer();
            CustomAnalyzer query_analyzer = customAnalyzerForQueryExpansion();
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

    private CustomAnalyzer customAnalyzerForQueryExpansion() throws IOException {
        //Read synonyms from wn_s.pl file
        /*Uncomment this line to run wordnet with verbs and adverbs and comment line 120*/
        // String originalWordNet="wn_s.pl";
        String originalWordNet="wn_s1.pl";

        Map<String, String> sffargs = new HashMap<>();
        sffargs.put("synonyms", originalWordNet);
        sffargs.put("format", "wordnet");
        //    Custom analyzer should analyze query text like the EnglishAnalyzer and have
        //    an extra filter for finding the synonyms of each token from the Map sffargs
        //    and add them to the query.
        CustomAnalyzer.Builder builder = CustomAnalyzer.builder()
                /**
                 * Uncomment these lines to run the "original code" and comment the next 138-144 lines.
                .withTokenizer(StandardTokenizerFactory.class)
                .addTokenFilter(StandardFilterFactory.class)
                .addTokenFilter(EnglishPossessiveFilterFactory.class)
                .addTokenFilter(LowerCaseFilterFactory.class)
                .addTokenFilter(StopFilterFactory.class)
                .addTokenFilter(PorterStemFilterFactory.class)
                .addTokenFilter(SynonymFilterFactory.class, sffargs);*/
                .withTokenizer(WhitespaceTokenizerFactory.class)
                .addTokenFilter(EnglishPossessiveFilterFactory.class)
                .addTokenFilter(LowerCaseFilterFactory.class)
                .addTokenFilter(StopFilterFactory.class)
                .addTokenFilter(PorterStemFilterFactory.class)
                .addTokenFilter(SynonymFilterFactory.class, sffargs)
                .addTokenFilter(RemoveDuplicatesTokenFilterFactory.class);



        CustomAnalyzer analyzer = builder.build();

        return analyzer;
    }

    public static void main(String [] args){
        Searcher searcher=new Searcher();
    }
}
