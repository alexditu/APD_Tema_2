/* Ditu Alexandru 333CA Tema2 APD */
/**
 * Clasa ce reprezinta un thread worker.
 */


import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Map.Entry;

class Worker extends Thread {
	WorkPool wp;
	ReducedWorkPool rwp;
	FrequencyWorkPool fwp;
	int workPollType;
	
	
	public Worker(WorkPool workpool, int type) {
		this.wp = workpool;
		workPollType = type;
	}
	
	public Worker(ReducedWorkPool workpool, int type) {
		this.rwp = workpool;
		workPollType = type;
	}
	
	public Worker(FrequencyWorkPool workpool, int type) {
		this.fwp = workpool;
		workPollType = type;
	}

	/**
	 * Procesarea unei solutii partiale. Aceasta poate implica generarea unor
	 * noi solutii partiale care se adauga in workpool folosind putWork().
	 * Daca s-a ajuns la o solutie finala, aceasta va fi afisata.
	 * @throws IOException 
	 */
	void processPartialSolution(MapPartialSolution ps) throws IOException {
		
		ReducePartialSolution rps = new ReducePartialSolution(ps.getFileName());
		
		String delim = "\t\n\r\f .-(),";
		
		RandomAccessFile raf = new RandomAccessFile(ps.getFileName(), "r");
		int start = ps.getStartPosition();
		
		// verific la stanga
		int startPos = 0;
		if (start != 0) {
			byte c1, c2;
			if (start - 1 >= 0) {
				raf.seek(start-1);
				c1 = raf.readByte();
				c2 = raf.readByte();
				
				// daca c2 nu e delimitator si nici c1 inseamna ca nu sunt la inceput de cuvant
				if (!delim.contains((char)c2 + "") && !delim.contains((char)c1 + "")) {
					// atunci trebuie sa sar peste cuvantul curent deoarece a mai fost citit
					startPos = 1;
					while (!delim.contains((char)raf.readByte() + "") && start + startPos < raf.length() - 1) {
						startPos ++;
					}
				}
			}
		}
		
		// verific la dreapta
		int end = ps.getEndPostion();
		// tb sa nu citesc EOF!!!!
		while (end > raf.length() - 2) {
			end--;
		}
		raf.seek(end);
		byte c = raf.readByte();
		int endPos = 0;
		// cat timp nu e delimitator sar peste cuvinte
		while ((delim.contains((char)c + "") == false) && (end + endPos < raf.length() - 1)) {
			endPos ++;
			c = raf.readByte();
		}
		
		int length = (end + endPos) - (start + startPos) + 1;
		
		if (length == 0) {
			raf.close();
			return;
		}
		
		// citesc acuma efectiv fisierul
		byte []buf = new byte[length];
		raf.seek(start + startPos);
		
		if (raf.read(buf,0, length) == -1) {
			System.out.println("Read Error!");
		}
		
		String buffer = new String(buf);
		
		// parsez fragmentul de fisier
		StringTokenizer stk = new StringTokenizer(buffer, delim);
		
		// daca am gasit numai delimitatori, nu mai creez hashMap pentru reduce
		boolean foundTokens = false;
		
		while(stk.hasMoreTokens()) {
			String token = stk.nextToken().toLowerCase();
			rps.incWordFreq(token);
			foundTokens = true;
		}
		
		// adaug fragmentul pentru procesat de Reduce
		if (foundTokens) {
			ps.reduceWorkPool.putWork(rps);
			// incrementez nr de fragmente pentru fisierul curent
			ps.reduceWorkPool.incNoOfFragments(ps.getFileName());
		}
		
		raf.close();
		
	}
	
	void processPartialSolution (ReducePartialSolution2 rps) {

		HashMap <String, Integer> h1, h2;
		h1 = rps.getH1();
		h2 = rps.getH2();
		
		for(Entry<String, Integer> it : h1.entrySet()) {
			if (h2.containsKey(it.getKey())) {
				int count = it.getValue();
				count += h2.get(it.getKey());
				h2.put(it.getKey(), count);
			} else {
				h2.put(it.getKey(), it.getValue());
			}
		}
		
		if (rps.isLast()) {
			rwp.addResult(rps.getFilename(), h2);
		} else {
			ReducePartialSolution pSolution = new ReducePartialSolution(rps.getFilename(), h2);
			rwp.putWork(pSolution);
		}
	}
	
	void processPartialSolution (FrequencyPartialSolution fps) {
		HashMap<String, Float> doc1 = fwp.getInDocMap();
		HashMap<String, Integer> doc2 = fps.getHashMap();
		HashMap<String, Float> doc2f = new HashMap<>();
		
		// calculez frecventa cuvintelor din doc2
		float wordCount = 0;
		for(Entry<String, Integer> i : doc2.entrySet()) {
			wordCount += i.getValue();
		}
		for(Entry<String, Integer> i : doc2.entrySet()) {
			float freq;
			freq = (i.getValue() / wordCount) * 100;
			doc2f.put(i.getKey(), freq);
		}
		
		// calculez gradul de similaritate
		float gradSim = 0, f1, f2;
		for(Entry<String, Float> i : doc1.entrySet()) {
			String word = i.getKey();
			if (doc2.containsKey(word)) {
				f1 = i.getValue();
				f2 = doc2f.get(word);
				gradSim += f1 * f2;
			}
			// else: daca nu exista e 0
		}
		
		gradSim = gradSim / 100;
		// adaug la rezultatele finale:
		GenericPair result = new GenericPair(fps.getFileName(), gradSim);
		fwp.addGradSimFis(result);
	}
	
	
	public void run() {
		System.out.println("Thread-ul worker " + this.getName() + " a pornit...");
		while (true) {
			PartialSolution ps;
			if (workPollType == 1) {
				ps = wp.getWork();
			} else {
				if (workPollType == 2) {
					ps = rwp.getWork(this.getName());
					while (ps != null && ((ReducePartialSolution2)ps).mustWait()) {
						ps = rwp.getWork(this.getName());
					}
				} else {
					ps = fwp.getWork();
				}
			}
			
			if (ps == null) {
				break;
			}
			
			try {
				if (ps instanceof MapPartialSolution) {
					processPartialSolution((MapPartialSolution)ps);
				} else {
					if (ps instanceof ReducePartialSolution2) {
						processPartialSolution((ReducePartialSolution2)ps);
					} else {
						processPartialSolution((FrequencyPartialSolution)ps);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Thread-ul worker " + this.getName() + " s-a terminat...");
	}

	
}
