/* Ditu Alexandru 333CA Tema2 APD */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.StringTokenizer;


public class ReplicatedWorkers {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		

		if (args.length < 3) {
			System.out.println("Error: Too few command line args! Exiting...");
			return;
		}
		HashMap <String, HashMap<String, Integer>> finalResults = new HashMap<>();
		
		int noOfThreads = Integer.parseInt(args[0]);
		
		InputDataReader in = new InputDataReader(args[1], args[2]);
		WorkPool MapWP = new WorkPool(noOfThreads);
		ReducedWorkPool ReduceWP = new ReducedWorkPool(noOfThreads, finalResults); //new WorkPool(Integer.parseInt(args[0]));
		
		// impartire fisier in fragmente de dimensiune fixa, ND
		MapPartialSolution mps;
		int start, pieces, endSize;
		int fileSize;
		File f;
		String crtFileName;
		
		// creare vector cu numele fisierelor
		String fileNames[] = new String[in.getNoOfDocs()];
		
		for (int i = 0; i < in.getNoOfDocs(); i++) {
			
			crtFileName = in.getInputName(i);
			fileNames[i] = crtFileName; 
			f = new File(crtFileName);
			fileSize = (int) f.length();
			pieces = fileSize / in.getFragmentSize();
			endSize = fileSize % in.getFragmentSize();
			start = 0;
			
			for (int j = 0; j < pieces; j++) {
				mps = new MapPartialSolution(crtFileName, start, start + in.getFragmentSize(), 
											 fileSize, ReduceWP);
				MapWP.putWork(mps);
				start += in.getFragmentSize() + 1;
				
			}
			
			// in cazul in care mai am octeti de parsat din fisierul curent:
			if (endSize != 0) {
				mps = new MapPartialSolution(crtFileName, start, start + endSize,
											 fileSize, ReduceWP);
				MapWP.putWork(mps);
			}
		}
		
		Worker w[] = new Worker[noOfThreads];
		// incep munca pentru Map
		for (int i =0; i < noOfThreads; i++) {
			w[i] =  new Worker(MapWP, 1);
			w[i].start();
		}
		
		// inainte sa fac Reduce toate thread-urile trebuie sa se termine
		for (int i = 0; i < noOfThreads; i++) {
			w[i].join();
		}
		
		// adaug in workPool-ul pentru Reduce numele fisierelor de intrare
		ReduceWP.setFileNames(fileNames);
		
		
		// incep munca pentru Reduce
		for (int i =0; i < noOfThreads; i++) {
			w[i] =  new Worker(ReduceWP, 2);
			w[i].start();
		}
		
		// astept sa se termin de combinat rezultatele pentru fiecare fisier
		for (int i = 0; i < noOfThreads; i++) {
			w[i].join();
		}
		
		// calculez frecventa pentru fisierul de input
		float wordCount = 0;
		HashMap<String, Float> inDocMap = new HashMap<>();
		for(Entry<String, Integer> i : finalResults.get(in.getInputDoc()).entrySet()) {
			wordCount += i.getValue();
		}
		for(Entry<String, Integer> i : finalResults.get(in.getInputDoc()).entrySet()) {
			float freq;
			freq = (i.getValue() / wordCount) * 100;
			inDocMap.put(i.getKey(), freq);
		}
		
		// creez workPool-ul pentru calculul gradului de similaritate
		FrequencyWorkPool fwp = new FrequencyWorkPool(noOfThreads, inDocMap);
		
		// adaug taskurile la workPool
		for (Entry<String, HashMap<String, Integer>> i : finalResults.entrySet()) {
			// nu adaug si fisierul in sine
			if (i.getKey().equals(in.getInputDoc()) == false) {
				FrequencyPartialSolution fps;
				fps = new FrequencyPartialSolution(i.getValue(), i.getKey());
				fwp.putWork(fps);
			}
		}
		
		// pornesc thread-urile:
		for (int i =0; i < noOfThreads; i++) {
			w[i] =  new Worker(fwp, 3);
			w[i].start();
		}
		
		for (int i = 0; i < noOfThreads; i++) {
			w[i].join();
		}
		
		// sortare descrescatoare dupa gradul de similaritate
		ArrayList<GenericPair> gradSimFis = fwp.getGradSim();
		Collections.sort(gradSimFis,new Comparator<GenericPair>() {
		    @Override
		    public int compare(GenericPair a, GenericPair b) {
		     return (b.getSecond()).compareTo(a.getSecond());
		    }
		 });
		
		// Scriere in fisier a rezultatelor
		float X = in.getSimilarityLimit();
		BufferedWriter bw = new BufferedWriter(new FileWriter(in.getOutputFilename()));
		bw.write("Rezultate pentru: (" + in.getInputDoc() + ")");
		bw.newLine();
		bw.newLine();
		for (int i = 0; i < gradSimFis.size(); i++) {
			if (gradSimFis.get(i).getSecond() >= X){
				long value = (long)(gradSimFis.get(i).getSecond() * 1000);
				float fval = (float)value / 1000;
				bw.write(gradSimFis.get(i).getFirst() + " (" + fval + "%)");
				bw.newLine();
			}
		}
		bw.close();
	}

}
