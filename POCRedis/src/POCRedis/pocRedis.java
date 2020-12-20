package POCRedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;

public class pocRedis {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		try {
			Jedis jedis = new Jedis("localhost");
			// NETTOIE LA BASE DE DONNEE
			// 
			System.out.println(jedis.flushAll());
			
			// CHECK CONNEXION
			// Retourne "PONG" en cas de reussite sinon timeout
			System.out.println("Server ping : "+jedis.ping());
			//////////////////
			
			// AFFICHER INFO SERVEUR
			// Retourne un string comprenant des informations serveurs
			//System.out.println("Server Info : " + jedis.info());
			////////////////////////
			
			// LISTER TOUTES LES CONVERSATIONS
			// Retourne un Set<String>
			jedis.smembers(getUsername());
			//////////////////////////////////
			// Permets d'�viter une requ�te a la base de donn�e m�re
			//////////////////////////////////
			
			// AJOUTER UNE CONVERSATION A GETUSERNAME()
			// Retourne 1 en cas d'ajout r�ussi et 0 sinon
			jedis.sadd(getUsername(),getOtherUsername());
			/////////////////////////////////////////
			// sadd emp�che la duplication des informations en empechant la cr�ation de donn�es en double
			/////////////////////////////////////////
			
			// DECONNECTER DU SERVEUR
			// Pas de valeur de retour
			jedis.disconnect();
			/////////////////////////
			// Peut-�tre bon de d�connecter apr�s utilisation �tant donn� l'exceptionnalit� de la cr�ation d'une nouvelle conversation
			/////////////////////////
			
			
			for(int i = 0 ; i < 4 ; i++)
			{
				System.out.println("Ajout d'une conversation : " + jedis.sadd(getUsername(), getOtherUsername()));
			}
			System.out.println("Liste des conversations : " + jedis.smembers(getUsername()));

			
		} catch (Exception e)
		{
			System.out.println(e);
		}
	}
	
	static String getUsername()
	{
		return "Yann";
	}
	
	static String getOtherUsername()
	{
		int i = (int)(Math.random() * 10);
		String[] noms = {"Thomas", "Lucas", "Yann", "Jacob", "Andre", "Benjamin", "Kevin", "Louis", "Jean", "Nicolas", "Guillaume"};
		return noms[i];
	}

}
