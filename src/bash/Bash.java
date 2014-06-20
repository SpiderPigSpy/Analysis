/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bash;

import argsparser.ArgsParser;
import argsparser.Command;
import download.Downloader;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lucene.Creator;
import lucene.Lucene;
import lucene.Searcher;
import net.sourceforge.argparse4j.ArgumentParsers;
import static net.sourceforge.argparse4j.impl.Arguments.storeTrue;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.lucene.queryparser.classic.ParseException;

/**
 *
 * @author alex
 */
public class Bash {

    public enum Status {
        Download, CreateIndex, CommonInfo, QueryInfo, None
    };
    public static final Status status = Status.None;

    public static final String HOME = System.getProperty("user.home");
    public static final String SEP = System.getProperty("file.separator");
    public static final String FILES_DIR = HOME + SEP + "bash" + SEP + "files";
    public static final String INDEX_DIR = HOME + SEP + "bash" + SEP + "index";
    
    public static final int START_PAGE = 1;
    public static final int END_PAGE = 929;
    
    public Downloader dloader;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        long startTime = new Date().getTime();
        ArgumentParser parser = ArgumentParsers.newArgumentParser("Bash")
                .defaultHelp(true)
                .description("Download and analyze bash.im quotes");
        
        parser.addArgument("-f", "--files-path")
                .dest("files")
                .help("Path to store downloaded files")
                .required(false)
                .setDefault(FILES_DIR)
                .metavar("FILES_PATH")
                .type(String.class);
        
        parser.addArgument("-l", "--lucene-path")
                .dest("lucene")
                .help("Path to store lucene index files")
                .required(false)
                .setDefault(INDEX_DIR)
                .metavar("LUCENE_PATH")
                .type(String.class);
        
        parser.addArgument("-D", "--Download")
                .dest("download")
                .help("Download quotes. Use '-d START END' to specify pages")
                .required(false)
                .action(storeTrue());
        
        parser.addArgument("-d")
                .dest("default")
                .help("Default download pages: from START to END")
                .required(false)
                .nargs(2)
                .metavar("START","END")
                .setDefault(START_PAGE, END_PAGE)
                .type(Integer.class);
        
        parser.addArgument("-c")
                .dest("create")
                .help("Create index in index folder")
                .required(false)
                .action(storeTrue());
        
        parser.addArgument("-i")
                .dest("info")
                .help("General qoute analyze")
                .required(false)
                .action(storeTrue());
        
        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
        
        
        Bash bash = new Bash();
        
        String filesPath = FILES_DIR;
        String indexPath = INDEX_DIR;
        
        boolean download = false;
        int downloadStart = START_PAGE;
        int downloadEnd = END_PAGE;
        boolean baseAnalyze = false;
        boolean createIndex = false;
        
        System.out.println(ns);
        
        try {
            download = ns.getBoolean("download");
            downloadStart = ((List<Integer>) ns.get("default")).get(0);
            downloadEnd = ((List<Integer>) ns.get("default")).get(1);
            baseAnalyze = ns.getBoolean("info");
            createIndex = ns.getBoolean("create");
            
            filesPath = ns.getString("files") != null ? ns.getString("files") : FILES_DIR;
            indexPath = ns.getString("lucene") != null ? ns.getString("lucene") : INDEX_DIR;
            
            
        } catch (Exception e) {
            System.out.println("Error during args parse");
        }
        
        if (download){
            bash.startDownload(filesPath, downloadStart, downloadEnd);
        }
        
        if (createIndex){
            bash.startLuceneIndexCreate(filesPath, indexPath);
        }
        
        if (baseAnalyze){
            bash.startCommonInfo(filesPath, indexPath);
        }
        
