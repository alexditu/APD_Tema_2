/* Ditu Alexandru 333CA Tema2 APD */

public class MapPartialSolution extends PartialSolution {
	
	String filename;
	int startPosition, endPosition, totalSize;
	ReducedWorkPool reduceWorkPool;
	
	public MapPartialSolution () {
	}
	
	public MapPartialSolution (String filename, int startPosition, int endPosition, int totalSize,
								ReducedWorkPool rwp) {
		this.filename = filename;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.totalSize = totalSize;
		this.reduceWorkPool = rwp;
		
	}
	
	public int getStartPosition() {
		return startPosition;
	}
	
	public int getEndPostion() {
		return endPosition;
	}
	
	public int getFragmentSize() {
		return endPosition - startPosition;
	}
	
	public String getFileName() {
		return filename;
	}
	
	public int getTotalSize() {
		return totalSize;
	}

}
