/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package download;

import bash.Quote;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author alex
 */
public class DownloadTask implements Runnable{
    
    public static final String BASH_URL = "http://bash.im/";
    private static final String ONE_QUOTE_URL = "http://bash.im/quote/";
    private static final String MULTI_QUOTE_URL = "http://bash.im/index/";
    private final String urlOne;
    private final String urlMulti;
    private final String saveBase;
    private final String savePath;
    
    public DownloadTask(){
        urlOne = BASH_URL;
        urlMulti = BASH_URL;
        saveBase = "";
        savePath = "";
    }
    
    protected DownloadTask(int num, String _savePath){
        System.out.println("Download and parse " + num);
        urlOne = ONE_QUOTE_URL + num;
        urlMulti = MULTI_QUOTE_URL + num;
        saveBase = _savePath + System.getProperty("file.separator");
        savePath = _savePath + System.getProperty("file.separator") + num + ".txt";
    }

    @Override
    public void run() {
        downMultiQuotes();
    }
    
    public  List<Quote> getFromDoc(Document doc){
        List<Quote> result = new ArrayList<>();
        if (doc==null) return result;
        Elements quotes = doc.getElementsByClass("quote");
        
        if (quotes.isEmpty()){
            return result;
        }
        
        for (Element quote : quotes) {
            QuoteDownload q = new QuoteDownload();
            if (!q.parse(quote)){
                continue;
            }
            result.add(q);
        }
        return result;
    }
    
    public List<Quote> getFromUrl(String url){
        Document doc = getDoc(url);
        return getFromDoc(doc);
    }
    
    private void downMultiQuotes(){
        Document doc = getDocMulti();
        if (doc==null) return;
        
        List<Quote> list = getFromDoc(doc);
        
        for (Quote q : list) {
            String multiPath = saveBase + q.rawName + ".txt";
            
            File file = new File(multiPath);
            if (file.exists()){
                continue;
            }
            
            save(multiPath, q);
        }
    }
    
    private Document getDocMulti(){
        return getDoc(urlMulti);
    }
    
    private Document getDoc(String path){
        Document doc;
        try {
            doc = Jsoup.connect(path).timeout(10000).userAgent("Mozilla/17.0").get();
        } catch (IOException ex) {
            Logger.getLogger(DownloadTask.class.getName()).log(Level.SEVERE, "getDocMulti " + urlMulti);
            return null;
        }
        return doc;
    }
    
    private void save(String path, Quote quote){
        try {
            PrintWriter out = new PrintWriter(path);
            out.println(quote.rawName);
            out.println(quote.rawDate);
            out.println(quote.rawRating);
            out.println(quote.quote);
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DownloadTask.class.getName()).log(Level.SEVERE, "save " + path);
        } catch (Exception ex){
            Logger.getLogger(DownloadTask.class.getName()).log(Level.SEVERE, "save " + path);
        }
    }
    
    private void save(String path, String text){
        try {
            PrintWriter out = new PrintWriter(path);
            out.println(text);
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DownloadTask.class.getName()).log(Level.SEVERE, "save " + path);
        } catch (Exception ex){
            Logger.getLogger(DownloadTask.class.getName()).log(Level.SEVERE, "save " + path);
        }
    }
    
}
