package spotify;

import com.univocity.parsers.csv.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Musique implements MusiqueInterface {

    // Liste contenant toutes les lignes du fichier CSV, chaque ligne est un tableau de chaînes
    private List<String[]> lignes;

    // Copie originale des données chargées, utilisée pour restaurer après filtrage
    private List<String[]> original;

    /**
     * Méthode pour charger un fichier CSV.
     * @param path Chemin du fichier CSV à charger.
     * @throws Exception en cas d'erreur lors de la lecture du fichier.
     */
    @Override
public void charger(String path) throws Exception {

    System.out.println("Chargement du fichier : " + path);

    // Charger le fichier depuis le dossier 'resources' en utilisant le class loader
    InputStream is = Musique.class.getClassLoader().getResourceAsStream(path);

    // Vérifier si le fichier n'a pas été trouvé dans les ressources
    if (is == null) {
        throw new FileNotFoundException("Fichier non trouvé dans les ressources : " + path);
    }

    // Configuration du parser CSV Univocity
    CsvParserSettings settings = new CsvParserSettings();
    settings.setHeaderExtractionEnabled(false); // Pas de header dans ce CSV
    settings.setSkipEmptyLines(true); // Ignorer les lignes vides
    settings.setLineSeparatorDetectionEnabled(true); // Détecter automatiquement les séparateurs de ligne
    settings.setIgnoreLeadingWhitespaces(true); // Ignorer les espaces au début
    settings.setIgnoreTrailingWhitespaces(true); // Ignorer les espaces à la fin

    CsvParser parser = new CsvParser(settings);

    // Lecture du fichier à partir de l'InputStream
    try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
        // Charger toutes les lignes dans la liste 'lignes'
        lignes = parser.parseAll(br);
    }

    // Sauvegarde des données originales pour le filtrage
    original = new ArrayList<>(lignes);

    System.out.println("Chargement terminé : " + lignes.size() + " lignes chargées.");
}


    /**
     * Affiche les premières lignes chargées, jusqu'à un maximum de 200 lignes pour éviter d'inonder la console.
     */
    @Override
    public void afficher() {
        if (lignes == null || lignes.isEmpty()) {
            System.out.println("Aucune donnée chargée.");
            return;
        }

        // Limite d'affichage pour éviter trop de données dans la console
        int limit = Math.min(200, lignes.size());

        System.out.println("Affichage des " + limit + " premières lignes :");
        for (int i = 0; i < limit; i++) {
            System.out.println(String.join(" | ", lignes.get(i)));
        }
    }

    /**
     * Trie les données selon la colonne spécifiée.
     * @param column index de la colonne sur laquelle trier.
     */
    @Override
    public void sort(int column) {
        if (lignes == null || lignes.isEmpty()) return;

        // Vérification que l'index de colonne est valide
        String[] firstRow = lignes.get(0);
        if (column < 0 || column >= firstRow.length) {
            System.out.println("Index de colonne invalide pour le tri : " + column);
            return;
        }

        // Détection si la colonne contient des données numériques
        boolean numeric = lignes.stream()
                .map(r -> r[column])
                .filter(Objects::nonNull)
                .anyMatch(v -> v.matches("-?\\d+(\\.\\d+)?"));

        if (numeric) {
            // Tri numérique (double), en convertissant chaque valeur, en plaçant les valeurs invalides à la fin
            lignes.sort(Comparator.comparingDouble(a -> {
                try {
                    return Double.parseDouble(a[column]);
                } catch (Exception e) {
                    return Double.MAX_VALUE; // Valeur très grande pour mettre à la fin
                }
            }));
            System.out.println("Tri numérique effectué sur la colonne " + column);
        } else {
            // Tri alphabétique (par défaut) avec gestion des valeurs nulles
            lignes.sort(Comparator.comparing(a -> a[column] == null ? "" : a[column]));
            System.out.println("Tri alphabétique effectué sur la colonne " + column);
        }
    }

    /**
     * Filtre les lignes dont la 3e colonne (index 2) contient la chaîne passée en paramètre (insensible à la casse).
     * Si la chaîne est vide ou nulle, restaure toutes les données d'origine.
     * @param text texte à chercher dans la colonne genre.
     */
    @Override
    public void filter(String text) {
        if (lignes == null || lignes.isEmpty()) return;

        // Si texte vide ou nul, on restaure la liste originale complète
        if (text == null || text.trim().isEmpty()) {
            lignes = new ArrayList<>(original);
            System.out.println("Filtre supprimé — restauration de l'ensemble des données.");
            return;
        }

        // Passage en minuscules pour comparaison insensible à la casse
        String lower = text.toLowerCase();

        // Suppression des lignes ne contenant pas le texte dans la 3e colonne
        lignes.removeIf(row -> row.length < 3 ||
                row[2] == null ||
                !row[2].toLowerCase().contains(lower));

        System.out.println("Filtrage effectué : genre contenant \"" + text + "\".");
    }

    /**
     * Recherche et affiche les lignes dont la 2e colonne (index 1) contient la chaîne passée (insensible à la casse).
     * @param text texte à chercher dans la colonne recherche.
     */
    @Override
    public void search(String text) {
        if (lignes == null || lignes.isEmpty()) return;

        String lower = text.toLowerCase();
        System.out.println("Résultats de la recherche :");

        for (String[] row : lignes) {
            if (row.length > 1 &&
                    row[1] != null &&
                    row[1].toLowerCase().contains(lower)) {
                System.out.println(String.join(" | ", row));
            }
        }
    }

    /**
     * Affiche la ligne à l'index spécifié.
     * @param index index de la ligne à afficher.
     */
    @Override
    public void printRow(int index) {
        if (lignes == null || lignes.isEmpty()) {
            System.out.println("Aucune donnée chargée.");
            return;
        }
        if (index < 0 || index >= lignes.size()) {
            System.out.println("Index de ligne invalide : " + index);
            return;
        }

        System.out.println(String.join(" | ", lignes.get(index)));
    }

    /**
     * Affiche la colonne spécifiée pour toutes les lignes.
     * @param index index de la colonne à afficher.
     */
    @Override
    public void printColumn(int index) {
        if (lignes == null || lignes.isEmpty()) {
            System.out.println("Aucune donnée chargée.");
            return;
        }

        String[] first = lignes.get(0);
        if (index < 0 || index >= first.length) {
            System.out.println("Index de colonne invalide : " + index);
            return;
        }

        for (String[] row : lignes) {
            if (index < row.length)
                System.out.println(row[index]);
            else
                System.out.println("(donnée manquante)");
        }
    }

    /**
     * Point d'entrée principal pour tester la classe.
     */
    public static void main(String[] args) throws Exception {
        Musique m = new Musique();

        // Chargement du fichier CSV
        m.charger("spotify_FULL.csv");

        // Tri sur la première colonne (index 0)
        m.sort(0);

        // Affichage de la première colonne
        m.printColumn(0);
    }
}
