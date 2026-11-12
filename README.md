# TP Android — Quiz en images

Jeu de quiz de **cinq questions illustrées** (thème : formes géométriques). Pour chaque question, l'utilisateur observe une image et choisit une réponse parmi trois boutons. Une seule activité, interface XML, logique en Kotlin.

## Navigation dans le dépôt

| Élément | Emplacement |
|---|---|
| Code Kotlin | [`MainActivity.kt`](app/src/main/java/com/example/quizimages/MainActivity.kt), [`Question.kt`](app/src/main/java/com/example/quizimages/Question.kt) |
| Layout | [`activity_main.xml`](app/src/main/res/layout/activity_main.xml) |
| Images (drawables vectoriels) | [`res/drawable`](app/src/main/res/drawable) : `triangle`, `carre`, `cercle`, `etoile`, `rectangle` |
| Captures d'écran | [`captures/`](captures) |
| Compte rendu | [`compte-rendu/compte_rendu.md`](compte-rendu/compte_rendu.md) (+ PDF) |

## Ouvrir et lancer

1. Cloner le dépôt : `git clone <url du dépôt>`
2. Android Studio > **File > Open** > dossier du projet. Le **Gradle Wrapper** est inclus : la synchronisation télécharge la bonne version de Gradle.
3. Choisir un émulateur (API 24 ou plus) puis ▶ **Run**.

`local.properties`, `build/` et `.gradle/` sont exclus du dépôt (`.gitignore`).

## Règles du jeu implémentées

- Score et progression démarrent à **0/5** ; le score reste visible en haut à droite.
- Un clic valide la réponse : +1 point si elle est juste ; la **progression avance dans tous les cas**.
- Les trois choix sont ensuite **désactivés** : impossible de gagner deux points sur une question.
- **Question suivante** n'apparaît qu'après une réponse ; à la dernière question elle devient **Voir le résultat**.
- Écran final dans la même activité (choix masqués) + bouton **Rejouer**.
- Correction textuelle (✔ / ✘ + bonne réponse) : la couleur ne fait que compléter le message.
- Partie conservée lors de la rotation (`onSaveInstanceState`) ; défilement pour les petits écrans.

## Captures

| Question | Bonne réponse | Mauvaise réponse | Résultat final |
|---|---|---|---|
| <img src="captures/01_question_debut.png" width="180"> | <img src="captures/02_bonne_reponse.png" width="180"> | <img src="captures/03_mauvaise_reponse.png" width="180"> | <img src="captures/05_resultat_final.png" width="180"> |
