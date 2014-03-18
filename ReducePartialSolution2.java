/* Ditu Alexandru 333CA Tema2 APD */

import java.util.HashMap;

/*
 * Aceasta clasa a fost creeata pentru a putea imbina 2 elemente de tipul
 * PartialSolution (necesara pentru getWork, din workPool-ul Reduce)
 */
public class ReducePartialSolution2 extends PartialSolution {
	
	String fileName;
	HashMap <String, Integer> wordHashMap1, wordHashMap2;
	boolean last;
	boolean wait;
	
	public ReducePartialSolution2(HashMap<String, Integer> h1, HashMap<String, Integer> h2,
									String fileName) {
		this.wordHashMap1 = h1;
		this.wordHashMap2 = h2;
		this.fileName = fileName;
		last = false;
		wait = false;
	}
	public ReducePartialSolution2(boolean wait) {
		
		this.wait = true;
	}
	
	public HashMap<String, Integer> getH1() {
		return wordHashMap1;
	}
	public HashMap<String, Integer> getH2() {
		return wordHashMap2;
	}
	
	public String getFilename() {
		return fileName;
	}
	
	public void setIsLast() {
		last = true;
	}
	
	public boolean isLast() {
		return last;
	}
	
	public void setWait() {
		wait = true;
	}
	
	public boolean mustWait() {
		return wait;
	}
}
