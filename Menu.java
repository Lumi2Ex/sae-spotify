public class Menu {
	public static void main(String[] args) throws Exception {
		int choix=0, choix_interne=0;
		Musique m= new Musique();
		System.out.println("bienvenue sur notre explorateur musical !");
		while(choix>=0 && choix<=5) {
			System.out.print("Que souhaitez-vous faire ? \n 1. Charger des données \n 2. Afficher les données \n 3. Filtrer les données \n 4. Trier les données \n 5. Rechercher des données concernant un titre \n 0. Infos sur le programme \n >>>");
			choix=Clavier.lireInt();
			if(choix==0) {
				System.out.println("Ce programme a été réalisé par Mathéo Rousseau et Mathis Malabry");
				System.out.println("Nous avons utilisé les cours de nos chers professeurs de dev et nos connaissances personnelles :)");
			}
			if(choix==1) {
				System.out.println("Vous avez choisi de charger des données !");
				System.out.print("Que souhaitez vous ouvrir ? \n 1. 100 Musiques \n 2. 1000 Musiques\n 3. 10000 Musiques\n 4. 100000 Musiques\n 5. Toutes les musiques\n >>> ");
				choix_interne=Clavier.lireInt();
				if(choix_interne==1) {
					System.out.println("Affichons 100 musiques !");
					m.charger("spotify_100.csv"); //charger le fichier spotify_100.csv
				}
				if(choix_interne==2) {
					System.out.println("Affichons 1000 musiques !");
					m.charger("spotify_1000.csv");
				}
				if(choix_interne==3) {
					System.out.println("Affichons 10000 musiques !");
					m.charger("spotify_10000.csv");
				}
				if(choix_interne==4) {
					System.out.println("Affichons 100000 musiques !");
					m.charger("spotify_100000.csv");
				}
				if(choix_interne==5) {
					System.out.println("Affichons toutes les musiques !");
					m.charger("spotify_FULL.csv");
				}
				
			}
			if(choix==2) {
				System.out.println("Vous avez choisi d'afficher des données !"); 
				m.afficher(); //afficher le programme qui a été chargé
				
			}
			if(choix==3) {
				System.out.println("Vous avez choisi de filtrer des données !");
				System.out.print("Vous voulez filtrer par quoi ? \n 1. Selon l'année \n 2. Selon le titre d'une chanson \n 3. Selon le titre d'album \n 4. Selon le type d'album \n 5. Selon un artiste \n>>>");
				choix_interne=Clavier.lireInt();
				if(choix_interne==1) {
					System.out.println("Filtrons par année !"); //chercher des musiques en fonction d'une info précise
				}
				if(choix_interne==2) {
					System.out.println("Filtrons selon le titre de la chanson!");
				}
				if(choix_interne==3) {
					System.out.println("Filtrons selon le titre de l'album!");
				}
				if(choix_interne==4) {
					System.out.println("Filtrons selon le type d'album !");
				}
				if(choix_interne==5) {
					System.out.println("Filtrons selon l'artiste");
				}
			}
			if(choix==4) {
				System.out.println("Vous avez choisi de trier des données !");
				System.out.print("Comment voulez vous trier ? \n 1. Par titre de chanson \n 2. Par nom d'album \n 3. Par genre d'album \n 4. Par date de sortie \n 5. Par durée de la chanson \n 6. Par liste des artistes \n 7. Par popularité\n>>>");
				choix_interne=Clavier.lireInt();
				if(choix_interne==1) {
					System.out.println("Trions par titre de chanson !"); // trier par chanson
					m.sort(1);
				}
				if(choix_interne==2) {
					System.out.println("Trions par nom d'album !");
				}
				if(choix_interne==3) {
					System.out.println("Trions par genre d'album !");
				}
				if(choix_interne==4) {
					System.out.println("Trions par date de sortie !");
				}
				if(choix_interne==5) {
					System.out.println("Trions par durée de chanson !");
				}
				if(choix_interne==6) {
					System.out.println("Trions par liste des artistes !");
				}
				if(choix_interne==7) {
					System.out.println("Trions par popularité !");
				}
			}
			if(choix==5) {
				System.out.println("Vous avez choisi de rechercher un titre !"); //chercher un titre précis
				System.out.print("Quel titre recherchez-vous ?");
			}
		}
				
	}
}