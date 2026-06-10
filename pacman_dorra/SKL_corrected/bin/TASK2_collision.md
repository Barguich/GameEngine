# TASK Détection de collisions

Pour tester la collision de deux entités, qui peuvent avoir des formes compliquées, 
on recouvre les entités par une union de formes géométriques simples, `Rect` et `Circle`,
ce qui permet de tester efficacement l'intersection, en considérant ces formes engloblantes (__Bounding Shapes__).

# `Bounding Shape`

On souhaite englober la forme d'une entité dans une _union de cercles et de rectangles_.

> Par exemple, 
  * Pac Man est englobé dans un cercle, 
  * le Boss est recouvert par l'union de deux rectangles,
  * Un fantome est englobé par l'union d'un rectange et d'un cercle.

## `Shape`

#### `Circle` 

Un cercle est caractérisée par son centre et son rayon.

#### `Rect`

Une rectangle qui peut être tourné est caractérisé par ses deux coins diagonalement opposés,
à partir desquels on peut en déduire
    * son centre de rotation
    * sa largeur, sa hauteur
    * son angle de rotation autour de son centre par rapport à l'axe des _x_

### interface `iShape` 

Tout objet de type `iShape` est capable de déterminer si 
* il intersecte un cercle donnée en paramètre
* il intersecte un rectangle donnée en paramètre
* il intersecte un `iShape` donnée en paramètre

```Java
 interface iShape {
   boolean intersects(iShape shape);
   boolean intersects(Circle circle);
   boolean intersects(Rect rect);
}
```


## `Bounding`

Le `Bounding` d'une entité est une collection de `Rect` et `Circle` qui sont placées de manière à recouvrir l'entité.
Ces formes doivent s'adapter 
* à la taille de l'entité 
* à la position du centre du l'entité
* à l'orientation de l'entité, ce qui nécessite alors de gérer les rotations.

Une entité doit fournir une méthode `boolean intersects(Entity e)` qui teste l'intersection de son `Bounding` avec celui de l'entité passé en paramètre.


#### To Do

Avant de vous lancer dans l'algorithmique, 
   
* Posez vous les __QUESTIONS__
  * Quelle une hiérachie de classes et d'interfaces ?
  * Comment construire les `shape` qui recouvrent une entité ? 
* Essayez de créer les `Bounding` de Pac Man, d'un fantôme, du Boss
* Faîtes validez votre proposition par un enseignant
* Écrivez complétement `Bounding` avant les autres classes.
* Modifiez `Entity` et ses spécialisation pour leur ajouter leur `Bounding`
* Passez ensuite à l'algorithmique en commençant par l'intersection cercle-cercle. 

#### BOUNDING
```Java
 class Bounding {
  // FIELDS
  Set<iShape> boundings;
  // CONSTRUCTOR
   Bounding() {}
  // BUILDER
   void add(iShape shape) {}
  // INTERSECTION
   boolean intersects(iShape shape) {}
   boolean intersects(Bounding bounding) {}
}
```


# Algorithmes de géométrie algébrique 

On considère deux sortes de _shape_ (`Rect` et `Circle`) ; il faut donc résoudre trois cas :

#### cas 1. Intersection cercle - cercle : facile

#### cas 2,3. Intersection rectange - _shape_

Le problème se simplifie quand le rectangle est aligné avec les axes _x,y_.
Donc la solution consiste à _se ramener à un repère centré sur le rectangle et aligné sur les axes du rectangle_ : 
* Virtuellement, on translate le rectangle en (0,0) et on le fait tourner de `-rect.angle` 
* On applique la même translation et la même rotation à l'autre _shape_.
* On détermine s'il y a intersection dans le cas idéalisé d'un rectangle aligné avec les axes _x,y_.

##### cas 2. Intersection rectangle - cercle

> Faîtes des dessins pour mieux comprendre les explications

Principe
1. on translate le centre du cercle vers le repère formé par les axes du rectangle,
2. on redresse le repère du rectangle en annulant la rotation du rectangle (on entraine le cercle dans cette rotation)
3. on détermine le point <i>P</i> du rectangle le plus proche du centre <i>C</i> du cercle
   de façon efficace car le rectangle est aligné sur les axes X,Y.
4. on conclut qu'il y a intersection si distance(P,C) < rayon du cercle.

##### cas 3. Intersection rectangle - rectangle 

