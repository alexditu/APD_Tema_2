/* Ditu Alexandru 333CA Tema2 APD */
import java.util.HashMap;


public class ReducePartialSolution extends PartialSolution {
	
	String fileName;
	HashMap <String, Integer> wordHashMap; 
	
	public ReducePartialSolution(String fileName) {
		this.fileName = fileName;
		wordHashMap = new HashMap<>();
	}
	
	public ReducePartialSolution(String fileName, HashMap <String, Integer> wordHashMap) {
		this.fileName = fileName;
		this.wordHashMap = wordHashMap;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public Integer getWordCount(String word) {
		return wordHashMap.get(word);
	}
	
	public void incWordFreq(String word) {
		if (wordHashMap.get(word) == null) {
			wordHashMap.put(word, 1);
		} else {
			int crtCount = wordHashMap.get(word);
			crtCount++;
			wordHashMap.put(word, crtCount);
		}
	}
	
	public HashMap <String, Integer> getHMap() {
		return wordHashMap;
	}
}
