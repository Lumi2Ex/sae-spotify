package spotify;

public interface MusiqueInterface {
	void charger(String path) throws Exception;
	
	void afficher();
	
	void sort(int SortAns);
	
	void filter(String filAns);
	
	void search(String seAns);
	
	void printRow(int index);
	
}
