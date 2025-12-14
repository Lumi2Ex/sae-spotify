package musique;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Classe Song - Représente une chanson du fichier CSV Spotify
 */
class Song {
    String trackName;           // Nom de la chanson
    String albumName;           // Nom de l'album
    String albumType;           // Type d'album (single, album, compilation)
    String releaseDate;         // Date de sortie
    String durationMs;          // Durée en millisecondes
    String[] artists;           // Tableau des artistes (jusqu'à 12)
    int albumPopularity;        // Popularité (0-100)
    String[] allFields;         // Tous les champs CSV
    
    public Song(String[] fields) {
        this.allFields = fields;
        this.trackName = getFieldSafe(fields, 0);
        this.albumName = getFieldSafe(fields, 7);
        this.albumType = getFieldSafe(fields, 4);
        this.releaseDate = getFieldSafe(fields, 8);
        this.durationMs = getFieldSafe(fields, 3);
        
        // Construction du tableau d'artistes (colonnes 13-24)
        ArrayList<String> artistList = new ArrayList<>();
        for (int i = 13; i <= 24; i++) {
            String artist = getFieldSafe(fields, i);
            if (!artist.isEmpty()) {
                artistList.add(artist);
            }
        }
        this.artists = artistList.toArray(new String[0]);
        
        // Popularité
        try {
            this.albumPopularity = Integer.parseInt(getFieldSafe(fields, 10));
        } catch (NumberFormatException e) {
            this.albumPopularity = 0;
        }
    }
    
    private String getFieldSafe(String[] fields, int index) {
        return (index >= 0 && index < fields.length) ? fields[index] : "";
    }
    
    public String getArtistsString() {
        return artists.length > 0 ? String.join(", ", artists) : "Unknown";
    }
    
    public int getYear() {
        try {
            return Integer.parseInt(releaseDate.substring(0, 4));
        } catch (Exception e) {
            return 0;
        }
    }
    
    @Override
    public String toString() {
        return String.format("%-40s | %-30s | %-20s | %4d | Pop: %3d", 
            truncate(trackName, 40),
            truncate(getArtistsString(), 30),
            truncate(albumName, 20),
            getYear(),
            albumPopularity);
    }
    
    private String truncate(String s, int length) {
        return s.length() > length ? s.substring(0, length-3) + "..." : s;
    }
}

/**
 * Classe principale avec menu et algorithmes de tri/recherche
 */
public class Musique implements MusiqueInterface {
    
    // IMPORTANT: Changer ici pour comparer ArrayList vs LinkedList
    private List<Song> songs;
    private String currentImplementation;
    private Scanner scanner;
    
    public Musique(boolean useArrayList) {
        // Choix de l'implémentation
        if (useArrayList) {
            songs = new ArrayList<>();
            currentImplementation = "ArrayList";
        } else {
            songs = new java.util.LinkedList<>();
            currentImplementation = "LinkedList";
        }
        scanner = new Scanner(System.in);
    }
    
    @Override
    public void charger(String path) throws Exception {
        System.out.println("\n=== CHARGEMENT avec " + currentImplementation + " ===");
        long startTime = System.currentTimeMillis();
        
        BufferedReader csvReader = new BufferedReader(new FileReader(path));
        String row;
        
        // Ignorer l'en-tête
        csvReader.readLine();
        
        int count = 0;
        while ((row = csvReader.readLine()) != null) {
            String[] fields = row.split(",");
            if (fields.length >= 10) {
                songs.add(new Song(fields));
                count++;
            }
        }
        csvReader.close();
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("✓ Chargé: " + count + " chansons");
        System.out.println("✓ Temps: " + duration + " ms");
        System.out.println("✓ Structure: " + currentImplementation);
    }
    
    @Override
    public void afficher() {
        System.out.println("\n=== AFFICHAGE (" + songs.size() + " chansons) ===");
        System.out.println(String.format("%-40s | %-30s | %-20s | %4s | %s", 
            "TITRE", "ARTISTE", "ALBUM", "ANNÉE", "POP"));
        System.out.println("-".repeat(120));
        
        // Limiter l'affichage selon la taille
        int displayCount = songs.size() > 1000 ? songs.size() / 100 : 
                          songs.size() > 100 ? 50 : songs.size();
        
        for (int i = 0; i < Math.min(displayCount, songs.size()); i++) {
            System.out.println(i + ". " + songs.get(i));
        }
        
        if (displayCount < songs.size()) {
            System.out.println("... (" + (songs.size() - displayCount) + " autres chansons)");
        }
    }
    
