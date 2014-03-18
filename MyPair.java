/* Ditu Alexandru 333CA Tema2 APD */

import java.util.HashMap;


public class MyPair {
	String fileName;
	HashMap <String, Integer> wordHashMap; 
	
	public MyPair(String fileName) {
		this.fileName = fileName;
		wordHashMap = new HashMap<>();
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
}
