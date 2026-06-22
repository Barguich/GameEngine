package view;

import model.Entity;

public interface EntityRenderHook {

	/* Decalage horizontal */
	int offsetX(Entity e);

	/* Decalage vertical */
	int offsetY(Entity e);

}
