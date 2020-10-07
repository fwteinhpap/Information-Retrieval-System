import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.deeplearning4j.models.embeddings.learning.impl.elements.CBOW;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import txtparsing.MyDoc;
import txtparsing.TXTparsing;

import java.io.FileNotFoundException;
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
            Analyzer analyzer = new StandardAnalyzer();
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

    public void createIndex(String doctxt) throws IOException {
        List<MyDoc> docs= TXTparsing.parse(doctxt);
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

    public void trainModel(String doctxt) throws FileNotFoundException {
        SentenceIterator iter = new BasicLineIterator(doctxt);
        //SkipGram MLP = new SkipGram();
        CBOW MLP = new CBOW();
       Word2Vec vec = new Word2Vec.Builder()
                .layerSize(150)
                .windowSize(3)
                .epochs(5)
                .elementsLearningAlgorithm(MLP)
                .tokenizerFactory(new DefaultTokenizerFactory())
                .iterate(iter)
                .build();

        vec.fit();
        System.out.println("Save vectors....");
        WordVectorSerializer.writeWord2VecModel(vec, "docs/myModel_150.txt");

    }
    public static void main(String [] args) throws IOException {
        Indexer i=new Indexer();
        String pathToRead = "docs/documents.txt";
        //i.createIndex(pathToRead);
        String pathToTrain = "docs/IR2020_clean.txt";
        i.trainModel(pathToTrain);
    }
}
