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
import java.util.logging.Level;
import java.util.logging.Logger;
import lucene.Creator;
import lucene.Lucene;
import lucene.Searcher;
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

    public Downloader dloader;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        ArgsParser ap = new ArgsParser(args);
        ap.addCommand(new Command("d",
                "\n     [int start = 1]"
              + "\n     [int end = 929]"
              + "\n     Download bash quotes from start to end and save to files_path"));
        ap.addCommand(new Command("c",
                "\n     Create lucene index at index_path, get files from files_path"));
        ap.addCommand(new Command("i",
                "\n     Print common info"));
        ap.addCommand(new Command("q",
                "\n     [query]"
              + "\n     Analyze by query"));
        ap.addCommand(new Command("f",
                "\n     [str files_path = " + FILES_DIR + "]"
              + "\n     use files_path to access or save quotes files"));
        ap.addCommand(new Command("l",
                "\n     [str index_path = " + INDEX_DIR + "]"
              + "\n     use index_path to access or create lucene index"));

        if (args.length == 0) {
            ap.printHelp();
            return;
        }
        
        ap.parse();
        Bash bash = new Bash();
        
        String fileDir = FILES_DIR;
        if (ap.parseKeys.containsKey("f")){
            fileDir = ap.parseKeys.get("f").args.get(1);
        }
        
        String indexDir = INDEX_DIR;
        if (ap.parseKeys.containsKey("l")){
            indexDir = ap.parseKeys.get("l").args.get(1);
        }
        
        if (ap.parseKeys.containsKey("q")){
            try {
                bash.startQueryInfo(ap.parseKeys.get("q").args.get(0));
            } catch (Exception e) {
                ap.printHelp();
                return;
            }
           
        }
        
        switch (status) {
            case Download:
                for (int i = 0; i < 1; i++) {
                    bash.startDownload(FILES_DIR, 1, 929);
                }
                break;
            case CreateIndex:
                bash.startLuceneIndexCreate();
                break;
            case CommonInfo:
                bash.startCommonInfo();
                break;
            case QueryInfo:
                bash.startQueryInfo("");
                break;
        }

    }

    public void startDownload(String path, int start, int end) {
        dloader = new Downloader(path, start, end);
        try {
            dloader.start();
        } catch (MalformedURLException ex) {
            Logger.getLogger(Bash.class.getName()).log(Level.SEVERE, "StartDownload Fail");
        }
    }

    public void startLuceneIndexCreate() {
        try {
            Creator c = new Creator(INDEX_DIR, true);
            c.create(FILES_DIR);
        } catch (Exception ex) {
            Logger.getLogger(Bash.class.getName()).log(Level.SEVERE, "Failed to create index", ex);
        }
    }

    public void startCommonInfo() {
        try {
            long start = new Date().getTime();
            Searcher s = new Searcher(INDEX_DIR);
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
                File f = new File(FILES_DIR + SEP + i + ".txt");
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
                File f = new File(FILES_DIR + SEP + i + ".txt");
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

            System.out.println("time: " + (new Date().getTime() - start) + " ms");
        } catch (IOException | ParseException ex) {
            Logger.getLogger(Bash.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void startQueryInfo(String query) {
        System.out.println("Parse query " + query);
    }
}
