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

/**
 *
 * @author alex
 */
public class Quote {
   /*
    * В порядке появления в файле
    */
    public String num;
    public String date;
    public String rating;
    public String quote;
    
    public Quote(){
        
    }
    
    public Quote(File file) throws IOException{
        this(file.getPath());
    }
    
    public Quote(String fileName) throws FileNotFoundException, IOException{
        FileReader a = new FileReader(new File(fileName));
        BufferedReader br = new BufferedReader(a);
        num = br.readLine();
        date = br.readLine();
        rating = br.readLine();
        quote = br.readLine();
        br.close();
        a.close();
    }
}
