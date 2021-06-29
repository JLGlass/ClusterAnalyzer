package sequences;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;
import java.io.File;
import java.util.regex.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;

// package to handle fasta files

public class Fasta extends Sequence {

    private final Map<String, String> seqMap;
    private Pattern regex;
    
    ExecutorService pool = Executors.newFixedThreadPool(8);
    CountDownLatch latch;
    // constructor loads sequence

    public Fasta(File[] inFiles) {
        files = inFiles;
        seqMap = new HashMap<String, String>();
        latch = new CountDownLatch(files.length);
        for (final File file : files) {
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("test");
                    readFasta(file);
                }
            });
        }
        pool.shutdown();
        try {
          latch.await();
        } catch (InterruptedException e) {
           // handle
        }
        System.out.println("Finished all threads");
        System.out.println("seqMap:" + seqMap.size());
    }

    //sets up regular expression for later pattern searching
    public void setRegex(String regExp, boolean caseSensitive) {
        if (caseSensitive == true) {
            regex = Pattern.compile(regExp);
        }
        else {
            regex = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE);
        }

    }

    //returns DNA sequence
    public String getSeq(String seqName) {
        return(seqMap.get(seqName));
    }

    // returns set of sequence names
    @Override
    public Set<String> getNames() {
        return(seqMap.keySet());
    }

    // returns concatenated list of sequence names
    @Override
    public String getNameList() {
        String retStr = "";

        for (String name : seqMap.keySet()) {
            retStr = retStr + " " + name;
        }

        return(retStr.trim());
    }

    // reads the fasta file and extracts the header
    /*private Map<String, String>*/
    void readFasta(File file) {
        BufferedReader objReader = null;
        CharBuffer cb = null;
        String header = "", currLine;
        boolean firstLine;

        Pattern p = Pattern.compile(">(?:\\s+)?(.+)");

        try {

            //for (File file : files) {
                cb = CharBuffer.allocate((int) file.length());
                objReader = new BufferedReader(new FileReader(file));
                firstLine = true;
                header = file.getName();

                while ((currLine = objReader.readLine()) != null) {

                    Matcher m = p.matcher(currLine);

                    if (m.find()) {

                        if (!firstLine) {
                            cb.flip();
                            seqMap.put(header, cb.toString());
                        }

                        firstLine = false;
                        header = m.group(1);
                        cb.clear();
                    }
                    else {
                        cb.put(currLine);
                    }
                }

                cb.flip();

                synchronized(seqMap) {
                    seqMap.put(header, cb.toString());
                    System.out.println("seqMap:" + seqMap.size());
                }
           // }

        }

        catch(OutOfMemoryError E) {
            System.out.printf("\nOut of memory. Consider using the -XmxNG (N = # gigabytes) flag to specify a larger heap size\n\n");
        }
        catch (IOException e) {
            //e.printStackTrace();
        }
        finally {
            try {
                if (objReader != null)
                    objReader.close();
            } catch (IOException ex) {
                //ex.printStackTrace();
            }
            latch.countDown();
        }

        //return(seqMap);
    }

    // returns an Integer array of regex positions within the file
    public Integer[] getPositionList(String seqName) {
        List<Integer> pList = new ArrayList<Integer>();

        Matcher m = regex.matcher(seqMap.get(seqName));

        while (m.find()) {
            pList.add(m.end());
        }

        Integer[] pArray = new Integer[pList.size()];
        pList.toArray(pArray);

        return(pArray);

    }

}
