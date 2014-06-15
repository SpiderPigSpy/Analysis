/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lucene;

import bash.Quote;
import org.apache.lucene.document.Document;

/**
 *
 * @author alex
 */
public class DocQuote extends Quote{
    
    public DocQuote(Document doc){
        num = doc.get(Lucene.FIELD_NAME);
        rating = doc.get(Lucene.FIELD_RATING);
        date = doc.get(Lucene.FIELD_DATE);
        quote = doc.get(Lucene.FIELD_TEXT_STRING);
    }
    
}