    // ========== TRI SÉLECTION (Manuel) ==========
    public void triSelection() {
        System.out.println("\n=== TRI SÉLECTION (par popularité) ===");
        long startTime = System.currentTimeMillis();
        
        int n = songs.size();
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            
            // Trouver le minimum dans la partie non triée
            for (int j = i + 1; j < n; j++) {
                if (songs.get(j).albumPopularity < songs.get(minIdx).albumPopularity) {
                    minIdx = j;
                }
            }
            
            // Échanger
            if (minIdx != i) {
                Song temp = songs.get(i);
                songs.set(i, songs.get(minIdx));
                songs.set(minIdx, temp);
            }
            
            // Progression (pour grandes listes)
            if (n > 1000 && i % (n / 10) == 0) {
                System.out.print(".");
            }
        }
        
        long endTime = System.currentTimeMillis();
        System.out.println("\n✓ Tri Sélection terminé en " + (endTime - startTime) + " ms");
    }
    
    // ========== TRI FUSION (Manuel) ==========
    public void triFusion() {
        System.out.println("\n=== TRI FUSION (par popularité) ===");
        long startTime = System.currentTimeMillis();
        
        Song[] temp = new Song[songs.size()];
        songs.toArray(temp);
        triFusionRecursif(temp, 0, temp.length - 1);
        
        // Remettre dans la liste
        songs.clear();
        for (Song s : temp) {
            songs.add(s);
        }
        
        long endTime = System.currentTimeMillis();
        System.out.println("✓ Tri Fusion terminé en " + (endTime - startTime) + " ms");
    }
    
    private void triFusionRecursif(Song[] arr, int left, int right) {
        if (left < right) {
            int middle = (left + right) / 2;
            triFusionRecursif(arr, left, middle);
            triFusionRecursif(arr, middle + 1, right);
            fusion(arr, left, middle, right);
        }
    }
    
    private void fusion(Song[] arr, int left, int middle, int right) {
        int n1 = middle - left + 1;
        int n2 = right - middle;
        
        Song[] L = new Song[n1];
        Song[] R = new Song[n2];
        
        for (int i = 0; i < n1; i++) L[i] = arr[left + i];
        for (int j = 0; j < n2; j++) R[j] = arr[middle + 1 + j];
        
        int i = 0, j = 0, k = left;
        
        while (i < n1 && j < n2) {
            if (L[i].albumPopularity <= R[j].albumPopularity) {
                arr[k++] = L[i++];
            } else {
                arr[k++] = R[j++];
            }
        }
        
        while (i < n1) arr[k++] = L[i++];
        while (j < n2) arr[k++] = R[j++];
    }
    
    // ========== TRI JAVA (TimSort) ==========
    @Override
    public void sort(int criteria) {
        System.out.println("\n=== TRI JAVA (TimSort) ===");
        long startTime = System.currentTimeMillis();
        
        switch (criteria) {
            case 0: // Titre
                Collections.sort(songs, (s1, s2) -> s1.trackName.compareTo(s2.trackName));
                System.out.println("Critère: Titre");
                break;
            case 1: // Popularité
                Collections.sort(songs, (s1, s2) -> Integer.compare(s1.albumPopularity, s2.albumPopularity));
                System.out.println("Critère: Popularité");
                break;
            case 2: // Année
                Collections.sort(songs, (s1, s2) -> Integer.compare(s1.getYear(), s2.getYear()));
                System.out.println("Critère: Année");
                break;
            default:
                System.out.println("Critère invalide");
                return;
        }
        
        long endTime = System.currentTimeMillis();
        System.out.println("✓ Tri Java terminé en " + (endTime - startTime) + " ms");
    }
    
    // ========== FILTRES ==========
    @Override
    public void filter(String text, int columnIndex) {
        System.out.println("\n=== FILTRE MANUEL (par année) ===");
        long startTime = System.currentTimeMillis();
        
        try {
            int year = Integer.parseInt(text);
            int initialSize = songs.size();
            
            // Parcours inverse pour éviter les problèmes d'index
            for (int i = songs.size() - 1; i >= 0; i--) {
                if (songs.get(i).getYear() != year) {
                    songs.remove(i);
                }
            }
            
            long endTime = System.currentTimeMillis();
            System.out.println("✓ Filtré: " + (initialSize - songs.size()) + " chansons supprimées");
            System.out.println("✓ Restantes: " + songs.size() + " chansons");
            System.out.println("✓ Temps: " + (endTime - startTime) + " ms");
        } catch (NumberFormatException e) {
            System.out.println("❌ Année invalide");
        }
    }
    
    public void filtreJava(String critere, String valeur) {
        System.out.println("\n=== FILTRE JAVA (removeIf) ===");
        long startTime = System.currentTimeMillis();
        
        int initialSize = songs.size();
        
        switch (critere) {
            case "artiste":
                songs.removeIf(s -> !s.getArtistsString().toLowerCase().contains(valeur.toLowerCase()));
                break;
            case "album":
                songs.removeIf(s -> !s.albumName.toLowerCase().contains(valeur.toLowerCase()));
                break;
            case "titre":
                songs.removeIf(s -> !s.trackName.toLowerCase().contains(valeur.toLowerCase()));
                break;
            case "type":
                songs.removeIf(s -> !s.albumType.equalsIgnoreCase(valeur));
                break;
            default:
                System.out.println("❌ Critère invalide");
                return;
        }
        
        long endTime = System.currentTimeMillis();
        System.out.println("✓ Filtré: " + (initialSize - songs.size()) + " chansons supprimées");
        System.out.println("✓ Restantes: " + songs.size() + " chansons");
        System.out.println("✓ Temps: " + (endTime - startTime) + " ms");
    }
    
    // ========== RECHERCHE LINÉAIRE ==========
    @Override
    public void search(String titre) {
        System.out.println("\n=== RECHERCHE LINÉAIRE ===");
        long startTime = System.currentTimeMillis();
        
        int comparisons = 0;
        Song found = null;
        
        for (Song song : songs) {
            comparisons++;
            if (song.trackName.equalsIgnoreCase(titre)) {
                found = song;
                break;
            }
        }
        
        long endTime = System.currentTimeMillis();
        
        if (found != null) {
            System.out.println("✓ Trouvé: " + found);
        } else {
            System.out.println("✗ Titre non trouvé");
        }
        System.out.println("✓ Comparaisons: " + comparisons);
        System.out.println("✓ Temps: " + (endTime - startTime) + " ms");
    }
    
    // ========== RECHERCHE DICHOTOMIQUE ==========
    public void rechercheDichotomique(String titre) {
        System.out.println("\n=== RECHERCHE DICHOTOMIQUE ===");
        System.out.println("⚠ La liste doit être triée par titre!");
        
        long startTime = System.currentTimeMillis();
        
        int left = 0, right = songs.size() - 1;
        int comparisons = 0;
        Song found = null;
        
        while (left <= right) {
            comparisons++;
            int mid = left + (right - left) / 2;
            Song midSong = songs.get(mid);
            int cmp = midSong.trackName.compareToIgnoreCase(titre);
            
            if (cmp == 0) {
                found = midSong;
                break;
            } else if (cmp < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        
        long endTime = System.currentTimeMillis();
        
        if (found != null) {
            System.out.println("✓ Trouvé: " + found);
        } else {
            System.out.println("✗ Titre non trouvé");
        }
        System.out.println("✓ Comparaisons: " + comparisons);
        System.out.println("✓ Temps: " + (endTime - startTime) + " ms");
    }
    
    // ========== SUPPRESSION UN À UN ==========
    public void suppressionUnAUn() {
        System.out.println("\n=== SUPPRESSION UN À UN (test performance) ===");
        System.out.println("⚠ Ceci va vider toute la liste!");
        
        int initialSize = songs.size();
        long startTime = System.currentTimeMillis();
        
        while (!songs.isEmpty()) {
            songs.remove(0); // Suppression du premier élément
        }
        
        long endTime = System.currentTimeMillis();
        System.out.println("✓ Supprimé: " + initialSize + " chansons");
        System.out.println("✓ Temps: " + (endTime - startTime) + " ms");
    }
    
    // Méthodes de l'interface (non utilisées ici)
    @Override
    public void printRow(int index) {}
    
    @Override
    public void printColumn(int index) {}
    
    // ========== MENU PRINCIPAL ==========
    public void menu() {
        while (true) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("MENU PRINCIPAL - Structure: " + currentImplementation);
            System.out.println("Chansons chargées: " + songs.size());
            System.out.println("=".repeat(60));
            System.out.println("1. Charger un fichier");
            System.out.println("2. Afficher les données");
            System.out.println("3. Trier");
            System.out.println("4. Filtrer");
            System.out.println("5. Rechercher");
            System.out.println("6. Tests de performance");
            System.out.println("0. Quitter");
            System.out.print("\nChoix: ");
            
            int choix = scanner.nextInt();
            scanner.nextLine(); // Consommer le \n
            
            try {
                switch (choix) {
                    case 1: menuCharger(); break;
                    case 2: afficher(); break;
                    case 3: menuTrier(); break;
                    case 4: menuFiltrer(); break;
                    case 5: menuRechercher(); break;
                    case 6: menuPerformance(); break;
                    case 0: 
                        System.out.println("Au revoir!");
                        return;
                    default:
                        System.out.println("❌ Choix invalide");
                }
            } catch (Exception e) {
                System.out.println("❌ Erreur: " + e.getMessage());
            }
        }
    }
    
    private void menuCharger() throws Exception {
        System.out.println("\n=== CHARGER UN FICHIER ===");
        System.out.println("1. spotify_100.csv");
        System.out.println("2. spotify_1000.csv");
        System.out.println("3. spotify_10000.csv");
        System.out.println("4. spotify_100000.csv");
        System.out.println("5. spotify_FULL.csv");
        System.out.print("Choix: ");
        
        int choix = scanner.nextInt();
        scanner.nextLine();
        
        String[] files = {"", "src/spotify_100.csv", "src/spotify_1000.csv", 
                         "src/spotify_10000.csv", "src/spotify_100000.csv", "src/spotify_FULL.csv"};
        
        if (choix >= 1 && choix <= 5) {
            charger(files[choix]);
        }
    }
    
    private void menuTrier() {
        System.out.println("\n=== TRIER ===");
        System.out.println("1. Tri Sélection (par popularité)");
        System.out.println("2. Tri Fusion (par popularité)");
        System.out.println("3. Tri Java - par titre");
        System.out.println("4. Tri Java - par popularité");
        System.out.println("5. Tri Java - par année");
        System.out.print("Choix: ");
        
        int choix = scanner.nextInt();
        scanner.nextLine();
        
        switch (choix) {
            case 1: triSelection(); break;
            case 2: triFusion(); break;
            case 3: sort(0); break;
            case 4: sort(1); break;
            case 5: sort(2); break;
        }
    }
    
    private void menuFiltrer() {
        System.out.println("\n=== FILTRER ===");
        System.out.println("1. Filtre manuel (par année)");
        System.out.println("2. Filtre Java (par artiste)");
        System.out.println("3. Filtre Java (par album)");
        System.out.println("4. Filtre Java (par titre)");
        System.out.println("5. Filtre Java (par type)");
        System.out.print("Choix: ");
        
        int choix = scanner.nextInt();
        scanner.nextLine();
        
        if (choix == 1) {
            System.out.print("Année: ");
            String annee = scanner.nextLine();
            filter(annee, 8);
        } else if (choix >= 2 && choix <= 5) {
            String[] criteres = {"", "", "artiste", "album", "titre", "type"};
            System.out.print("Valeur à rechercher: ");
            String valeur = scanner.nextLine();
            filtreJava(criteres[choix], valeur);
        }
    }
    
    private void menuRechercher() {
        System.out.println("\n=== RECHERCHER ===");
        System.out.println("1. Recherche linéaire");
        System.out.println("2. Recherche dichotomique (liste doit être triée!)");
        System.out.print("Choix: ");
        
        int choix = scanner.nextInt();
        scanner.nextLine();
        
        System.out.print("Titre à rechercher: ");
        String titre = scanner.nextLine();
        
        if (choix == 1) {
            search(titre);
        } else if (choix == 2) {
            rechercheDichotomique(titre);
        }
    }
    
    private void menuPerformance() {
        System.out.println("\n=== TESTS DE PERFORMANCE ===");
        System.out.println("1. Test suppression un à un");
        System.out.println("2. Comparer tous les tris");
        System.out.print("Choix: ");
        
        int choix = scanner.nextInt();
        scanner.nextLine();
        
        if (choix == 1) {
            suppressionUnAUn();
        } else if (choix == 2) {
            // Sauvegarder une copie
            System.out.println("⚠ Cette opération va trier la liste 3 fois");
        }
    }
    
    // ========== MAIN ==========
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        System.out.println("=== SAE 1.02 - Exploration de données musicales ===");
        System.out.println("\nChoisir l'implémentation:");
        System.out.println("1. ArrayList");
        System.out.println("2. LinkedList");
        System.out.print("Choix: ");
        
        int choix = sc.nextInt();
        boolean useArrayList = (choix == 1);
        
        Musique app = new Musique(useArrayList);
        app.menu();
        
        sc.close();
    }
}