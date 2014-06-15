/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lucene;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
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
    
    public Searcher(String indexPath){
        INDEX_PATH = indexPath;
    }
    
    public void search(String field, String searchString) throws IOException, ParseException{
        System.out.println("Searcher::search searching for '" + searchString + "'");
        Directory directory = FSDirectory.open(new File(INDEX_PATH));
        IndexReader indexReader = DirectoryReader.open(directory);
        
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        
        Analyzer analyzer = new RussianAnalyzer(Version.LUCENE_48);
        QueryParser queryParser = new QueryParser(Version.LUCENE_48, field, analyzer);
        Query query = queryParser.parse(searchString);
        TopDocs top = indexSearcher.search(query, 100);
        
        System.out.println("Searcher::search found " + top.totalHits);
        
        for (ScoreDoc doc : top.scoreDocs){
            System.out.println(doc.doc);
            Document qdoc = indexReader.document(doc.doc);
            DocQuote q = new DocQuote(qdoc);
            System.out.println("    " + q.num);
            System.out.println("    " + q.date);
            System.out.println("    " + q.rating);
            System.out.println("    " + q.quote);
        }
    }
}
