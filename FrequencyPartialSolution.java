/* Ditu Alexandru 333CA Tema2 APD */

import java.util.HashMap;


public class FrequencyPartialSolution extends PartialSolution{

	HashMap<String, Integer> hMap;
	String fileName;
	
	public FrequencyPartialSolution(HashMap<String, Integer> hMap, String fileName) {
		this.hMap = hMap;
		this.fileName = fileName;
	}
	
	public HashMap<String, Integer> getHashMap() {
		return hMap;
	}
	
	public String getFileName() {
		return fileName;
	}
}
