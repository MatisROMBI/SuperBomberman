# Super Bomberman

## Tests Unitaires

Ce projet utilise JUnit 5 et Mockito pour les tests unitaires. Les tests sont organisés dans le dossier `src/test/java/com/bomberman/model/`.

### Structure des Tests

- `BoardTest.java` : Tests pour la classe Board
  - Test d'initialisation du plateau
  - Test des dimensions du plateau
  - Test d'accès aux cellules
  - Test de validation des positions

- `BombTest.java` : Tests pour la classe Bomb
  - Test d'initialisation de la bombe
  - Test de l'explosion
  - Test de la progression
  - Test du temps restant

- `GameTest.java` : Tests pour la classe Game
  - Test d'initialisation du jeu
  - Test de la pause/reprise
  - Test de l'arrêt du jeu
  - Test du mode multijoueur

- `PlayerTest.java` : Tests pour la classe Player
  - Test d'initialisation du joueur
  - Test des mouvements
  - Test du score
  - Test des bombes

### Exécution des Tests

Pour exécuter les tests, utilisez la commande Maven suivante :

```bash
./mvnw test
```

Pour exécuter un test spécifique :

```bash
./mvnw test -Dtest=BoardTest
```

### Configuration

Les dépendances de test sont configurées dans le fichier `pom.xml` :

- JUnit Jupiter 5.9.2
- Mockito 5.3.1

### Rapports de Test

Les rapports de test sont générés dans le dossier `target/surefire-reports/` après l'exécution des tests. 