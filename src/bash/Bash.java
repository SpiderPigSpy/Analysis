/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bash;

import download.Downloader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import lucene.Creator;
import lucene.Lucene;
import lucene.Searcher;
import org.apache.lucene.queryparser.classic.ParseException;

/**
 *
 * @author alex
 */
public class Bash {

    public enum Status {Download, CreateIndex, ReadIndex};
    public static final Status status = Status.ReadIndex;
    
    public static final String HOME = System.getProperty("user.home");
    public static final String SEP = System.getProperty("file.separator");
    public static final String FILE_DIR = HOME + SEP + "bash" + SEP + "files";
    public static final String INDEX_DIR = HOME + SEP + "bash" + SEP + "index";
    
    public Downloader dloader;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Bash bash = new Bash();
        switch (status){
            case Download:
                for (int i = 0; i < 1; i++) {
                    bash.startDownload(FILE_DIR, 1, 929);
                }
                break;
            case CreateIndex:
                bash.startLuceneIndexCreate();
                break;
            case ReadIndex:
                bash.startLuceneReadIndex();
                break;
        }
        
        
        
    }
    
    public void startDownload(String path, int start, int end){
        dloader = new Downloader(path, start, end);
        try {
            dloader.start();
        } catch (MalformedURLException ex) {
            Logger.getLogger(Bash.class.getName()).log(Level.SEVERE, "StartDownload Fail");
        }
    }
    
    public void startLuceneIndexCreate(){
        try {
            Creator c = new Creator(INDEX_DIR, true);
            c.create(FILE_DIR);
        } catch (Exception ex) {
            Logger.getLogger(Bash.class.getName()).log(Level.SEVERE, "Failed to create index", ex);
        }
    }
    
    public void startLuceneReadIndex(){
        try {
            long start = new Date().getTime();
            Searcher s = new Searcher(INDEX_DIR);
//            s.search(Lucene.FIELD_DATE + ":2007*");
            int all = 0;
            for (int i = 2004; i < 2015; i++) {
                int res = s.getHits(Lucene.FIELD_DATE + ":"+i+"*");
                all += res;
                System.out.println(i + " " + res);
            }
            System.out.println("All " + all);
            
            System.out.println("time: " + (new Date().getTime()-start));
        } catch (IOException | ParseException ex) {
            Logger.getLogger(Bash.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
