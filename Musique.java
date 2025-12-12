package spotify;

import com.opencsv.*;
import java.io.*;
import java.util.*;

public class Musique implements MusiqueInterface {

    // Store lines parsed as String[] fields
    private List<String[]> lignes;

    @Override
    public void charger(String path) throws Exception {

        System.out.println("Loading file: " + path);

        // Load CSV from resources OR from file system
        InputStream is = Musique.class.getResourceAsStream("/spotify_FULL.csv");

        if (is == null) {
            // fallback: try loading from disk
            File file = new File(path);
            if (!file.exists()) {
                throw new FileNotFoundException("File not found: " + path);
            }
            System.out.println("Loading from disk instead of classpath.");
            is = new FileInputStream(file);
        }

        // FAST buffered reader (4 MB buffer)
        BufferedReader br = new BufferedReader(
                new InputStreamReader(is),
                4 * 1024 * 1024
        );

        // Faster parser with no quotation parsing
        CSVParser parser = new CSVParserBuilder()
                .withSeparator(',')
                .withIgnoreQuotations(true)
                .build();

        CSVReader reader = new CSVReaderBuilder(br)
                .withCSVParser(parser)
                .build();

        lignes = new ArrayList<>(1_000_000); // optional preallocation

        String[] row;
        int count = 0;

        System.out.println("Reading CSV...");

        while ((row = reader.readNext()) != null) {
            lignes.add(row);
            count++;

            // Progress every 100k
            if (count % 100000 == 0) {
                System.out.println("Loaded " + count + " rows...");
            }
        }

        System.out.println("Finished loading " + count + " rows.");
    }

    @Override
    public void afficher() {
        if (lignes == null || lignes.isEmpty()) {
            System.out.println("No data loaded.");
            return;
        }

        for (String[] row : lignes) {
            System.out.println(String.join(" | ", row));
        }
    }

    @Override
    public void sort(int SortAns) {
        // Example: sort alphabetically by first column (index 0)
        if (lignes == null) return;

        lignes.sort(Comparator.comparing(a -> a[SortAns]));
        System.out.println("Sorted by column " + SortAns);
    }

    @Override
    public void filter(String filAns) {
        // Example filter: keep only rows where column 2 contains "pop"
        if (lignes == null) return;

        lignes.removeIf(row -> !row[2].toLowerCase().contains(filAns));
        System.out.println("Filtered by genre containing '" + filAns + "'.");
    }

    @Override
    public void search(String filSea) {
        if (lignes == null) return;

        for (String[] row : lignes) {
            if (row[1].toLowerCase().contains(filSea)) {
                System.out.println(String.join(" | ", row));
            }
        }
    }
    
	@Override
	public void printRow(int index) {
	    if (lignes == null || lignes.isEmpty()) {
	        System.out.println("No data loaded.");
	        return;
	    }
	    if (index < 0 || index >= lignes.size()) {
	        System.out.println("Invalid row index: " + index);
	        return;
	    }

	    String[] row = lignes.get(index);
	    System.out.println(String.join(" | ", row));
	}


    public static void main(String[] args) throws Exception {
        Musique m = new Musique();
        m.charger("spotify_FULL.csv");
        m.afficher();
    }
}
