import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import txtparsing.MyDoc;
import txtparsing.TXTparsing;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class Indexer {
    IndexWriter indexWriter;

    public Indexer(){

        String indexLocation = ("index");

        System.out.println("Indexing to directory '" + indexLocation + "'...");
        try {
            //This directory contains the indexes
            Directory indexDirectory = FSDirectory.open(Paths.get(indexLocation));


            // define which analyzer to use for the normalization of documents
            EnglishAnalyzer analyzer = new EnglishAnalyzer();
            Similarity similarity = new BM25Similarity();

            // configure IndexWriter
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setSimilarity(similarity);

            // Create a new index in the directory, removing any
            // previously indexed documents:
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            indexWriter = new IndexWriter(indexDirectory, iwc);
        }catch (IOException e){
            System.out.println(" caught a " + e.getClass() +
                    "\n with message: " + e.getMessage());
        }

    }

    public void createIndex() throws IOException {
        String doctxt="docs/documents.txt";
        List<MyDoc> docs=TXTparsing.parse(doctxt);
        for (MyDoc doc : docs){
            indexDoc(indexWriter, doc);
        }
        indexWriter.close();
    }

    private void indexDoc(IndexWriter indexWriter, MyDoc mydoc) {
        try {

            // make a new, empty document
            Document doc = new Document();

            // create the fields of the document and add them to the document
            StoredField title = new StoredField("title", mydoc.getTitle());
            doc.add(title);
            StoredField docID = new StoredField("docid", mydoc.getDocID());
            doc.add(docID);
            StoredField content = new StoredField("content", mydoc.getContent());
            doc.add(content);

            String fullSearchableText =mydoc.getTitle() + " " +  mydoc.getContent();
            TextField contents = new TextField("contents", fullSearchableText, Field.Store.NO);
            doc.add(contents);

            if (indexWriter.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE) {
                // New index, so we just add the document (no old document can be there):
                System.out.println("adding " + mydoc);
                indexWriter.addDocument(doc);
            }
        } catch(Exception e){
            e.printStackTrace();
        }

    }

    public static void main(String [] args) throws IOException {
        Indexer i=new Indexer();
        i.createIndex();
    }
}
