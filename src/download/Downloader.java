/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package download;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alex
 */
public class Downloader {
    
    private static final Logger log = Logger.getLogger(Downloader.class.getName());
    
    private final String SAVE_PATH;
    private final int MIN;
    private final int MAX;
    
    public Downloader(String _path, int _min, int _max){
        logInfo("Start download ");
        SAVE_PATH = _path;
        MIN = _min;
        MAX = _max;
    }
    
    public void start() throws MalformedURLException{
        File file = new File(SAVE_PATH);
        if (file.exists() & file.isFile()){
            log.log(Level.SEVERE, "Save path is a file");
            return;
        }
        if (!file.exists() & !file.mkdirs()){
            log.log(Level.SEVERE, "Could not create save path");
            return;
        }
        downloadExecutorServiceAdvanced(MIN, MAX);
    }
    
    private void downloadExecutorServiceAdvanced(int start, int stop) 
                 throws MalformedURLException {
        int maxPoolThreads = 2;
        int maxThreads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(maxPoolThreads);
        int i = start;
        
        boolean firstTime = true;
        
        while (i < stop){
            if (!executor.isTerminated() & !firstTime){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    
                }
            } else {
                firstTime = false;
                executor = Executors.newFixedThreadPool(maxThreads);
                for (int k = 0; k < maxThreads; k++) {
                    executor.execute(new DownloadTask(i, SAVE_PATH));
                    if (++i==stop){
                        break;
                    }
                }
                executor.shutdown();
            }
            
        }
        
    }
    
    private static void logInfo(String msg){
        log.log(Level.INFO, msg);
    }
    
}
