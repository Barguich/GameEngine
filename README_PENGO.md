# Projet Pengo

Ce projet implémente une version du jeu **Pengo** en Java, avec un moteur générique, un modèle de jeu, une vue avec viewport, des sprites, des collisions et des automates/bots GAL pour les ennemis.

## 1. Prérequis

- Eclipse ou un IDE Java équivalent.
- Le projet doit être ouvert avec le dossier `ws` comme projet Java. Le code source est dans `ws/Pengo`, mais la racine du projet Eclipse reste `ws`.
- Les ressources doivent rester dans le dossier :

```text
Asset/
```

Ce dossier contient les images, les cartes et les fichiers de configuration.

## 2. Classe principale

La classe à exécuter est :

```text
pengo.PengoMain
```

Fichier correspondant :

```text
ws/Pengo/game/pengo/PengoMain.java
```

C'est cette classe qui crée le jeu, initialise le modèle, la vue, le viewport, les contrôleurs, les bots et charge les cartes.

## 3. Compilation

### Avec Eclipse

1. Importer le projet Java dans Eclipse.
2. Vérifier que le JDK utilisé est bien .
3. Vérifier que le dossier `Asset/` est bien à la racine du projet d'exécution.
4. Lancer une compilation normale avec :

```text
Project > Clean
```

puis :

```text
Project > Build Project
```


## 4. Exécution

### Avec Eclipse

1. Ouvrir `PengoMain.java`.
2. Faire clic droit sur le fichier.
3. Choisir :

```text
Run As > Java Application
```

La fenêtre du jeu s'ouvre ensuite automatiquement.



## 5. Choix de la carte

Au lancement, le jeu affiche un menu de sélection de carte.

Cartes disponibles :

```text
Classic
Torus
Big ViewPort
```

Contrôles du menu :

| Touche        | Action                   |
|-------------- |--------------------------|
| Flèche haut/Z | Monter dans le menu      |
| Flèche bas /S | Descendre dans le menu   |
| Entrée        | Valider la carte choisie |


Après validation, la carte sélectionnée est chargée depuis le dossier :

```text
Asset/rsrc/maps/
```

## 6. Contrôles pendant le jeu

| Touche            | Action                     |
|-------------------|----------------------------|
| Flèche haut / Z   | Aller vers le haut         |
| Flèche bas / S    | Aller vers le bas          |
| Flèche gauche / Q | Aller vers la gauche       |
| Flèche droite / D | Aller vers la droite       |
| Espace            |casser le bloc devant Pengo |
| Échap             | Ouvrir le menu pause       |


## 7. Menu pause / victoire / game over

Pendant le jeu, appuyer sur :

```text
Échap
```

ouvre le menu.

Options possibles :

```text
Reprendre
Recommencer
Quitter
```

Contrôles du menu :

| Touche          | Action                  |
|-----------------|-------------------------|
| Flèche haut / Z | Monter                  |
| Flèche bas / S  | Descendre               |
| Entrée / Espace | Valider                 |
| Q               | Quitter                 |
| R               | Recommencer directement |

## 8. Objectif du jeu

Le joueur contrôle Pengo. Le but est de survivre, d'éliminer les ennemis et d'utiliser les blocs de glace.

Fonctionnalités principales :

- Pousser des blocs de glace.
- Écraser les ennemis avec un bloc glissant.
- Casser les blocs abîmés.
- Traverser un bloc très abîmé avec ralentissement.
- Utiliser les bonus.
- Gagner avec l'alignement des DiamondBlocks ou si tous les ennemies sont mort 

## 9. Organisation générale du projet

```text
engine/      moteur générique du jeu
geometry/    grille, coordonnées et vecteurs
collision/   formes et tests de collision
model/       entités et modèle générique
gal/         actions, conditions et automates GAL
pengo/       logique spécifique au jeu Pengo
view/        affichage, viewport, avatars et overlays
Asset/       cartes, sprites et configuration
```

## 10. Remarque importante

Le moteur reste générique : il ne connaît pas les règles spécifiques de Pengo. La logique du jeu Pengo est placée dans le package :

```text
pengo
```

Cela permet de séparer clairement le moteur général, le modèle du jeu, les collisions, les automates et l'affichage.
