/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package argsparser;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alex
 */
public class Command {
    
    public final String KEY;
    public final String DESCRIPTION;
    public List<String> args = new ArrayList<>();
    
    public Command(String key, String description){
        this.KEY = key;
        this.DESCRIPTION = description;
    }
    
    public Command(String key){
        this(key, "");
    }
    
}
