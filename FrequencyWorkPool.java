/* Ditu Alexandru 333CA Tema2 APD */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Clasa ce implementeaza un "work pool" conform modelului "replicated workers".
 * Task-urile introduse in work pool sunt obiecte de tipul PartialSolution.
 *
 */
public class FrequencyWorkPool {
	int nThreads; // nr total de thread-uri worker
	int nWaiting = 0; // nr de thread-uri worker care sunt blocate asteptand un task
	public boolean ready = false; // daca s-a terminat complet rezolvarea problemei 
	
	// cate fragmente sunt din fiecare fisier
	HashMap<String, Integer> noOfFragments;
	
	// din ce fisier proceseaza fiecare thread
	String []crtFragProcessed;
	
	// Cuvintele si frecventa lor din fisierul de test
	HashMap <String, Float> inDocMap;
	
	// Rezultatele finale cu gradul de similaritate pentru fiecare fisier
	ArrayList<GenericPair> gradSimFis;
	
	LinkedList<FrequencyPartialSolution> tasks = new LinkedList<>();

	/**
	 * Constructor pentru clasa WorkPool.
	 * @param nThreads - numarul de thread-uri worker
	 */
	public FrequencyWorkPool(int nThreads, HashMap<String, Float> inDocMap) {
		this.nThreads = nThreads;
		this.inDocMap = inDocMap;
		gradSimFis = new ArrayList<GenericPair>();		
	}

	/**
	 * Functie care incearca obtinera unui task din workpool.
	 * Daca nu sunt task-uri disponibile, functia se blocheaza pana cand 
	 * poate fi furnizat un task sau pana cand rezolvarea problemei este complet
	 * terminata
	 * @return Un task de rezolvat, sau null daca rezolvarea problemei s-a terminat 
	 */
	public synchronized PartialSolution getWork() {
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
		return tasks.remove();

	}
	


	/**
	 * Functie care introduce un task in workpool.
	 * @param sp - task-ul care trebuie introdus 
	 */
	synchronized void putWork(PartialSolution sp) {
		//System.out.println("WorkPool - adaugare task: " + sp);
		tasks.add((FrequencyPartialSolution)sp);
		/* anuntam unul dintre workerii care asteptau */
		this.notify();

	}
	
	synchronized void addGradSimFis(GenericPair newPair) {
		gradSimFis.add(newPair);
	}
	
	synchronized HashMap<String, Float> getInDocMap() {
		return new HashMap<String, Float> (inDocMap); 
	}
	
	public ArrayList<GenericPair> getGradSim() {
		return gradSimFis;
	}


}


