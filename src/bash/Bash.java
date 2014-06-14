/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bash;

import download.Downloader;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alex
 */
public class Bash {

    public enum Status {Download, CreateIndex};
    public static final Status status = Status.CreateIndex;
    
    public static final String HOME = System.getProperty("user.home");
    public static final String SEP = System.getProperty("file.separator");
    
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
                    bash.startDownload(HOME + SEP + "bash", 1, 929);
                }
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
    
}
