package game.pengo.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import engine.geometry.Grid;
import engine.model.Entity;

public class PengoMapLoader {
	//lire le fichier text et retourne un tableau de lignes 
	// on met throws IOException in case le fichier n'existe pas ,ne peut pas etre lu ou methode a echoué
	  public static String[] readMap(String path) throws IOException {
	        assert path != null;

	        List<String> lines = new ArrayList<String>();

	        BufferedReader br = new BufferedReader(new FileReader(path));

	        String line;
	        //on lit le fichier jusqua le fin;
	        while ((line = br.readLine()) != null) {
	            if (!line.isEmpty()) {
	                lines.add(line);
	            }
	        }

	        br.close();//on ferme le ficher when we are finished 

	        if (lines.isEmpty()) {
	            throw new IllegalArgumentException("Map vide : " + path);
	        }
	        int width = lines.get(0).length();//on prend la longueur de la première ligne comme largeur de la map;

	        for (String l : lines) {
	            if (l.length() != width) {
	                throw new IllegalArgumentException("Toutes les lignes doivent avoir la même taille");
	            }
	        }

	        return lines.toArray(new String[0]);
	    }
	  public static int width(String[] map) {
	        assert map != null;
	        return map[0].length();
	    }

	    public static int height(String[] map) {
	        assert map != null;
	        return map.length;
	    }
	    //on cree les entites correspondantes 
	    public static void load(PengoModel model, String[] map) {
	        assert model != null;
	        assert map != null;

	        Grid grid = model.grid();//on recupere la grille

	        for (int y = 0; y < map.length; y++) {
	            String line = map[y];

	            for (int x = 0; x < line.length(); x++) {//on parcour chaque caracter de la ligne en corrdonnée horizontale
	                char c = line.charAt(x);//recupere le symbole de la position x y

	                Entity e = createEntity(c);//on cree lentite correspondante 

	                if (e != null) {
	                    e.setSize(grid.new Dimension(1, 1));//on lui donne la taille de la cellule
	                    e.setPosition(grid.new Position(x, y));//on la place a la position x y

	                    if (e instanceof PengoPlayer) {//si l'entite est le joueur on lenregistre comme joueur principal du jeu 
	                        model.setPlayer((PengoPlayer) e);
	                    } else {
	                        model.add(e);//snn on lajoute a liste des entite du model
	                    }
	                }
	            }
	        }
	    }
	    private static Entity createEntity(char c) {
	        switch (c) {
	            case '#':
	                return new Wall();

	            case 'P':
	                return new PengoPlayer();

	            case 'I':
	                return new IceBlock();

	            case 'G':
	                return new GoldBlock();

	            case 'D':
	                return new DiamondBlock();

	            case 'E':
	                return new Enemy();

	            case 'F':
	                return new FishBonus();

	            case '.':
	            case ' ':
	                return null;

	            default:
	                throw new IllegalArgumentException("Symbole inconnu dans la map : " + c);
	        }
	    }



}
