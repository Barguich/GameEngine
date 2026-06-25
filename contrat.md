# CONTRAT

* Équipe : 4
* Jeu : Pengo
* contact : Florian.Vautier@etu.univ-grenoble-alpes.fr
* version du 18/06 11h30


# 1. Point technique à expliquer durant la démo
Au choix
* [o] implémentation de la condition Closest
* [o] implémentation du Golden Block et du Diamond Block en GAL

# 2. Présentation du jeu

Nous proposons une adaptation du jeu **Pengo**.

Le joueur contrôle un pingouin dans un labyrinthe constitué de blocs de glace. Il doit éliminer les ennemis en poussant des blocs, en utilisant les murs, et en exploitant les bonus disponibles.

Le joueur doit éviter les ennemis, car un contact direct avec eux lui fait perdre une vie.

La partie est gagnée dans l'un des cas suivants :

- tous les ennemis sont éliminés ;
- trois Diamond Blocks sont alignés horizontalement ou verticalement.

## 2.2 Nos choix

### Extensions

- Bonus Gold Block qui gèle les ennemis.
- Fish Bonus qui multiplie la vitesse du joueur par 2.
- Diamond Blocks donnant une condition de victoire spéciale.
- Score différent selon le mode d'élimination de l'ennemi.
- IA basée sur des Bots Java et des scripts GAL.

## 2.3 Bonus ajoutés

### [x] Gold Block

Le Gold Block est un bloc spécial. Lorsqu'un ennemi le touche, il est gelé pendant quelques secondes sans être averti du piège.

Effets :

- immobilisation temporaire de l'ennemi ;
- changement visuel de l'ennemi ;
- désactivation temporaire de son Bot ;
- retour automatique à l'état normal après expiration du timer.

**Démo**
  - Un ennemi touche un Gold Block.
  - L'ennemi devient gelé.
  - Après quelques secondes, il redevient actif.

---

### [x] Fish Bonus

Le Fish Bonus est un bonus récupéré par le joueur.

Effets :

- la vitesse du joueur est multipliée par 2 ;
- l'effet est temporaire ;
- la vitesse normale est restaurée automatiquement à la fin du bonus.

**Démo**
  - Le joueur ramasse le Fish Bonus.
  - Il se déplace plus rapidement.
  - Après quelques secondes, il revient à sa vitesse normale.

### [x] Diamond Blocks

Les Diamond Blocks sont trois blocs spéciaux présents dans le niveau.

Effets :

- si les trois Diamond Blocks sont alignés horizontalement ou verticalement, le joueur gagne immédiatement ;
- cette condition de victoire donne un objectif supplémentaire au joueur.

**Démo**
  - Le joueur pousse les Diamond Blocks.
  - Les trois blocs deviennent alignés.
  - La partie affiche une victoire.

## 2.4 [?] Système de score *(optionnel)*

Le score dépend de la manière dont un ennemi est éliminé, ainsi que du nombre d'ennemis éliminés simultanément.


# 3. Difficultés techniques et solutions envisagées

## 3.1 [x/?] Déplacement des blocs de glace

### Difficulté

Les blocs de glace ne se déplacent pas comme le joueur. Lorsqu'un bloc est poussé, il doit continuer à glisser dans la même direction jusqu'à rencontrer un obstacle (mur ou autre bloc).

### Solution envisagée

Chaque bloc de glace possède un état `sliding`, une direction de déplacement et une vitesse.

Lorsqu'un joueur pousse un bloc, l'état `sliding` passe à vrai et la direction de poussée est mémorisée. À chaque appel de `tick`, le modèle calcule la prochaine case dans cette direction.

- Si la case est libre, le bloc continue son déplacement.
- Si la case contient un ennemi, celui-ci est déplacé avec le bloc.
- Si la case contient un mur ou un autre bloc, le déplacement s'arrête.
- Si un ennemi est coincé entre le bloc et un obstacle, il est éliminé.

