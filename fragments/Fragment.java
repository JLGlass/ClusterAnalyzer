package fragments;

import java.util.Arrays;

// input: position list array (such as Fasta.getPositionList() output)
// outputs: array of fragment lengths

public class Fragment {
	
	private Integer[] positionArr;
	private double[] lengths;
	private int patternsPerFragment;
	private double max;
	private double min;
	private double stdDev;
    private double avg;
	
	public Fragment(Integer[] positions, int numFeatures) {
		positionArr = positions;
		setPatternsPerFragment(numFeatures);
	}
	
	public Integer[] getPositions() {
		return(positionArr);
	}
	
	public void setPatternsPerFragment(int num) {
		lengths = null;
		patternsPerFragment = num;
		max = -1;
		min = Double.MAX_VALUE;
        stdDev = 0;
        
        generateLengths();
	}
	
	public int getNumPositions() {
        
        if (positionArr == null) {
            return(0);
        }
        
		return(positionArr.length);
	}
	
	public int getNumFragments() {
        if (lengths == null) {
            generateLengths();
        }

        if (lengths == null) {
            return(0);
        }
        
		return(lengths.length);
	}
	
	public Integer getFirstPos() {
        if (lengths == null) {
            generateLengths();
        }

		return(positionArr[0]);
	}
	
	public Integer getLastPos() {
        if (positionArr == null) {
            return(0);
        }

		return(positionArr[positionArr.length - 1]);
	}
	
	public Integer getLastFragmentPos() {
        if (positionArr == null) {
            return(0);
        }

		return(positionArr[positionArr.length - patternsPerFragment]);
	}
		
	public double getMaxLength() {
        if (lengths == null) {
            generateLengths();
        }
        
		return(max);
	}
	
	public double getMinLength() {
		if (lengths == null) {
            generateLengths();
        }
        return(min);
	}

    public double getStdDev() {
        return(stdDev);
    }
	
	public double getAvgLength() {
		return(avg);
	}

    public Integer getPos(int i) {
        return(positionArr[i]);
    }
    
    public double getLength(int i) {
        if (lengths == null) {
            generateLengths();
        }
        
        return(lengths[i]);
    }
    
     
    private void generateLengths() {
        
        if (positionArr.length > patternsPerFragment) {
            
            lengths = new double[positionArr.length - patternsPerFragment];
            double M = 0;
            double S = 0;
            double temp;
            int i;
            double sum = 0;
            
            // running SD based on Knuth TAOCP vol 2, 3rd edition, page 232
            
            lengths[0] = positionArr[patternsPerFragment] - positionArr[0];

            for (i = 1; i < positionArr.length - patternsPerFragment; i++) {
                lengths[i] = positionArr[i + patternsPerFragment] - positionArr[i];
                max = (max < lengths[i])? lengths[i] : max;
                min = (min > lengths[i])? lengths[i] : min;
                
                temp = M;
                M += (lengths[i] - temp) / i;
                S += (lengths[i] - temp) * (lengths[i] - M);
                sum += lengths[i];
            }
            
            stdDev = Math.sqrt(S / (i - 1));
			avg = sum / lengths.length;
            
        }
                
    }
    
	public double[] getLengths() {
		if (lengths == null) {
			generateLengths();
        }
		
		return(lengths);
	}
	
	public double[] getLengthsSubset(int fraction) {
		if (lengths == null) {
			generateLengths();
		}
		
		double[] lengthsFraction = new double[lengths.length / fraction];
		
		for (int i = 0; i < lengths.length / fraction; i++) {
			lengthsFraction[i] = lengths[i * fraction];
		}
		
		return(lengthsFraction);
	}
	
	public double[] getLengthsSubsetAvgs(int fraction) {
		
		if (lengths == null) {
			generateLengths();
		}
		
		double subsetAvg = 0;		
		double[] lengthsFraction = new double[lengths.length / fraction];
		
		for (int i = 0; i < lengths.length / fraction; i++) {
			
			subsetAvg = 0;
			for (int j = 0; j < fraction; j++) {
				subsetAvg += lengths[(i * fraction) + j];
			}
			subsetAvg /= fraction;			
			lengthsFraction[i] = subsetAvg;
		}
		
		return(lengthsFraction);
	}

	public double[] getLengthsSubsetMedians(int fraction) {
		
		if (lengths == null) {
			generateLengths();
		}
		
		double[] lengthsFraction = new double[lengths.length / fraction];
		double[] subset = new double[fraction];
		
		for (int i = 0; i < lengths.length / fraction; i++) {
			
			for (int j = 0; j < fraction; j++) {
				subset[j] = lengths[(i * fraction) + j];
			}
			
			Arrays.sort(subset);
			lengthsFraction[i] = subset[(int)(fraction/2)];
		}
		
		return(lengthsFraction);
	}
	
}
