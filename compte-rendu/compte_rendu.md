# Compte rendu — TP Android : quiz en images

**Nom et prénom (ou binôme) :** …………………… **Thème choisi :** formes géométriques

## 1. Interface et widgets

L'écran (`activity_main.xml`) est un `LinearLayout` vertical composé de :

- un **bandeau fixe** (fond violet) avec le titre `txt_titre`, le score `txt_score` à droite, une `ProgressBar` horizontale `progress_quiz` (max = 5) et le texte `txt_progression` ;
- un `ScrollView` contenant la zone de question (`txt_numero`, `img_question` en `fitCenter` donc sans déformation, `txt_question`, les trois `Button` `btn_choix1` à `btn_choix3`, `txt_feedback` et `btn_suivant`) et la zone de résultat `layout_resultat` (`txt_resultat`, `txt_appreciation`, `btn_rejouer`).

Le bandeau est hors du `ScrollView` : le score reste visible même sur un petit écran.

## 2. Tableau des questions et images

Une `data class Question(texte, image, choix, bonneReponse, description)` représente une question. `image` est un identifiant `@DrawableRes` (`R.drawable.triangle`…), `choix` un tableau de trois `String`, `bonneReponse` l'indice 0, 1 ou 2 du choix correct, `description` le texte d'accessibilité affecté à `contentDescription`. Un bloc `init` vérifie qu'il y a exactement trois choix.

Le tableau `questions = arrayOf(...)` contient les cinq questions ; les bonnes réponses sont aux indices 1, 0, 2, 1, 2 pour ne pas toujours être au même endroit. Les images sont des drawables vectoriels XML (noms en minuscules sans accent).

| # | Image | Question | Bonne réponse |
|---|---|---|---|
| 1 | triangle | Quelle forme géométrique voyez-vous ? | Triangle |
| 2 | carre | Combien de côtés possède cette forme ? | 4 |
| 3 | cercle | De quelle couleur est ce cercle ? | Vert |
| 4 | etoile | Combien de branches compte cette étoile ? | 5 |
| 5 | rectangle | Quelle forme est affichée ? | Rectangle |

## 3. Événements, score et progression

- **Quel choix a été cliqué ?** Les boutons sont dans une liste ; `forEachIndexed` associe à chacun un écouteur qui appelle `verifierReponse(indice)`. On compare `indice` à `questions[indiceQuestion].bonneReponse`.
- **Un seul point par question** : la variable `reponduQuestion` passe à `true` au premier clic ; `verifierReponse` sort immédiatement si elle est déjà vraie, et les trois boutons sont désactivés (`isEnabled = false`).
- **Score ≠ progression** : `score` n'augmente que pour une bonne réponse ; `nbReponses` (valeur de la `ProgressBar`) augmente à chaque réponse, juste ou fausse.
- **Dernière question** : `indiceQuestion == questions.lastIndex` → le bouton affiche « Voir le résultat » ; un clic passe `partieTerminee` à vrai et affiche le score final sans jamais dépasser le tableau.
- **Rejouer** remet indice, score, progression et états à zéro.

## 4. Tests

| Test | Attendu | Résultat |
|---|---|---|
| Nouvelle partie | Q1, 3 choix, 0/5, progression 0/5 | ✅ |
| Bonne réponse | score +1, progression +1 | ✅ |
| Mauvaise réponse | score inchangé, progression +1 | ✅ |
| Plusieurs clics | aucun point supplémentaire, choix désactivés | ✅ |
| Question suivante | image/texte/choix mis à jour, score conservé | ✅ |
| 5 bonnes réponses | 5/5 | ✅ |
| 5 mauvaises réponses | 0/5, progression 5/5 | ✅ |
| Réponses mélangées (4 justes) | 4/5 | ✅ |
| Rejouer | retour Q1, 0/5 | ✅ |
| Rotation | partie conservée | ✅ |

<p><img src="../captures/01_question_debut.png" width="160"> <img src="../captures/02_bonne_reponse.png" width="160"> <img src="../captures/03_mauvaise_reponse.png" width="160"> <img src="../captures/05_resultat_final.png" width="160"></p>

## 5. Difficultés rencontrées

- Distinguer clairement **score** et **progression** : deux variables séparées ont évité de lier la barre au nombre de bonnes réponses.
- Restaurer l'état après rotation : il faut sauvegarder aussi le choix donné pour réafficher la correction de la question en cours.
- Garder le score visible avec un défilement : le bandeau a été placé en dehors du `ScrollView`.