Cette logique est implémentée dans `IceBlock` et `PengoModel`.
### [x/?] Démo

Le joueur pousse un bloc vers la droite : 
- [x] Le bloc continue à glisser jusqu'à toucher un autre bloc ou le bord de la carte.
- [?] Si le bloc rencontre un ennemi il l'emporte avec lui


## 3.2 [x] Destruction / fonte des blocs de glace

### Difficulté

Le joueur et certains ennemis peuvent faire fondre ou détruire des blocs de glace. Cette action modifie la structure du labyrinthe pendant la partie.

### Solution envisagée

Chaque bloc possède un état : intact, fissuré, détruit. À chaque frappe, son point de vie diminue de 1. Lorsqu'il atteint 0, il est retiré du modèle et de l'affichage. Ce comportement peut être décrit en GAL.

### [x] Démo

Le joueur détruit un bloc devant lui. Le bloc disparaît et un nouveau passage devient disponible.

---

## 3.3 Apparition dynamique des ennemis

### Difficulté

Certains blocs peuvent cacher un ennemi. Pendant la partie, un bloc peut donc se transformer en ennemi actif.

### Solution envisagée

Remplacer dynamiquement l'entité Bloc par une entité Ennemi via un automate GAL. L'ennemi reçoit ensuite son propre Bot, ses points de vie et son animation.

### Démo

Un bloc spécial disparaît et un ennemi apparaît à sa place.

---

## 3.4 [x] Gestion des bonus temporaires

### Difficulté

Les bonus ne doivent pas être permanents. Le Gold Block gèle un ennemi pendant un temps limité et le Fish Bonus augmente temporairement la vitesse du joueur.

### Solution envisagée

Associer un timer à chaque effet temporaire. À chaque tick, le temps restant diminue. Lorsque le timer arrive à zéro, l'effet est annulé.

### [x] Démo

Le joueur ramasse un Fish Bonus : Sa vitesse est doublée pendant quelques secondes, puis redevient normale.

---

## 3.5 [x] Intelligence artificielle des ennemis

### Difficulté

Les ennemis ne doivent pas seulement avancer au hasard. Ils doivent pouvoir patrouiller, poursuivre le joueur et, optionnellement, être gelés.

### Solution envisagée = [#MP : condition Closest]

Utiliser plusieurs comportements de Bot :

- Bot de patrouille ;
- Bot de poursuite ;
- Bot gelé (bloqué via une fonction `wait` en GAL) ;
- Bot mort / inactif.

Certains comportements sont décrits en GAL afin de pouvoir les modifier sans toucher au code Java.

La condition `Closest` recherche le joueur dans un rayon donné autour de l'ennemi. Lorsqu'elle est vérifiée, la direction du joueur est calculée puis utilisée dans une transition GAL afin de poursuivre le joueur.

Exemple :

```gal
Closest(@, d) ? Move(d)
```

Lorsque le joueur n'est plus détecté, les autres transitions reprennent le contrôle et l'ennemi revient à son comportement de patrouille.


### [x] Démo

Un ennemi patrouille, puis change de comportement lorsqu'il détecte le joueur.

---

## 3.6 Gestion des différentes morts des ennemis

### Difficulté

Un ennemi peut mourir de plusieurs façons :

