import sequences.*;
import fragments.*;
import java.io.File;
import java.util.*;

// input: name of BED file

public class ReadBed {
 
	public static void main(String[] args) {
		
        if (args.length < 1) {
            System.out.printf("Usage: java ReadBed [filename]\n");
            System.exit(0);
        }
        
        File f = new File(args[0]);
        File[] files = new File[1];
        files[0] = f;
		Bed bed = new Bed(files);
        
        Set<String> names = bed.getNames();
        
        for (String name : names) {
            
            Integer[] posList = bed.getPositionList(name);
            
            
            for (int i = 0; i < posList.length; i++) {                
                System.out.printf("posList[%d] = %d\n", i, posList[i]);
            }
        }
        
        System.out.printf("nameList: %s\n", bed.getNameList());
        
	}
}
