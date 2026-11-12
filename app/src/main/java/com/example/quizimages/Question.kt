package com.example.quizimages

import androidx.annotation.DrawableRes

/**
 * Une question du quiz.
 *
 * @property texte texte de la question (porte sur le contenu de l'image)
 * @property image identifiant de la ressource drawable associée
 * @property choix les trois réponses proposées
 * @property bonneReponse indice (0, 1 ou 2) du choix correct dans [choix]
 * @property description description de l'image pour l'accessibilité (TalkBack)
 */
data class Question(
    val texte: String,
    @DrawableRes val image: Int,
    val choix: Array<String>,
    val bonneReponse: Int,
    val description: String
) {
    init {
        require(choix.size == 3) { "Chaque question doit proposer exactement trois choix" }
        require(bonneReponse in 0..2) { "L'indice de la bonne réponse doit être 0, 1 ou 2" }
    }

    // equals/hashCode redéfinis car la classe contient un tableau
    override fun equals(other: Any?) = other is Question && texte == other.texte && image == other.image
    override fun hashCode() = 31 * texte.hashCode() + image
}