        System.out.println("Total time: " + (new Date().getTime() -startTime) + " ms");
        
    }

    public void startDownload(String path, int start, int end) {
        dloader = new Downloader(path, start, end);
        try {
            dloader.start();
        } catch (MalformedURLException ex) {
            Logger.getLogger(Bash.class.getName()).log(Level.SEVERE, "StartDownload Fail");
        }
    }

    public void startLuceneIndexCreate(String filesDir, String indexDir) {
        try {
            Creator c = new Creator(indexDir, true);
            c.create(filesDir);
        } catch (Exception ex) {
            Logger.getLogger(Bash.class.getName()).log(Level.SEVERE, "Failed to create index", ex);
        }
    }

    public void startCommonInfo(String filesDir, String indexDir) {
        try {
            long start = new Date().getTime();
            Searcher s = new Searcher(indexDir);
            //Распределение цитат по годам
            System.out.println("Распределение цитат по годам...");
            int all = 0;
            for (int i = 2004; i < 2015; i++) {
                int res = s.getHits(Lucene.FIELD_DATE + ":" + i + "*");
                all += res;
                System.out.println(i + "    " + res);
            }

            //Распределение цитат по месецам
            System.out.println("Распределение цитат по месецам...");
            all = 0;
            int[] months = new int[12];
            for (int i = 2004; i < 2015; i++) {
                for (int mon = 1; mon < 13; mon++) {
                    String monStr = mon < 10 ? ("0" + mon) : (String.valueOf(mon));
                    int res = s.getHits(Lucene.FIELD_DATE + ":" + i + "-" + monStr + "*");
                    all += res;
                    months[mon - 1] += res;
                }
            }
            for (int i = 0; i < months.length; i++) {
                System.out.println((i + 1) + "    " + months[i]);
            }

            System.out.println("Проход по всем файлам...");
            all = 0;

            int[] dow = new int[7];

            float[] ratingYear = new float[11];
            int[] ratingYearNum = new int[11];
            int badYears = 0;

            for (int i = 0; i < 500000; i++) {
                File f = new File(filesDir + SEP + i + ".txt");
                if (!f.exists() | !f.isFile()) {
                    continue;
                }
                Quote q = new Quote(f);
                q.convert();

                int realDow = q.date.get(Calendar.DAY_OF_WEEK) - 2 >= 0
                        ? q.date.get(Calendar.DAY_OF_WEEK) - 2
                        : 6;
                //Распределение цитат по дням недели
                dow[realDow]++;

                float sk;
                int k;
                //Распределение среднего рейтинга по годам
                if (q.year > 2003) {
                    sk = ratingYear[q.year - 2004];
                    k = ratingYearNum[q.year - 2004];
                    ratingYear[q.year - 2004] = sk * k / (k + 1) + q.rating / (k + 1);
                    ratingYearNum[q.year - 2004]++;
                } else {
//                    System.out.println("Bad year " + q.rawDate);
                    badYears++;
                }
                all++;
            }

            System.out.println("Проход по всем файлам x2...");

            float[] ratingDow = new float[7];
            int[] ratingDowNum = new int[7];

            for (int i = 0; i < 500000; i++) {
                File f = new File(filesDir + SEP + i + ".txt");
                if (!f.exists() | !f.isFile()) {
                    continue;
                }
                Quote q = new Quote(f);
                q.convert();

                int realDow = q.date.get(Calendar.DAY_OF_WEEK) - 2 >= 0
                        ? q.date.get(Calendar.DAY_OF_WEEK) - 2
                        : 6;

                //Распределение среднего рейтинга по дням недели
                if (q.year > 2003) {
                    float sk = ratingDow[realDow];
                    int k = ratingDowNum[realDow];
                    //Нормарованный рейтинг
                    ratingDow[realDow] = sk * k / (k + 1) + q.rating / (k + 1) / ratingYear[q.year - 2004];
                    ratingDowNum[realDow]++;
                }
            }
            System.out.println("");

            //Распределение цитат по дням недели
            System.out.println("Распределение цитат по дням недели:");
            for (int i = 0; i < dow.length; i++) {
                System.out.println((i + 1) + "    " + dow[i]);
            }

            //Распределение среднего рейтинга по годам
            System.out.println("Распределение среднего рейтинга по годам:");
            System.out.println("Bad years: " + badYears);
            for (int i = 0; i < ratingYear.length; i++) {
                System.out.println((i + 2004) + "    " + ratingYear[i]);
            }

            //Распределение среднего рейтинга по дням недели
            System.out.println("Распределение среднего рейтинга по дням недели:");
            for (int i = 0; i < ratingDow.length; i++) {
                System.out.println((i + 1) + "    " + ratingDow[i]);
            }

            System.out.println("Basic analyze time: " + (new Date().getTime() - start) + " ms");
        } catch (IOException | ParseException ex) {
            Logger.getLogger(Bash.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void startQueryInfo(String query) {
        System.out.println("Parse query " + query);
    }
}
