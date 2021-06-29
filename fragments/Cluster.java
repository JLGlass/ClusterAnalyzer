package fragments;

import java.util.*;
import java.util.regex.*;

// input: position list array (such as Fasta.getPositionList() output)
// outputs: array of fragment lengths

// Need to update to include whole genome data
// add in a parse chrm function (take from Fasta)

public class Cluster {
	
	private List<String[]> clusterList;
	private int patternsPerFragment;
	private double avgClusterLength;
	private double minFragmentLength;
	private Map<String, Fragment> fragmentMap;
	
	public Cluster(Map<String, Fragment> fragMap, int numFeatures, double minFragLength) {
		fragmentMap = fragMap;
		patternsPerFragment = numFeatures;
		minFragmentLength = minFragLength;
        
		clusterList = new ArrayList<String[]>();

		generateClusterList();
	}
	
	
	//gets the chromosome from a given header in the format chrXX
	//assumes header is of the form chr## or chromosome## or chromosome ## (case insensitive)
	private String parseChrom(String name) {
		
		Pattern p = Pattern.compile("chr(?:omosome)?(?:\\s+)?((\\w+))", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(name);		
		String num = null;
		
		if (m.find()) {
			num = m.group(1);
		}
		else {
			System.err.printf("No chromosome pattern found in %s\n", name);
		}
		
		return("chr" + num);
	}
	
	public void setMinFragmentLength(double minLength) {
		minFragmentLength = minLength;
		generateClusterList();
	}
			
	public double getAvgClusterLength() {
		return(avgClusterLength);
	}	
	
    public int getNumClusters() {
        return(clusterList.size());
    }

	private void generateClusterList() {

		int numInClusters = 0;
        Fragment fragment;
        String chrm;
        Integer start, end;
        Integer clustStart = 0, clustEnd = 0;

		clusterList.clear();
		
		Iterator it = fragmentMap.entrySet().iterator();
		
        while (it.hasNext()) {
			Map.Entry mapEntry = (Map.Entry)it.next();
			
            chrm = parseChrom((String)mapEntry.getKey());
            fragment = (Fragment)mapEntry.getValue();
            
            for (int i = 0; i < fragment.getNumFragments(); i++) {
                start = fragment.getPos(i);
                end = fragment.getPos(i + patternsPerFragment - 1);
                
                if (fragment.getLength(i) < minFragmentLength) {
                    numInClusters++;
                    
                    if (start > clustEnd) {
                        if (clustEnd > 0) {
                            clusterList.add(new String[]{chrm, clustStart.toString(), clustEnd.toString()});
                        }
                        clustStart = start;
                        clustEnd = end;
                    }
                    else {
                        clustEnd = end;
                    }
                }
            }
            
            if (clustStart > 0) {
                clusterList.add(new String[]{chrm, clustStart.toString(), clustEnd.toString()});
            }
			
		}
        
		avgClusterLength = (numInClusters/(clusterList.size() + 1)) / minFragmentLength;
    }
	
    public List<String[]> getClusterList(double minLength) {
		
		if (minLength != minFragmentLength) {
			minFragmentLength = minLength;
			generateClusterList();
		}
		
		return(clusterList);
	}
}
