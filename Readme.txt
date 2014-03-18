Tema2 APD
Ditu Alexandru 333CA


1) Specificatii sistem
	Sistem de operare: 	Linux Mint 15: Olivia (x86-64 bit)
	Kernel/Build:		3.8.0-19-generic / #29-Ubuntu SMP Wed Apr 17 18:16:28
						UTC 2013
	Processor:			Intel Core i7-2630QM @ 2.00 Ghz x 4 
						4 Core-uri
						8 Thread-uri
						Max Turbo Frequency - 2.9 Ghz
	Cache Memory:		6 MB

	Tema a fost facuta in Eclipse 4.3

2) Metoda de rezolvare a temei:
	Am abordat tema in 3 pasi:
	1. unul in care impart fisierul in fragmente si calculez numarul de cuvinte 
	  pentru fragmentul respectiv

	2. unul pentru a unifica rezultatele partiale si obtin numarul de cuvinte 
	  pentru fiecare fisier in parte

	3. unul pentru a compara gradul de similaritate intre documentul dorit si 
	  celelalte

	Pasul 1 (etapa Map):
	- in aceasta etapa Workerii folsesc metodele din clasa MapWorkPool
	- in main -clasa ReplicatedWorkers- (deci serial) calculez pozitiile de start 
	  si de final pentru fiecare fragment in parte apoi adaug in MapWorkPool 
	  solutiile partiale ce contin informatiile necesare pentru a putea
	  citi datele din fisier
	- apoi pornesc NT thread-uri ce folosesc solutiile partiale de mai sus
	- fiecare thread citeste cate un fragment din fisier (si verfica si cazul
	  "cuvintelor de margine"), apoi numara cate cuvinte sunt in fragmentul
	  respectiv
	- rezultatul obtinut (un HashMap<String, Integer>) perechi cuvant-nr aparitii
	  este adaugat intr-un HashMap<String,HashMap<String, Integer>> pentru a putea
	  tine evidenta fragmentelor in functie de fisierul din care fac parte

	Pasul 2 (etapa Reduce):
	- in aceasta etapa Worker-ii folosesc metodele din clasa ReduceWorkPool
	- rezultatele de la pasul anterior trebuiesc unificate
	- unificarea se face in paralel
	- fiecare Worker extrage din HashMap-ul obtinut anterior, cate 2 fragmente
	  (de tipul PartialSolution2)
	- combina cuvintele si nr lor de aparitii obtinand astfel inca un rezultat
	  partial, pe care il adauga iar la task-uri.
	- se repeta acest procedeu pana cand, petru fiecare fisier in parte exista
	  doar un singur task (deci nu mai au ce combina)
	- atunci acest task este scos si adaugat intr-un HashMap de rezultate finale
	  (continte toate asocierile cuvant-nr aparatii pentru fiecare fisier in parte)

	Pasul 3 (etapa de calcul al gradului de similaritate):
	- in aceasta etapa Worker-ii folosesc metodele din clasa FrequencyWorkPool
	- dupa ce am calculat numarul de aparitii pentru fiecare cuvant din fiecare
	  fisier in parte, trebuie sa mai calculez si frecventa acestora, apoi
	  gradul de similaritate
	- prima data calculez frecventa cuvintelor din fisierul DOC pentru care se
	  doreste determinarea gradului de plagiere
	- acest calcul se efectueaza in main (clasa ReplicatedWorkers)
	- rezultatul se adauga in FrequencyWorkPool
	- apoi se pornesc NT thread-uri care calculeaza pentru fiecare fisier in parte
	  frecventa cuvintelor, dupa care gradul de similaritate intre fiserul curent
	  si fisierul DOC (deci aceasta operatie se realizeaza in paralel)
	- dupa ce am calculat gradul de similaritate, in main scriu rezultatele 
	  obtinute in fisierul de output

	Acestia sunt pasii pe care i-am urmat in rezolvarea temei. Am ales aceasta
abordare deoarece am incercat sa fac cat mai multe task-uri in paralel. Nu cred
ca pueam sa fac tema in 2 pasi deoarece fiecare pas depindea de rezultatele 
pasului anterior.

Mentionez ca delimitatorii folositi sunt: "\t\n\r\f .-()," si am folosit
scheletul de la laboratorul 5 : ReplicatedWorkers.
