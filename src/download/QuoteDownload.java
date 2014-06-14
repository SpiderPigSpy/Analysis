/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package download;

import bash.Quote;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author alex
 */
public class QuoteDownload extends Quote {
    
    private final static String CLASS_UP = "up";
    private final static String CLASS_RATING = "rating";
    private final static String CLASS_TEXT = "text";
    private final static String CLASS_DATE = "date";
    
    public boolean parse(Element element){
        Elements dates = element.getElementsByClass(CLASS_DATE);
        if (dates.size()!=1) {
            return false;
        }
        date = dates.text();
        
        Elements ratings = element.getElementsByClass(CLASS_RATING);
        if (ratings.size()!=1) {
            return false;
        }
        rating = ratings.text();
        
        Elements ups = element.getElementsByClass(CLASS_UP);
        if (ups.size()!=1) {
            return false;
        }
        String quoteNum = ups.get(0).attr("href");
        num = quoteNum.split("/")[2];
        
        Elements text = element.getElementsByClass(CLASS_TEXT);
        if (text.size() != 1){
            return false;
        }
        quote = text.get(0).text();
        
        return true;
    }
    
}
