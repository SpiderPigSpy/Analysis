/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lucene;

import bash.Quote;
import java.io.File;
import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.benchmark.byTask.utils.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author alex
 */
public class Creator {
    
    private final String INDEX_PATH;
    private String filesPath;
    
    public static final String FIELD_NAME = "name";
    public static final String FIELD_RATING = "rating";
    public static final String FIELD_DATE = "date";
    public static final String FIELD_TEXT = "text";
    
    private IndexWriter writer;
    
    public Creator(String _indexPath, boolean deleteOld) throws Exception{
        this.INDEX_PATH = _indexPath;
        File dir = new File(_indexPath);
        if (dir.exists() & dir.isFile()){
            throw new Exception("Index folder is a file");
        }
        
        if (dir.exists() & deleteOld){
            for (File file : dir.listFiles()) {
                file.delete();
            }
        }
        
        if (!dir.exists()){
            if (!dir.mkdirs()){
                throw new Exception("Index folder could not be created");
            }
        }
        
        
        
    }
    
    public void create(String _filesPath) throws IOException{
        filesPath = _filesPath;
        Analyzer analyzer = new RussianAnalyzer(Version.LUCENE_48);
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_48, analyzer);
        writer = new IndexWriter(FSDirectory.open(new File(INDEX_PATH)),config);
        
        File folder = new File(filesPath);
        
        if (!folder.isDirectory()){
            
            return;
        }
        
        int i = 1;
        
        for (File file : folder.listFiles()) {
            System.out.println(i++ + "/46354 Adding " + file);
            try {
                Quote q = new Quote(file);
                Document doc = new Document();
                doc.add(new StringField(FIELD_NAME, q.num, Field.Store.YES));
                doc.add(new StringField(FIELD_RATING, q.rating, Field.Store.YES));
                doc.add(new StringField(FIELD_DATE, q.date, Field.Store.YES));
                doc.add(new StringField(FIELD_TEXT, q.quote, Field.Store.YES));
                try {
                    writer.addDocument(doc);
                } catch (IOException ex) {
                    System.err.println("IndexCreator::indexFolder failed to store " + file.getPath());
                }
                
            } catch (Exception e) {
                System.out.println("    failed");
            }
            
        }
        
        writer.close();
    }
    
}
