package pengo.controller;

import engine.Game;
import gal_engine.Bot;
import model.Entity;
import oop.graphics.Canvas;
import oop.graphics.VirtualKeyCodes;
import pengo.brain.PlayerBot;
import pengo.model.DiamondBlock;
import pengo.model.IceBlock;
import pengo.model.PengoModel;
import pengo.model.PengoPlayer;
import view.MenuOverlay;
import view.View;

public class PengoController implements Canvas.KeyListener {

	private static final String RESUME = "Reprendre";
	private static final String RESTART = "Recommencer";
	private static final String QUIT = "Quitter";

	// Entrées affichées dans le menu de choix de map.
	private static final String MAP_CLASSIC = "Classic";
	private static final String MAP_TORUS = "Torus";
	private static final String MAP_BIG = "Big ViewPort";

	private final PengoModel model;
	private final View view;

	public PengoController(PengoModel model, View view) {
		this.model = model;
		this.view = view;

		// Le menu est reconstruit dès que l'état du modèle change
		// pause, victoire, défaite ou choix de map.
		model.setStateListener(state -> refreshMenu());

		refreshMenu();
	}

	// Récupère le bot joueur associé à Pengo.
	// Le contrôleur ne déplace pas directement le joueur : il transmet
	// les intentions clavier au PlayerBot.
	private PlayerBot playerBot() {
		PengoPlayer p = model.player();

		if (p == null) {
			return null;
		}

		Bot b = p.bot();

		if (b instanceof PlayerBot) {
			return (PlayerBot) b;
		}

		return null;
	}

	@Override
	public void pressed(Canvas canvas, int keyCode, char keyChar) {

		// Échap permet d'ouvrir ou fermer le menu de pause.
		if (keyCode == VirtualKeyCodes.VK_ESCAPE) {
			model.togglePause();
			refreshMenu();
			return;
		}

		// Si un menu est affiché, les touches servent à naviguer dedans.
		if (model.menuVisible()) {
			handleMenuKey(keyCode);
			return;
		}

		// Aucune action de jeu si la partie n'est pas en cours.
		if (!model.running()) {
			return;
		}

		// Les directions sont envoyées au PlayerBot, qui gère ensuite
		// le déplacement case par case.
		int dir = directionFor(keyCode);

		if (dir >= 0) {
			PengoPlayer p = model.player();

			if (p != null) {
				p.turnTo(dir);
			}

			PlayerBot bot = playerBot();

			if (bot != null) {
				bot.pressDirection(dir);
			}

			return;
		}

		switch (keyCode) {
		case VirtualKeyCodes.VK_SPACE:
			PengoPlayer p = model.player();

			if (p != null) {
				damageBlockInFront(p);
			}
			break;

		case VirtualKeyCodes.VK_R:
			// Reset complet de la partie courante.
			model.reset();
			refreshMenu();
			break;

		default:
			break;
		}
	}

	@Override
	public void released(Canvas canvas, int keyCode, char keyChar) {
		int dir = directionFor(keyCode);

		if (dir < 0) {
			return;
		}

		// Le relâchement de la touche est aussi transmis au PlayerBot
		// pour qu'il mette à jour son buffer d'entrées.
		PlayerBot bot = playerBot();

		if (bot != null) {
			bot.releaseDirection(dir);
		}
	}

	@Override
	public void typed(Canvas canvas, char keyChar) {
	}

	// Convertit une touche clavier en direction du moteur.
	// Convention : 0 = droite, 90 = bas, 180 = gauche, 270 = haut.
	private int directionFor(int keyCode) {
		switch (keyCode) {
		case VirtualKeyCodes.VK_UP:
		case VirtualKeyCodes.VK_Z:
			return 270;

		case VirtualKeyCodes.VK_DOWN:
		case VirtualKeyCodes.VK_S:
			return 90;

		case VirtualKeyCodes.VK_LEFT:
		case VirtualKeyCodes.VK_Q:
			return 180;

		case VirtualKeyCodes.VK_RIGHT:
		case VirtualKeyCodes.VK_D:
			return 0;

		default:
			return -1;
		}
	}

	// Reconstruit le contenu du menu selon l'état actuel du modèle.
	private void refreshMenu() {
		String title;
		String[] items;

		switch (model.state()) {
		case PAUSED:
			title = "PAUSE";
			items = new String[] { RESUME, RESTART, QUIT };
			break;

		case WON:
			title = "VICTOIRE !";
			items = new String[] { RESTART, QUIT };
			break;

		case GAME_OVER:
			title = "GAME OVER";
			items = new String[] { RESTART, QUIT };
			break;

		case CHOIX_MAP:
			title = "CHOISIR UNE MAP";
			items = new String[] { MAP_CLASSIC, MAP_TORUS, MAP_BIG };
			break;

		default:
			return;
		}

		view.menu().set(title, items, 0);
	}

	// Gère les touches lorsqu'un menu est affiché.
	private void handleMenuKey(int keyCode) {
		MenuOverlay menu = view.menu();

		switch (keyCode) {
		case VirtualKeyCodes.VK_UP:
		case VirtualKeyCodes.VK_Z:
			menu.move(-1);
			break;

		case VirtualKeyCodes.VK_DOWN:
		case VirtualKeyCodes.VK_S:
			menu.move(1);
			break;

		case VirtualKeyCodes.VK_ENTER:
		case VirtualKeyCodes.VK_SPACE:
			activateMenuItem(menu.items()[menu.selected()]);
			break;

		case VirtualKeyCodes.VK_Q:
			System.exit(0);
			break;

		case VirtualKeyCodes.VK_R:
			model.reset();
			refreshMenu();
			break;

		default:
			break;
		}
	}

	// Applique l'action associée à l'entrée sélectionnée dans le menu.
	private void activateMenuItem(String item) {
		switch (item) {
		case RESUME:
			model.resume();
			break;

		case RESTART:
			model.reset();
			refreshMenu();
			break;

		case QUIT:
			System.exit(0);
			break;

		case MAP_CLASSIC:
			model.chooseMap("Asset/rsrc/maps/pengo_classic.txt");
			break;

		case MAP_TORUS:
			model.chooseMap("Asset/rsrc/maps/pengo_torus.txt");
			break;

		case MAP_BIG:
			model.chooseMap("Asset/rsrc/maps/pengo_big_viewport.txt");
			break;

		default:
			break;
		}
	}

	// Attaque le bloc situé juste devant Pengo.
	// Les DiamondBlocks sont exclus car ils servent à la condition de victoire.
	private void damageBlockInFront(PengoPlayer player) {
		if (player == null || player.position() == null) {
			return;
		}

		int x = player.position().x();
		int y = player.position().y();

		switch (player.orientation()) {
		case 0:
			x++;
			break;

		case 90:
			y++;
			break;

		case 180:
			x--;
			break;

		case 270:
			y--;
			break;

		default:
			return;
		}

		Entity e = model.firstAt(Game.grid().new Position(x, y));

		if (e instanceof IceBlock && !(e instanceof DiamondBlock)) {
			((IceBlock) e).damage();
		}
	}
}