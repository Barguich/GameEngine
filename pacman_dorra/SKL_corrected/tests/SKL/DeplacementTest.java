package SKL;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import behavior.AttackBot;
import behavior.BasicStunt;
import behavior.FleeBot;
import engine.Entity;
import engine.Grid.Position;
import entities.Boss;
import entities.Ghost;
import entities.PacMan;
import engine.ISU;
import game.Game;
import game.Model;

public class DeplacementTest {
	// move east
	@Test
	void pacmanMoveEast() {
		Game game = new Game(10, 10);
		Position pc = game.grid().new Position(2, 2);
		Entity pacman = new PacMan(pc);
		ISU.Coord before = pacman.center().mkCopy();
		pacman.moveEast(3.7);
		assertEquals(before.x() + 3.7, pacman.center().x(), 0.001);
		assertEquals(before.y(), pacman.center().y(), 0.001);
	}

	// avec tore
	@Test
	void pacmanTore() {
		Game game = new Game(10, 10);
		Position pc = game.grid().new Position(9, 5);
		Entity pacman = new PacMan(pc);
		pacman.moveEast(3.7);
		assertEquals(0, pacman.position().x());
		assertEquals(5, pacman.position().y());
	}

	@Test
	void pacmanToreretour() {
		Game game = new Game(10, 10);
		Position p = game.grid().new Position(0, 5);
		Entity pacman = new PacMan(p);
		pacman.moveWest(3.7);
		assertEquals(9, pacman.position().x());
		assertEquals(5, pacman.position().y());
	}

	// Test bounding
	@Test
	void Boundingmovesavce() {
		Game game = new Game(10, 10);
		Position p = game.grid().new Position(2, 2);
		Entity pacman = new PacMan(p);
		pacman.setBounding();
		ISU.Coord before = pacman.center().mkCopy();
		pacman.moveEast(3.7);
		pacman.setBounding();
		assertEquals(before.x() + 3.7, pacman.center().x(), 0.001);
	}

	// Test 2 enite pas a la meme position ne sintersectent pas
	@Test
	void PacmanGhostFar() {
		Game game = new Game(10, 10);
		Position pc = game.grid().new Position(1, 1);
		Entity pacman = new PacMan(pc);
		Position pg = game.grid().new Position(8, 8);
		Ghost ghost = new Ghost(pg);
		assertFalse(pacman.intersects(ghost));
	}

	// test intersection
	@Test
	void PacmanGhostCollide() {
		Game game = new Game(10, 10);
		Position pc = game.grid().new Position(4, 1);
		Entity pacman = new PacMan(pc);
		Position pg = game.grid().new Position(4, 1);
		Ghost ghost = new Ghost(pg);
		assertTrue(pacman.intersects(ghost));

	}

	// intersection + deplacement
	@Test
	void IntersectionApresMove() {
		Game game = new Game(10, 10);
		Position pc = game.grid().new Position(2, 2);
		Entity pacman = new PacMan(pc);
		Position pg = game.grid().new Position(3, 2);
		Ghost ghost = new Ghost(pg);
		assertFalse(pacman.intersects(ghost));
		pacman.moveEast(3.7);
		assertTrue(pacman.intersects(ghost));
	}

	// tenant compte du tore
	@Test
	void IntersectionApresMoveTore() {
		Game game = new Game(10, 10);
		Position pc = game.grid().new Position(9, 2);
		Entity pacman = new PacMan(pc);
		Position pg = game.grid().new Position(0, 2);
		Ghost ghost = new Ghost(pg);
		assertFalse(pacman.intersects(ghost));
		pacman.moveEast(3.7);
		assertTrue(pacman.intersects(ghost));
	}

	// verifier el turn du tore
	@Test
	void ghostTurn() {
		Game game = new Game(10, 10);
		Ghost ghost = new Ghost(game.grid().new Position(4, 4));
		ghost.turn(90);
		assertEquals(90, ghost.orientation());
		assertNotNull(ghost.bounding());
	}

	// verification des bordes avec tore/interscetion
	@Test
	void largeEntityIntersectsAcrossTorusX() {
		Game game = new Game(10, 10);
		Boss boss = new Boss(game.grid().new Position(9, 5));
		Entity pacman = new PacMan(game.grid().new Position(0, 5));
		assertTrue(boss.intersects(pacman));
	}

	// verification dimension tore+ intersection avec coins
	@Test
	void largeEntity() {
		Game game = new Game(10, 10);
		Boss boss = new Boss(game.grid().new Position(9, 9));
		Entity pacman = new PacMan(game.grid().new Position(0, 9));
		assertTrue(boss.intersects(pacman));
	}

	@Test
	void attackBotMovesTowardPacman() {

		Game game = new Game(20, 15);
		Model model = new Model(game);

		PacMan pac = new PacMan(game.grid().new Position(1, 1));
		Ghost ghost = new Ghost(game.grid().new Position(5, 1));

		model.add(pac);
		model.add(ghost);

		BasicStunt ghostStunt = new BasicStunt(model, ghost);
		AttackBot ghostBot = new AttackBot(ghostStunt, pac);

		ghost.setStunt(ghostStunt);
		ghost.setBot(ghostBot);

		model.tick(100);

		assertEquals(4, ghost.position().x());
	}

	@Test
	void modelAddEntity() {
		Game game = new Game(10, 10);
		Model model = new Model(game);

		PacMan pacman = new PacMan(game.grid().new Position(2, 2));

		model.add(pacman);

		assertTrue(model.entities().contains(pacman));
		assertTrue(game.grid().cellAt(pacman.position()).contains(pacman));
		assertSame(model, pacman.model());
	}

	@Test
	void modelRemoveEntity() {
		Game game = new Game(10, 10);
		Model model = new Model(game);

		PacMan pacman = new PacMan(game.grid().new Position(2, 2));

		model.add(pacman);
		model.remove(pacman);

		assertFalse(model.entities().contains(pacman));
		assertFalse(game.grid().cellAt(pacman.position()).contains(pacman));
	}

	@Test
	void fleeBotMovesAwayFromPacman() {
		Game game = new Game(20, 15);
		Model model = new Model(game);

		PacMan pac = new PacMan(game.grid().new Position(1, 1));
		Ghost ghost = new Ghost(game.grid().new Position(5, 1));

		model.add(pac);
		model.add(ghost);

		BasicStunt ghostStunt = new BasicStunt(model, ghost);
		FleeBot ghostBot = new FleeBot(ghostStunt, pac);

		ghost.setStunt(ghostStunt);
		ghost.setBot(ghostBot);

		model.tick(100);

		assertEquals(6, ghost.position().x());
	}

	@Test
	void modelTickDetectsCollision() {
		Game game = new Game(20, 15);
		Model model = new Model(game);

		PacMan pac = new PacMan(game.grid().new Position(5, 5));
		Ghost ghost = new Ghost(game.grid().new Position(5, 5));

		model.add(pac);
		model.add(ghost);

		assertTrue(pac.intersects(ghost));
	}
}
