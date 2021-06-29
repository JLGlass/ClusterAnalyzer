package sequences;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;
import java.io.File;
import java.util.regex.*;
import java.util.*;

// package to handle BED files

public class Bed extends Sequence {
	   
    private Map<String, Set<Integer>> seqMap;
    private List<String> trackNames;
    
	// constructor loads sequence
	public Bed(File[] inFiles) {
		files = inFiles;
        seqMap = new HashMap<String, Set<Integer>>();
        trackNames = new ArrayList<String>();
        
		seqMap = readBed();
	}
    
    // returns set of sequence names (chromosome list for BED files)
    public Set<String> getNames() {
		return(seqMap.keySet());
	}
	
	// returns concatenated list of track names
    public String getNameList() {
        String retStr = "";
        
        if (trackNames.size() > 0) {

            for (String name : trackNames) {
                retStr = retStr + " " + name;
            }
        }
        else {
            for (File file : files) {
                retStr = retStr + " " + file.getName();
            }
        }
        
        return(retStr.trim());
    }
			
	// reads the BED file and extracts the header
    // populates seqMap with a set of start positions for each chromosome
	private Map<String, Set<Integer>> readBed() {
        BufferedReader objReader = null;
		String currLine, chrm;
        Integer pos;
        String[] fields = null;
        
        Pattern p = Pattern.compile("^track\\s+name\\=(?:\"|\')?([^\"\']+)(\\s+)?", Pattern.CASE_INSENSITIVE);

        try {
			
            for (File file : files) {
                objReader = new BufferedReader(new FileReader(file));
                
                while ((currLine = objReader.readLine()) != null) {
                    
                    Matcher m = p.matcher(currLine);
                    
                    if (m.find()) {
                        
                        trackNames.add(m.group(1));
                    }
                    else {
                        fields = currLine.split("\\s+");
                        chrm = fields[0];
                        pos = Integer.parseInt(fields[1]);
                        
                        //add the position to the chromosome specific position list
                                                
                        if (!seqMap.containsKey(chrm)) {
                            seqMap.put(chrm, new HashSet<Integer>());
                        }
                        
                        seqMap.get(chrm).add(pos);
                    }
                }
                
            }
            
        }
        
        catch(OutOfMemoryError E) {
            System.out.printf("\nOut of memory. Consider using the -XmxNG (N = # gigabytes) flag to specify a larger heap size\n\n");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (objReader != null)
                    objReader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        return(seqMap);
    }
	
	// returns an Integer array of regex positions within the file
	public Integer[] getPositionList(String seqName) {
		Integer[] retArr = null;
        
        if (seqMap.containsKey(seqName)) {
            retArr = seqMap.get(seqName).toArray(new Integer[seqMap.get(seqName).size()]);
        }
        
        Arrays.sort(retArr);
        
        return(retArr);
	}
	
}
