package sequences;

import java.io.File;
import java.util.*;

// abstract class to handle files containing sequence information

public abstract class Sequence {
	
	protected File[] files;
    
	// returns set of sequence names
    abstract Set<String> getNames();
	
	// returns concatenated list of sequence names
    abstract String getNameList();

	// returns an Integer array of positions within the file
    abstract Integer[] getPositionList(String seqName);

}
