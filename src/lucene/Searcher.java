/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lucene;

import java.io.File;
import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author alex
 */
public class Searcher {
    
    private final String INDEX_PATH;
    
    private IndexReader indexReader;
    private IndexSearcher indexSearcher;
    
    public Searcher(String indexPath) throws IOException{
        INDEX_PATH = indexPath;
        Directory directory = FSDirectory.open(new File(INDEX_PATH));
        indexReader = DirectoryReader.open(directory);
        
        indexSearcher = new IndexSearcher(indexReader);
    }
    
    private TopDocs applyQuery(String field, String searchString) throws IOException, ParseException{
        Analyzer analyzer = new RussianAnalyzer(Version.LUCENE_48);
        
        String fieldSearch = field == null ? Lucene.FIELD_TEXT_TEXT : field;
        QueryParser qP = new QueryParser(Version.LUCENE_48, fieldSearch, analyzer);
        Query query = qP.parse(searchString);
        TopDocs top = indexSearcher.search(query, 100);
        
        return top;
    }
    
    public void search(String field, String searchString) throws IOException, ParseException{
        System.out.println("Searcher::search searching for '" + searchString + "'");
        TopDocs top = applyQuery(field, searchString);
        
        System.out.println("Searcher::search found " + top.totalHits);
        
        for (ScoreDoc doc : top.scoreDocs){
            System.out.println(doc.doc);
            Document qdoc = indexReader.document(doc.doc);
            DocQuote q = new DocQuote(qdoc);
            q.convert();
            
            System.out.println("    " + q.rawName);
            System.out.println("    raw: " + q.rawDate);
            System.out.println("    " + q.date.toZonedDateTime());
            System.out.println("    " + q.rawRating);
            System.out.println("    " + q.quote);
        }
    }
    
    public void search(String query) throws IOException, ParseException{
        search(null, query);
    }
    
    public int getHits(String field, String searchString) throws IOException, ParseException{
        return applyQuery(field, searchString).totalHits;
    }
    
    public int getHits(String searchString) throws IOException, ParseException{
        return applyQuery(null, searchString).totalHits;
    }
}
