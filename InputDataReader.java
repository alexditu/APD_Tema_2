import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/* Ditu Alexandru 333CA
 * Citeste datele de intrare asa cum este specificat in enuntul temei.
 */
public class InputDataReader {

	BufferedReader br;
	String inputFileName, outputFileName, inputDoc;
	ArrayList <String> inputFileNames;
	int D, ND;
	float X;
	
	public InputDataReader(String inputFileName, String outputFileName) {
		this.inputFileName = inputFileName;
		this.outputFileName = outputFileName;
		
		try {
			br = new BufferedReader(new FileReader(inputFileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		inputFileNames = new ArrayList<>();
		try {
			inputDoc = br.readLine();
			D = Integer.parseInt(br.readLine());
			X = Float.parseFloat(br.readLine());
			ND = Integer.parseInt(br.readLine());
			
			for (int i = 0; i < ND; i++) {
				inputFileNames.add(br.readLine());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getOutputFilename() {
		return outputFileName;
	}
	
	public String getInputDoc() {
		return inputDoc;
	}
	
	public int getFragmentSize() {
		return D;
	}
	
	public float getSimilarityLimit() {
		return X;
	}
	
	public int getNoOfDocs() {
		return ND;
	}
	
	public String getInputName(int i) {
		return inputFileNames.get(i);
	}
}
