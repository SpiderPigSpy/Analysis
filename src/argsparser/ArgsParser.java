/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package argsparser;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author alex
 */
public class ArgsParser {
    
    private final String[] ARGS;
    
    private final Map<String, Command> keys;
    
    public Map<String, Command> parseKeys = new HashMap<>();
    
    public ArgsParser(String[] args){
        ARGS = args;
        keys = new HashMap<>();
    }
    
    public void addCommand(Command command){
        keys.put("-"+command.KEY, command);
    }
    
    public void parse(){
        Command c = null;
        boolean found = false;
        for (String s : ARGS) {
            System.out.println("Key " + s);
            if (keys.containsKey(s)){
                System.out.println("    containes");
                if (found) {
                    parseKeys.put(c.KEY, c);
                    System.out.println("    put");
                }
                c = new Command(s);
                found = true;
            } else if(found){
                System.out.println("    Add command");
                c.args.add(s);
            }
        }
        if (found & c != null) parseKeys.put(c.KEY, c);
    }
    
    public void printHelp(){
        for (Command command : keys.values()) {
            System.out.println(command.KEY + command.DESCRIPTION);
        }
    }
}
