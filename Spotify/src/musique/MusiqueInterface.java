package musique;

public interface MusiqueInterface {
	void charger(String path) throws Exception;
	
	void afficher();
	
	void sort(int SortAns);
	
	void filter(String text, int index);
	
	void search(String seAns);
	
	void printRow(int index);

	void printColumn(int index);
}