L'algorithme le plus général s'appuie sur le _Separation Axis Theorem_ qui dit que : « deux rectangles n'ont pas d'intersection si on peut construire une droite qui les sépare. 

Sous certaines hypothèses sur le jeu, 
* déplacements continus (pas d'apparition spontanée)
* pas de simulation
* taille minimale d'une boîte
on peut simplifier l'algorithme et tester l'inclusion des coins dans un rectangle
mais il faut s'assurer que les hypothèses sont satisfaites, sinon revenir à l'algorithme général _S.A.T_. 


### Application au cas du Tore

__Mise en garde__ : Si un forme à une taille qui dépasse le (plus petit des) demi-périmètres du Tore, le problème de l'intersection devient mathématiquement très compliqué.

En revanche si toutes les entités ont des tailles qui tiennent dans un quart de Tore alors l'intersection sur un Tore peut se traiter comme l'intersection dans un plan euclidien _mais_ en utilisant la normalisation des coordonnées et la distance du Tore.

On choisit de se placer dans le cas où les dimensions des _shape_ qui recouvrent l'entité respectent la contrainte 
d'être plus petite que le plus petit des demi-périmètres du Tore.

##### To Do
* Traduisez cette contrainte sous forme d'assertions. Où les placer ?
* Complétez la classe `Circle`
* Mettez en place le test de collision entre entités en utilisant uniquement des `Circle`
* Écrivez des tests. Testez.
* Faîtes valider par un enseignant

#### Circle
```Java
 class Circle  {
   double radius;
  // CONSTRUCTOR
   Circle(ISU.Coord center, double radius) {}
  // INTERSECTION
   boolean intersects(Rect rect) {}
   boolean intersects(Circle circle) {}
   boolean intersects(iShape shape) {}
}
```


##### To Do

* Complétez la classe `Rect`
* Mettez en place le test de collision entre entités en utilisant des `Circle` et des `Rect`
* Écrivez des tests. Testez.
* Faîtes valider par un enseignant

#### Rect
```Java
 class Rect  {
  // FIELDS
   double halfWidth, halfHeight;
   int angle_degree;
  // CONSTRUCTOR
   Rect(ISU.Coord center, ISU.Dimension size, int angle_degree) {}
  // TRANSLATION ?
  // ROTATION
   void rotate(int angle_degree) {}
```
##### INTERSECTION
```Java
   boolean intersects(iShape shape) {}
```
###### Rect/Circle Intersection
```Java
   boolean intersects(Circle circle) {}
```
###### Helping inner class
```Java
   class RectCircleIntersection {
    // FIELDS
     Rect outer;
     Circle circle;
    // CONSTRUCTOR
     RectCircleIntersection(Rect outer, Circle circle) {}
    // REMEDY means `set right an undesirable situation`
    void remedy() {}
    // INTERSECTION in the easy case
    boolean intersects() {}
     ISU.Coord closestRectpoint() {}
     double clamp(double p, double l, double r) {}
  }
```
###### Rect/Rect Intersection
```Java
   boolean intersects(Rect rect) {}
```
###### Helping inner class
```Java
   class RectRectIntersection {
  }
}
```


##### To Do

* Complétez la classe `Entity` pour lui ajouter la notion de `Bounding`
* Écrivez des tests. Testez.
* Faîtes valider par un enseignant

##### ENTITY
```Java
 class Entity {
	// FIELDS
	 Grid grid;
	 ISU isu;
	 String name;
	// FIELDS
	 ISU.Dimension size; // dimension de l'entité
	 ISU.Dimension step; // dimension d'un pas de déplacement
	 Grid.Position position; // position en ncell dans la grille
	 ISU.Coord center; // coordonnées en cm du centre de l'entité
	// FIELDS
	 int orientation_degree; // orientation par rapport à l'axe des x
	// CONSTRUCTOR
	 Entity(String name) {}
	// SETTER
	 void setPosition(Grid.Position position) {}
	 void setCoord(ISU.Coord center) {}
	 void setSize(Grid.Dimension dimension) {}
	 void setSize(ISU.Dimension dimension) {}
	// GETTER
	 ISU.Coord center() {}
	 Grid.Position position() {}
	 int orientation() {}
	// TRANSLATION
	 void translate(Grid.Vector v) {}
	 void translate(ISU.Vector v) {}
	// TURN
	 void turn(int angle_degree) {}
	// SHOW
	void show(PrintStream ps) {}
```
###### MOVE
```Java
	 void moveNorth(int nStep) {}
	 void moveSouth(int nStep) {}
	 void moveEast(double length_cm) {}
	 void moveWest(double length_cm) {}
```
###### Task COLLISION
```Java
	// INTERSECTION
	 Bounding bounding;
	 void setBounding();
	boolean intersects(Entity e) {}
	 double distanceCenterToCenter(Entity e) {}
	// DEPLOY in the Grid according to the BOUNDING
	 Set<Cell> occupied;
	 void deploy() {}
	 void occupy(Grid.Position position) {}
	 void retract() {}
}
```




# Premières entités spécialisées

Selon sa taille une entité peut occuper 1 ou plusieurs cellules.
Pour mettre au point le moteur on considère plusieurs cas :
* des obstacles qui occupent entièrement une cellule
* des _gums_ nettement plus petites que la cellule
* _pac man_ et les fantômes (_ghosts_) qui occupent une cellule mais qui ont une taille en cm d'une demi-cellule.
* On ajoute le cas d'un _boss_ qui occupe plusieurs cellules

#### `Obstacle`

* Un `Obstacle` occupe une cellule
* sa taille est exactement celle de la cellule.
* sa `Bounding.Rect` recouvre exactement la cellule. 

```Java
 class Obstacle  {
  // CONSTRUCTOR
   Obstacle(int x_ncell, int y_ncell) {}
```
###### Task COLLISION
```Java
   void setBounding() {}
}
```

#### `Gum`

* Une `Gum` occupe une cellule
* sa taille en cm est une fraction de la celulle
* elle est placée au centre de la cellule

##### GUM
```Java
 class Gum  {
  // CONSTRUCTOR
   Gum() {}
```
###### Task COLLISION
```Java
   void setBounding() {}
}
```

#### `Ghost`

* Un `Ghost` occupe une cellule 
* sa taille en cm est d'une demi-cellule

##### GHOST
```Java
 class Ghost  {
  // CONSTRUCTOR
   Ghost() {}
```
###### Task COLLISION
```Java
   void setBounding() {}
}
```

#### `PacMan`

* `PacMan` occupe une cellule
* sa taille en cm est d'une demi-cellule

##### PAC MAN
```Java
 class PacMan  {
  // CONSTRUCTOR
   PacMan() {}
```
###### Task COLLISION
```Java
   void setBounding() {}
}
```

#### `Boss`

* Le Boss a la forme d'un «t» qui occupe entièrement 6 cellules
  * la cellule contenant son centre, 1 cellule au dessus de son centre, 2 au dessous de son centre, et 2 cellules à droite de son centre.
  * Cette forme a été choisie pour pouvoir tester les rotations.

#### BOSS
```Java
 class Boss  {
  // CONSTRUCTOR
   Boss() {}
```
###### Task COLLISION
```Java
   void setBounding() {}
}
```

#### To Do
* __QUESTIONS__
  * qui installe une entité dans la/les cellules du modèle ?
    * le modèle dispose d'une factory d'entité ?
    * chaque entité connaît le modèle ?
  * quand un entité se déplace, qui met à jour le modèle et la position de l'entité pour qu'elles restent cohérentes et synchronisées ?

* Complétez les classes `PacMan`, `Ghost`, `Obstacle`, `Boss` pour qu'elles construisent leur `bounding`.



# `Model` 

Le `Model` contient (au moins)
* la grille
* la liste d'entités


#### To Do
* Complétez la classe `Model`
* Placez des `Obstacle`, des `Gum`, des `Ghost`, `PacMan` et un `Boss` dans le modèle.
* On rappelle que
   * Une cellule doit pouvoir contenir plusieurs entités
   * Chaque cellule sait quelle(s) entité(s) l'occupent
   * Chaque entité sait quelle(s) cellule(s) elle occupe
   * Chaque entité sait dans quelle cellule est son centre
* Écrivez des tests. Testez.
* Validez avec un enseignant.

#### MODEL
```Java
 class Model {
  // FIELDS
   Grid grid;
   List<Entity> entities;
  // CONSTRUCTOR
  Model() {}
  // ADD, REMOVE Entity
   void add(Entity e) {}
   void remove(Entity e) {}
}
```
