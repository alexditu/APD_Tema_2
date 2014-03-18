/* Ditu Alexandru 333CA Tema2 APD */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Clasa ce implementeaza un "work pool" conform modelului "replicated workers".
 * Task-urile introduse in work pool sunt obiecte de tipul PartialSolution.
 *
 */
public class ReducedWorkPool {
	int nThreads; // nr total de thread-uri worker
	int nWaiting = 0; // nr de thread-uri worker care sunt blocate asteptand un task
	public boolean ready = false; // daca s-a terminat complet rezolvarea problemei 
	
	// cate fragmente sunt din fiecare fisier
	HashMap<String, Integer> noOfFragments;
	
	// din ce fisier proceseaza fiecare thread
	ArrayList <String> crtFragProcessed;

	//LinkedList<PartialSolution> tasks = new LinkedList<PartialSolution>();
	HashMap<String, ArrayList<ReducePartialSolution>> tasks = 
			new HashMap<String, ArrayList<ReducePartialSolution>>();
	
	// un vector cu numele fisierelor
	String fileNames[];
	
	// rezultatele finale
	HashMap <String, HashMap<String, Integer>> finalRezults;
	
	/**
	 * Constructor pentru clasa WorkPool.
	 * @param nThreads - numarul de thread-uri worker
	 */
	public ReducedWorkPool(int nThreads, HashMap <String, HashMap<String, Integer>> f) {
		this.nThreads = nThreads;
		crtFragProcessed = new ArrayList<String>();
		noOfFragments = new HashMap<>();
		finalRezults = f;
		
	}

	/**
	 * Functie care incearca obtinera unui task din workpool.
	 * Daca nu sunt task-uri disponibile, functia se blocheaza pana cand 
	 * poate fi furnizat un task sau pana cand rezolvarea problemei este complet
	 * terminata
	 * @return Un task de rezolvat, sau null daca rezolvarea problemei s-a terminat 
	 */
	public synchronized PartialSolution getWork(String tName) {
		if (tasks.size() == 0) { // workpool gol
			nWaiting++;
			/* condtitie de terminare:
			 * nu mai exista nici un task in workpool si nici un worker nu e activ 
			 */
			if (nWaiting == nThreads) {
				ready = true;
				/* problema s-a terminat, anunt toti ceilalti workeri */
				notifyAll();
				return null;
			} else {
				while (!ready && tasks.size() == 0) {
					try {
						this.wait();
					} catch(Exception e) {e.printStackTrace();}
				}

				if (ready)
					/* s-a terminat prelucrarea */
					return null;

				nWaiting--;

			}
		}
		
		for (int i = 0; i < fileNames.length; i++) {
			if (noOfFragments.containsKey(fileNames[i])) {
				//int count = noOfFragments.get(fileNames[i]);
				int count = tasks.get(fileNames[i]).size();
				if (count >= 2) {
					// returnez 2 fragmente:
					ReducePartialSolution2 rps2;
					int last = tasks.get(fileNames[i]).size() - 1;
					rps2 = new ReducePartialSolution2(tasks.get(fileNames[i]).remove(last).getHMap(), 
									tasks.get(fileNames[i]).remove(last-1).getHMap(), fileNames[i]);
					
					// actualizez cate fragmente mai sunt din fisierul respectiv
					count -= 2;
					noOfFragments.put(fileNames[i], count);
					
					if (count == 0 && isFileProcessed(fileNames[i]) == false) {
						// am terminat de procesat toate fragmentele pt fis asta
						rps2.setIsLast();
						noOfFragments.remove(fileNames[i]);
						tasks.remove(fileNames[i]);
					}
					
					// retin ca se proceseaza 2 fragmente din fisierul acesta
					crtFragProcessed.add(fileNames[i]);
					
					return rps2;
				} else {
		
					if (count == 1) {
						if (isFileProcessed(fileNames[i]) == false) {
							// atunci e ultimul fragment
							ReducePartialSolution2 rps2;
							HashMap<String, Integer> h2 = new HashMap<>();
							int last = tasks.get(fileNames[i]).size() - 1;
							rps2 = new ReducePartialSolution2(tasks.get(fileNames[i]).remove(last).getHMap(), 
																h2, fileNames[i]);
							
							// actualizez cate fragmente mai sunt din fisierul respectiv
							count -= 1;
							noOfFragments.put(fileNames[i], count);
							
							// am terminat de procesat toate fragmentele
							rps2.setIsLast();
							noOfFragments.remove(fileNames[i]);
							tasks.remove(fileNames[i]);
							
							return rps2;
						}
					}
				}
			}
		}

		return new ReducePartialSolution2(true);
	}
	
	
	
	/**
	 * Functie care introduce un task in workpool.
	 * @param sp - task-ul care trebuie introdus 
	 */
	synchronized void putWork(ReducePartialSolution rps) {

		String name = rps.getFileName();
		// adaug in arrayListul corespunzator numelui fisierului din care face parte fragmentul
		if (tasks.containsKey(name)) {
			tasks.get(name).add(rps);
		} else {
			tasks.put(name, new ArrayList<ReducePartialSolution>());
			tasks.get(name).add(rps);
		}
		
		// nr si cate fragmente din fisierul respectiv am
		if (noOfFragments.containsKey(name)) {
			int nr = noOfFragments.get(name);
			noOfFragments.put(name, nr + 1);
		} else {
			noOfFragments.put(name, 1);
		}
		
		// daca am facut un putWork, inseamna ca am terminat de procesat acest fragment:
		for (int i = 0; i < crtFragProcessed.size(); i++) {
			if (crtFragProcessed.get(i).equals(name)) {
				crtFragProcessed.remove(i);
				break;
			}
		}
		
		
		/* anuntam unul dintre workerii care asteptau */
		this.notify();

	}
	
	synchronized boolean isFileProcessed(String filename) {
		for (int i = 0; i < crtFragProcessed.size(); i++) {
			if (crtFragProcessed.get(i).equals(filename)) {
				return true;
			}
		}
		return false;
	}
	
	synchronized void incNoOfFragments(String filename) {
		if (noOfFragments.containsKey(filename)) {
			int oldNo = noOfFragments.get(filename);
			noOfFragments.put(filename, oldNo + 1);
		} else {
			noOfFragments.put(filename, 1);
		}
	}
	
	synchronized int getNoOfFragments(String filename) {
		if (noOfFragments.containsKey(filename)) {
			return noOfFragments.get(filename);
		} else {
			return 0;
		}
	}
	
	synchronized void setNoOfFragments(String filename, int number) {
		noOfFragments.put(filename, number);
	}
	
	public void setFileNames(String fileNames[]) {
		this.fileNames = fileNames;
	}
	
	public void addResult(String filename, HashMap<String, Integer> h){
		finalRezults.put(filename, h);
	}
}