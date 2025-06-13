# 🎮 Bomberman JavaFX

Un jeu Bomberman moderne développé en JavaFX avec plusieurs modes de jeu, un système de thèmes personnalisables et un éditeur de niveaux intégré.

## 📋 Table des matières

- Aperçu du projet
- Fonctionnalités
- Installation
- Modes de jeu
- Contrôles
- Système de thèmes
- Éditeur de niveaux
- Architecture technique
- Contributions

## 🎯 Aperçu du projet

Ce projet est une implémentation moderne du jeu classique Bomberman, développé en JavaFX avec une architecture modulaire et des fonctionnalités avancées. Le jeu propose plusieurs modes de jeu, un système de thèmes personnalisables et un éditeur de niveaux pour créer ses propres maps.

## ✨ Fonctionnalités

### 🎮 Modes de jeu
- **Robot Survivor** : Mode solo contre des bots IA
- **Legend 1v1** : Mode compétitif pour 2 joueurs humains
- **Maps personnalisées** : Jouez sur vos propres créations

### 🎨 Système de thèmes
- **4 thèmes intégrés** : Classic, Legend, Retro, Futuristic
- **Thèmes personnalisables** : Créez vos propres thèmes
- **Sprites adaptatifs** : Chaque thème modifie l'apparence complète du jeu

### 🛠️ Éditeur de niveaux
- **Interface intuitive** : Créez facilement vos maps
- **Système de sauvegarde** : Gardez vos créations
- **Validation automatique** : Zones de spawn protégées

### 🎵 Système audio
- **Musiques thématiques** : Différentes pour chaque mode
- **Effets sonores** : Explosions, victoire, défaite
- **Contrôle du volume** : Activation/désactivation

### ⚙️ Fonctionnalités techniques
- **Menu de pause** : Avec reprendre, recommencer, quitter
- **Système de score** : Points pour destructions et victoires
- **Power-ups** : Vie, portée, vitesse, bombes supplémentaires
- **Animations fluides** : 60 FPS optimisé

## 📦 Installation

### Prérequis
- **Java 11+** (recommandé : Java 17)
- **JavaFX 11+** (si non inclus dans votre JDK)
- **Maven** (pour la compilation)

### Étapes d'installation

1. **Cloner le projet**
```bash
git clone https://github.com/votre-username/bomberman-javafx.git
cd bomberman-javafx
```

2. **Compiler le projet**
```bash
mvn clean compile
```

3. **Lancer le jeu**
```bash
mvn javafx:run
```

Ou si vous utilisez un IDE comme IntelliJ IDEA ou Eclipse, importez le projet Maven et lancez la classe `Main.java`.

## 🎮 Modes de jeu

### 🤖 Robot Survivor
- **Objectif** : Survivre et éliminer tous les bots
- **Joueurs** : 1 joueur humain vs 3 bots IA
- **Scoring** : Points pour chaque bot éliminé, pour chaque bloc détruit et pour chaque bonus récupéré

### ⚔️ Legend 1v1
- **Objectif** : Éliminer l'autre joueur ou coopérer contre les ennemis
- **Joueurs** : 2 joueurs humains + ennemis IA
- **Particularités** : 
  - 6 vies par joueur
  - Thème visuel glacial
  - Ennemis IA améliorés (Bomber et Yellow)
  - Scoring compétitif

## 🎮 Contrôles

### Mode Robot Survivor
- **Déplacement** : `ZQSD` ou `Flèches directionnelles`
- **Poser bombe** : `Espace`
- **Pause** : `Échap`

### Mode Legend 1v1
- **Joueur 1 (Blanc)** : `ZQSD` + `R` (bombe)
- **Joueur 2 (Noir)** : `IJKL` + `P` (bombe)
- **Pause** : `Échap`

### Éditeur de niveaux
- **Clic gauche** : Placer l'élément sélectionné
- **Clic droit** : Effacer la case
- **Glisser** : Dessiner en continu

## 🎨 Système de thèmes

### Thèmes disponibles

| Thème | Description | Particularités |
|-------|-------------|----------------|
| **Classic** | Style Bomberman original | Sprites traditionnels, couleurs vives |
| **Legend** | Thème glacial | Ninjas, blocs de glace, power-ups neige |
| **Retro** | Style pixel art old-school | Graphismes 8-bit |
| **Futuristic** | Thème sci-fi | Design moderne et futuriste |

### Création de thèmes personnalisés
1. Accédez au menu **Thèmes**
2. Cliquez sur **Créer un thème**
3. Choisissez un thème de base
4. Personnalisez les sprites et couleurs
5. Sauvegardez votre création

## 🛠️ Éditeur de niveaux

### Outils disponibles
- **Vide** : Cases marchables
- **Mur fixe** : Murs indestructibles
- **Mur destructible** : Murs cassables

### Fonctionnalités
- **Sauvegarde/Chargement** : Gardez vos créations
- **Aperçu en temps réel** : Visualisez votre map
- **Validation automatique** : Zones de spawn protégées
- **Export** : Partagez vos maps

### Contraintes
- Les positions de spawn (coins) sont protégées
- Au minimum 10 cases jouables requises
- Dimensions fixes : 15x13 cases

## 🏗️ Architecture technique

### Structure du projet
```
src/main/java/com/bomberman/
├── controller/          # Contrôleurs JavaFX (MVC)
├── model/              # Logique métier et entités
│   ├── enums/         # Énumérations (Direction, CellType, etc.)
│   └── ...
├── utils/              # Utilitaires (Constants, Managers)
├── view/               # Rendu et affichage
└── Main.java           # Point d'entrée

src/main/resources/
├── fxml/               # Interfaces utilisateur
├── images/             # Sprites et graphismes
├── sons/               # Musiques et effets sonores
└── css/                # Feuilles de style
```

### Patterns utilisés
- **MVC** : Séparation claire des responsabilités
- **Singleton** : Gestionnaires (ThemeManager, MapManager)
- **Observer** : Système d'événements de jeu
- **Strategy** : IA des différents ennemis

### Technologies
- **JavaFX** : Interface utilisateur et rendu
- **FXML** : Déclaration des interfaces
- **CSS** : Styling et thèmes visuels
- **Sérialisation Java** : Sauvegarde des maps et thèmes

## 🎯 Fonctionnalités avancées

### Intelligence Artificielle
- **Pathfinding** : Les bots trouvent le chemin optimal
- **Comportements adaptatifs** : Différentes stratégies par type d'ennemi
- **Anti-blocage** : Système pour éviter les situations bloquées

### Optimisations
- **Rendu 60 FPS** : Affichage fluide optimisé
- **Cache d'images** : Chargement intelligent des sprites
- **Positions pré-calculées** : Optimisation des calculs de rendu

### Système de scoring
- **Blocs détruits** : +50 points
- **Bots éliminés** : +300 points
- **Victoire** : +1000 points
- **Bonus coopération** : Points supplémentaires en Legend

## 👥 Crédits

- **Développement** : Tous les membres du groupe 11 de la SAÉ S2.01 de la promotion 2024/2025 à Aix-en-Provence
- **Sprites** : Assets Bomberman classiques
- **Musiques** : Compositions originales et remixes
- **Inspiration** : SuperBomberman original 

---

### 🏆 Fonctionnalités spéciales

- **Menu de pause** complet avec reprise, recommencer et retour au menu
- **Système de thèmes** entièrement personnalisable
- **Éditeur de niveaux** intégré avec validation
- **Modes de jeu** variés pour solo et multijoueur local
- **Optimisations** pour un gameplay fluide à 60 FPS

*Amusez-vous bien avec ce Bomberman moderne ! 💣*