- écrasé directement par un bloc de glace ;
- étourdi suite à une interaction avec le mur (le joueur peut alors l'éliminer).

### Solution envisagée

Lorsqu'une action de poussée est effectuée, le jeu vérifie :

- la trajectoire du bloc ;
- les ennemis situés autour du bloc ou du mur poussé ;
- les ennemis coincés entre deux obstacles.

Le score est attribué selon la règle correspondante.

### Démo

- Ennemi écrasé par un bloc : +100 points.
- Ennemi éliminé par poussée contre un mur / bloc proche : +400 points.

---

## 3.7 [x] Alignement des Diamond Blocks

### Difficulté

Le jeu doit détecter si les trois Diamond Blocks sont alignés horizontalement ou verticalement. Cette vérification doit être effectuée après chaque déplacement d'un Diamond Block.

### Solution envisagée

Après chaque déplacement d'un Diamond Block, le jeu [#MP: pas assez précis] récupère les positions des trois blocs et vérifie s'ils partagent la même ligne ou la même colonne. Cette logique peut être gérée via un automate GAL [#MP: à détailler]

```Haskell
DiamondBlock(Check){
  * (Check):
  | Step(N,D) && Step(S,D) ? Wiz : ()
  | ...

  * () 
}
```
- D étant interprété comme Diamond
- Wiz étant interprété comme une action de victoire par alignement des diamond blocks.

### [x] Démo

Le joueur pousse un Diamond Block. Les trois Diamond Blocks deviennent alignés. La partie se termine par une victoire.

---

## 3.8 [x] Modification dynamique du labyrinthe

### Difficulté

Le labyrinthe change pendant la partie : les blocs bougent, certains sont détruits et d'autres se transforment en ennemis. Les déplacements possibles changent donc constamment.

### Solution envisagée

Mettre à jour la représentation de la carte après chaque déplacement, destruction ou transformation d'un bloc.

### [x] Démo

Un bloc est détruit. Le joueur et les ennemis peuvent ensuite passer par l'ancienne position du bloc.

---

## 3.9 [?] Équilibrage du gameplay

### Difficulté

Le jeu doit rester jouable. Si les ennemis sont trop rapides ou si les bonus sont trop puissants, la partie devient déséquilibrée.

### Solution envisagée

Placer les paramètres importants dans le fichier de configuration :

- vitesse du joueur ;
- vitesse des ennemis ;
- durée du Fish Bonus ;
- durée du gel ;
- nombre d'ennemis ;
- score gagné selon l'action.

### Démo

Modifier la durée du Fish Bonus ou la vitesse des ennemis dans le fichier de configuration, puis relancer la partie.

---

# 4. [?] Conditions de victoire et de défaite

## Victoire

Le joueur gagne si :

- tous les ennemis sont éliminés ;
- ou les trois Diamond Blocks sont alignés.

## Défaite

Le joueur perd si :

- il n'a plus de vies ;
- un ennemi le touche alors qu'il n'a plus de protection.


# 3. Contrat concernant le moteur de jeu

## Partie Modèle

### Géométrie

- [x] **Monde torique**
  - Explications : Pas de monde torique pour ce jeu.
  - **Démo : Un essai avec le tore des blocs de glace qui sont cassables.**

- [ ] **Défilement / scrolling**

- [x] **Taille de la map modifiable**
  - Explications : Les niveaux sont stockés sous forme de fichiers ASCII. Chaque symbole possède une signification particulière (mur, glace, joueur, ennemi, bonus…). Lors du chargement, un parseur parcourt le fichier caractère par caractère et crée les entités correspondantes. La taille du monde dépend donc directement de la taille du fichier lu.
  - **Démo : Charger plusieurs fichiers de niveaux ayant des dimensions différentes et observer que le moteur adapte automatiquement la taille de la carte et la position des entités.**

- [x] **Bords du monde**
  - Explications : 
    * Les bords du monde constituent un obstacle, mais peuvent également être interactifs.  
    * Plusieurs types d'obstacles sont prévus : vibrant, cassable, non cassable, glissable…
  - **Démo : Envoyer un bloc ou tenter de faire traverser le joueur à travers un bord.**


### Génération de la map

- [ ] **Fixe**

- * [x] **Par configuration**

  * Explications :

    * Les niveaux sont décrits dans des fichiers texte ASCII.
    * Chaque caractère représente une entité du jeu (mur, bloc de glace, joueur, ennemi, bonus, etc.).
    * Lors du chargement, le fichier est parcouru caractère par caractère afin de construire dynamiquement le niveau.
    * Les dimensions de la carte ainsi que le contenu du niveau sont entièrement déterminés par le fichier de configuration.
  * **Démo :**

    * Modifier le fichier de niveau pour ajouter ou supprimer des entités.
    * Relancer le jeu et observer les modifications de la carte.

- [ ] **Aléatoire**

- [ ] **Aléatoire avec mémorisation**

### Interactions

- [ ] **Annulation d'une action**

- [x] **Interactions spécifiques**
  - Explications : Plusieurs interactions entre entités sont gérées :
    - Si ce sont deux ennemis, il n'y a pas de problème, ils peuvent se chevaucher.
    - Si c'est un ennemi et le joueur, il y a une collision.
    - Lancer un bloc vers un ennemi décale les deux.
    - Les ennemis et le joueur ne peuvent pas traverser un bloc intact. Lorsqu'un bloc est en cours de destruction, il est possible de le traverser mais avec une vitesse réduite.
  - **Démo : Traiter les 5 cas un par un**
    - deux ennemis peuvent se chevaucher.
    - un ennemi et le joueur entrent en collision.
    - Lancer un bloc vers un ennemi décale les deux.
    - Les ennemis et le joueur ne peuvent pas traverser un bloc intact. 
    - Lorsqu'un bloc est en cours de destruction, il est possible de le traverser mais avec une vitesse réduite.


### Affichage et animation

- [x] **Barre de santé / points de vie de chaque personnage**
  - Explications : Le joueur possède un nombre limité de vies. Les vies restantes sont affichées à l'écran.
  - **Démo : Un ennemi touche le joueur, le compteur de vies diminue.**

- [x] **Animation lors de la création / apparition**
  - Explications : Certaines entités apparaissent pendant la partie, par exemple un ennemi qui sort d'un bloc de glace.
  - **Démo : Un bloc se transforme en ennemi avec une animation d'apparition.**

- [x] **Animation lors de la destruction / disparition**
  - Explications : Lorsqu'un bloc est détruit ou lorsqu'un ennemi meurt, une animation de disparition est jouée. Le joueur tape sur le bloc, qui change d'état, puis disparaît en plusieurs étapes.
  - **Démo** : Le joueur pousse un bloc qui tue un ennemi, **l'ennemi disparaît avec une animation.**

- [x] **Animation en fonction de l'état du Bot**
  - Explications : L'animation du Bot peut changer selon la situation. Si un ennemi est proche du bord au moment où le joueur interagit avec ce bord, le bot passe en mode étourdi (pendant une durée limitée) avec une animation spécifique, sans lien avec son état habituel.
  - **Démo :** 
    - le joueur interagit avec le bord en présence d'un ennemi nearby.
    - l'ennemi nearby est étourdi
    - après un temps, l'ennemi nearby retrouve son état habituel.



### Adaptabilité = changement pendant le jeu

- [ ] **Changement de Stunt**

- [x] **Changement d'avatar**
  - Explications : L'image d'une entité peut changer selon son état, ce qui permet de montrer visuellement un bonus, un gel ou une destruction.
  - **Démo :** 
    -Les ennemis possèdent plusieurs apparences sélectionnées dynamiquement selon leur état :
    - état normal ;
    - état gelé ;
    - état passed out ;
    - état de destruction.
    La sélection du sprite est réalisée dans `EnemyAvatar`.
- [x] **Changement de Bot / FSM**
  - Explications : Les ennemis peuvent changer de comportement selon la situation : patrouille, poursuite, fuite, immobilisation. Leur agressivité augmente en fonction du nombre d'ennemis restants.
 Les ennemis utilisent un comportement de patrouille et de poursuite reposant sur la condition `Closest`. Lorsqu'un joueur est détecté à proximité, l'ennemi adapte sa direction pour se rapprocher de lui.
  - **Démo :** 
    - Un ennemi passe d'un comportement de patrouille à un comportement de poursuite lorsqu'il détecte le joueur.


## Partie Vue

- [x] **Viewport centré sur le joueur**
  - Explications : Dans un grand monde, la caméra suit le joueur en permanence et la vue est translatée en fonction des coordonnées du joueur et de la taille de la grille.
  - **Démo : déplacement du joueur dans une map de grande taille.**

* [?] elasticité   
  + Explications: _le joueur peut se déplacer dans une zone autour du centre sans déclencher le déplacement du viewport_

- [ ] **Zoom**

- [ ] **Scrolling**


## Mode debug

- [x] Affichage du temps entre deux *tick* (min, max, moyenne)
- [x] Affichage du temps entre deux *paint* (min, max, moyenne)
- [x] Affichage du nombre de *FPS* (min, max, moyenne)
- [x] Gestion de la charge (nombre maximum d'entités actives acceptées)
    + **Démo : création d'un bot spawner avec l'automate (1)--EGG-> (1) qui génère sans fin de nouvelles entités. Ce qui permet de voir à partir de combien d'entités le moteur tombe à < 24 fps.**

- [x] Affichage de l'action au-dessus du personnage
- [x] Affichage des bounding boxes (pour tester les collisions)

## Partie Contrôleur

### Bot

- [x] **Bot en Java**
  - Explications : Certains comportements simples restent programmés directement en Java, notamment les comportements utilitaires liés au déplacement, à l'arrêt temporaire, ou aux états particuliers d'un ennemi.
  - Démo : Un ennemi peut être immobilisé lorsqu'il est gelé ou lorsqu'il passe dans l'état `passedOut`.
  
- [ ] **Bot en FSM Java**

[x] **Bot en GAL + parser**
  - Explications : Certains comportements des ennemis sont décrits dans des fichiers `.gal`.  
    Le parser GAL charge ces fichiers au lancement du jeu et construit les automates correspondants.  
    Une transition GAL peut par exemple utiliser une condition de détection du joueur, comme `Closest`, puis déclencher une action de déplacement.
  - Démo : Modifier une transition dans un fichier `.gal`, relancer le jeu, puis observer que le comportement de l'ennemi change sans modifier les classes Java.
  
### Démo : modification du comportement des entités

- [ ] **En changeant de classe Bot**
  - Explications : —
  - Démo : —

- [ ] **En changeant de FSM Java**
  - Explications : —
  - Démo : —

- [x] **En changeant le fichier `.gal`**
  - Explications : Le comportement d'un ennemi peut être modifié en éditant le fichier GAL associé.
  - Démo : Modifier une transition dans le fichier GAL et relancer le jeu.


## Partie Controlleur

* [o] **Une action à la fois pour le joueur**

  * Explications :

    * À chaque tick, le contrôleur lit les entrées clavier et produit au plus une action prioritaire.
    * Les actions de déplacement utilisent les touches directionnelles tandis que l'action de frappe utilise une touche dédiée.
    * Si plusieurs entrées sont détectées simultanément, le contrôleur applique une règle de priorité afin de ne transmettre qu'une seule action au modèle.
    * Cette approche évite les conflits d'exécution et garantit un comportement déterministe du joueur.
  * **Démo :** appuyer simultanément sur plusieurs touches et montrer qu'une seule action est exécutée pendant le tick courant.

+ [o] **actions multiples indépendantes pour le joueur**
  + Explications: déplacement + action de frapper 
  + **Démo: déplacement et frappe simultanées**

+ [?] gestion des actions multiples compatibles/incompatibles

## Partie Bot

### action unique versus actions multiples simultanées

- [x] **Une action à la fois**
  + **Démo : une action à la fois pour les bots**

- [ ] **Actions multiples**

- [ ] **Gestion des actions compatibles / incompatibles**
