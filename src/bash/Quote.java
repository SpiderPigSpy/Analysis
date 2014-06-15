/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bash;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alex
 */
public class Quote {
    
    private static Logger logger = Logger.getLogger(Quote.class.getName());
    
    private final String path;
    
   /*
    * В порядке появления в файле
    */
    public String rawName;
    public String rawDate;
    public String rawRating;
    public String quote;
    
    public GregorianCalendar date = new GregorianCalendar();
    public int rating = 0;
    
    public int year;
    public int month;
    public int day;
    
    public Quote(){
        path = "";
    }
    
    public Quote(File file) throws IOException{
        this(file.getPath());
    }
    
    public Quote(String fileName) throws FileNotFoundException, IOException{
        path = fileName;
        FileReader a = new FileReader(new File(fileName));
        BufferedReader br = new BufferedReader(a);
        rawName = br.readLine();
        rawDate = br.readLine();
        rawRating = br.readLine();
        quote = br.readLine();
        br.close();
        a.close();
    }
    
    public void convert(){
        try {
            rating = Integer.valueOf(rawRating);
            
            String[] splitSpace = rawDate.split(" ");
            String dateStr = splitSpace[0];
            String timeStr = splitSpace[1];
            String[] splitDef = dateStr.split("-");
            String yearStr = splitDef[0];
            String monthStr = splitDef[1];
            String dayStr = splitDef[2];
            String[] splitDoub = timeStr.split(":");
            String hourStr = splitDoub[0];
            String minuteStr = splitDoub[1];
            
            year = Integer.valueOf(yearStr);
            month = Integer.valueOf(monthStr);
            day = Integer.valueOf(dayStr);
            
            date = new GregorianCalendar(
                    year,
                    month - 1,
                    day,
                    Integer.valueOf(hourStr),
                    Integer.valueOf(minuteStr)
            );
            
        } catch (Exception e) {
//            logger.log(Level.WARNING, "convert failed " + path, e);
        }
        
    }
}
