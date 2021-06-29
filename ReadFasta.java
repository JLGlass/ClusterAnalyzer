import sequences.*;
import fragments.*;
import java.io.File;
import java.util.*;

// input: name of fasta file
// output: CharBuffer containing fasta sequence (discards header line)

public class ReadFasta {
 
	public static void main(String[] args) {
		
		int numFeatures = 5;
		String feature = "CG";
		
        if (args.length < 1) {
            System.out.printf("Usage: java ReadFasta [filename]\n");
            System.exit(0);
        }
        
        File f = new File(args[0]);
        File[] files = new File[1];
        files[0] = f;
		Fasta fasta = new Fasta(files);
        
        Set<String> names = fasta.getNames();
        String seq;
        String lastName = "";
        
        for (String name : names) {
            seq = fasta.getSeq(name);
            lastName = name;
            System.out.printf("ReadFasta> name: |%s|, seq: |%s|, lastName: |%s|\n", name, seq, lastName);
            
            Integer[] posList = fasta.getPositionList(lastName, feature, false);
            
            Fragment frag = new Fragment(posList, numFeatures);
            double[] lengths = frag.getLengths();
            
            
            for (int i = 0; i < posList.length; i++) {
                if (i > 5 && i < posList.length - 5) {
                    continue;
                }
                
                System.out.printf("posList[%d] = %d\n", i, posList[i]);
            }
        }
        
        /*
		for (int i = 0; i < lengths.length; i++) {
			
			if (i > 5 && i < lengths.length - 5) {
				continue;
			}
			
			System.out.printf("lengths[%d] = %d, (%d - %d)\n", i, lengths[i], posList[i + numFeatures], posList[i]);
		}
		
		System.out.printf("seq length: %d, posList length: %d, fragment lengths: %d\n", seq.length(), posList.length, lengths.length);
		System.out.printf("fragment name: %s\n", frag.getName());
		System.out.printf("last elements: posList[%d] = %d, lengths[%d] = %d\n", posList.length - 1, posList[posList.length - 1], lengths.length - 1, lengths[lengths.length - 1]);
		System.out.printf("first elements: posList[0] = %d, lengths[0] = %d\n", posList[0], lengths[0]);
		
		System.out.printf("last position: %d, first position %d, last fragment pos %d, num positions: %d\n", frag.getLastPos(), frag.getFirstPos(), frag.getLastFragmentPos(), frag.numPositions());
		
		
		FragmentList fragList = new FragmentList(frag);
		Fragment frag2 = new Fragment(fasta.getName() + "2", posList, numFeatures);
		Fragment frag3 = new Fragment(fasta.getName() + "3", posList, numFeatures);

		System.out.printf("orig names) frag: %s, frag2: %s, frag3: %s\n", frag.getName(), frag2.getName(), frag3.getName());
		
		System.out.printf("num elements after initialized with frag: %d\n", fragList.getNumElements());
		fragList.add(frag2);
		System.out.printf("num elements after frag2 added: %d\n", fragList.getNumElements());
		fragList.add(frag3);
		System.out.printf("num elements after frag3 added: %d\n", fragList.getNumElements());
		
		Fragment resCurrFrag = fragList.getFragment();
		Fragment resFrag1 = fragList.getNextFragment();
		Fragment resFrag2 = fragList.getNextFragment();
		Fragment resFrag3 = fragList.getNextFragment();
		Fragment resFrag4 = fragList.getNextFragment();
		
		System.out.printf("result names) resCurrFrag: %s, resFrag1: %s, resFrag2: %s, resFrag3: %s, resFrag4: %s\n", resCurrFrag.getName(), resFrag1.getName(), resFrag2.getName(), resFrag3.getName(), resFrag4.getName());
		System.out.printf("#fragments) resCurrFrag: %d, resFrag1: %d, resFrag2: %d, resFrag3: %d, resFrag4: %d\n", resCurrFrag.numPositions(), resFrag1.numPositions(), resFrag2.numPositions(), resFrag3.numPositions(), resFrag4.numPositions());
		System.out.printf("numFragments in fragList: %d\n", fragList.getNumElements());
		*/
	}
}
