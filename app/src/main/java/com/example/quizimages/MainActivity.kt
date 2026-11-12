package com.example.quizimages

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    // Les cinq questions statiques du quiz (thème : formes géométriques).
    // La bonne réponse n'est pas toujours dans le même bouton.
    private val questions = arrayOf(
        Question(
            texte = "Quelle forme géométrique voyez-vous ?",
            image = R.drawable.triangle,
            choix = arrayOf("Carré", "Triangle", "Cercle"),
            bonneReponse = 1,
            description = "Un triangle orange"
        ),
        Question(
            texte = "Combien de côtés possède cette forme ?",
            image = R.drawable.carre,
            choix = arrayOf("4", "3", "5"),
            bonneReponse = 0,
            description = "Un carré bleu"
        ),
        Question(
            texte = "De quelle couleur est ce cercle ?",
            image = R.drawable.cercle,
            choix = arrayOf("Rouge", "Bleu", "Vert"),
            bonneReponse = 2,
            description = "Un cercle vert"
        ),
        Question(
            texte = "Combien de branches compte cette étoile ?",
            image = R.drawable.etoile,
            choix = arrayOf("6", "5", "4"),
            bonneReponse = 1,
            description = "Une étoile jaune à cinq branches"
        ),
        Question(
            texte = "Quelle forme est affichée ?",
            image = R.drawable.rectangle,
            choix = arrayOf("Losange", "Carré", "Rectangle"),
            bonneReponse = 2,
            description = "Un rectangle violet plus large que haut"
        )
    )

    // État de la partie
    private var indiceQuestion = 0      // question courante (0 à 4)
    private var score = 0               // nombre de bonnes réponses
    private var nbReponses = 0          // progression : nombre de réponses données
    private var reponduQuestion = false // la question courante a-t-elle reçu une réponse ?
    private var choixDonne = -1         // indice du choix cliqué pour la question courante
    private var partieTerminee = false  // l'écran de résultat est-il affiché ?

    private lateinit var txtScore: TextView
    private lateinit var txtProgression: TextView
    private lateinit var progressQuiz: ProgressBar
    private lateinit var txtNumero: TextView
    private lateinit var imgQuestion: ImageView
    private lateinit var txtQuestion: TextView
    private lateinit var boutonsChoix: List<Button>
    private lateinit var txtFeedback: TextView
    private lateinit var btnSuivant: Button
    private lateinit var layoutQuestion: LinearLayout
    private lateinit var layoutResultat: LinearLayout
    private lateinit var txtResultat: TextView
    private lateinit var txtAppreciation: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val barres = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(barres.left, 0, barres.right, barres.bottom)
            // Le bandeau coloré passe sous la barre d'état
            findViewById<View>(R.id.bandeau).setPadding(
                dp(16), barres.top + dp(12), dp(16), dp(12)
            )
            insets
        }

        txtScore = findViewById(R.id.txt_score)
        txtProgression = findViewById(R.id.txt_progression)
        progressQuiz = findViewById(R.id.progress_quiz)
        txtNumero = findViewById(R.id.txt_numero)
        imgQuestion = findViewById(R.id.img_question)
        txtQuestion = findViewById(R.id.txt_question)
        boutonsChoix = listOf(
            findViewById(R.id.btn_choix1),
            findViewById(R.id.btn_choix2),
            findViewById(R.id.btn_choix3)
        )
        txtFeedback = findViewById(R.id.txt_feedback)
        btnSuivant = findViewById(R.id.btn_suivant)
        layoutQuestion = findViewById(R.id.layout_question)
        layoutResultat = findViewById(R.id.layout_resultat)
        txtResultat = findViewById(R.id.txt_resultat)
        txtAppreciation = findViewById(R.id.txt_appreciation)

        progressQuiz.max = questions.size

        // Chaque bouton connaît son indice : c'est ainsi qu'on sait quel choix a été cliqué
        boutonsChoix.forEachIndexed { indice, bouton ->
            bouton.setOnClickListener { verifierReponse(indice) }
        }
        btnSuivant.setOnClickListener { questionSuivante() }
        findViewById<Button>(R.id.btn_rejouer).setOnClickListener { rejouer() }

        // Restauration de la partie après une rotation de l'écran
        savedInstanceState?.let {
            indiceQuestion = it.getInt(CLE_INDICE)
            score = it.getInt(CLE_SCORE)
            nbReponses = it.getInt(CLE_REPONSES)
            reponduQuestion = it.getBoolean(CLE_REPONDU)
            choixDonne = it.getInt(CLE_CHOIX, -1)
            partieTerminee = it.getBoolean(CLE_TERMINEE)
        }

        if (partieTerminee) afficherResultat() else afficherQuestion()
    }

    /** Affiche la question courante : numéro, image, texte et trois choix. */
    private fun afficherQuestion() {
        val question = questions[indiceQuestion]
        layoutQuestion.visibility = View.VISIBLE
        layoutResultat.visibility = View.GONE

        txtNumero.text = getString(R.string.numero_question, indiceQuestion + 1, questions.size)
        imgQuestion.setImageResource(question.image)
        imgQuestion.contentDescription = question.description
        txtQuestion.text = question.texte
        boutonsChoix.forEachIndexed { i, bouton -> bouton.text = question.choix[i] }

        if (reponduQuestion) {
            afficherCorrection()
        } else {
            boutonsChoix.forEach { it.isEnabled = true }
            txtFeedback.text = ""
            btnSuivant.visibility = View.GONE
        }
        actualiserBandeau()
    }

    /** Valide le choix cliqué : un seul point possible par question. */
    private fun verifierReponse(indiceChoix: Int) {
        if (reponduQuestion) return // protection contre les clics multiples

        reponduQuestion = true
        choixDonne = indiceChoix
        nbReponses++ // la progression avance que la réponse soit juste ou fausse
        if (indiceChoix == questions[indiceQuestion].bonneReponse) {
            score++
        }
        afficherCorrection()
        actualiserBandeau()
    }

    /** Désactive les choix et affiche un message (texte + couleur) et le bouton de navigation. */
    private fun afficherCorrection() {
        val question = questions[indiceQuestion]
        boutonsChoix.forEach { it.isEnabled = false }

        val juste = choixDonne == question.bonneReponse
        // Le texte (✔ / ✘ + phrase) distingue la réponse, la couleur ne fait que compléter
        txtFeedback.text = if (juste) {
            getString(R.string.bonne_reponse)
        } else {
            getString(R.string.mauvaise_reponse, question.choix[question.bonneReponse])
        }
        txtFeedback.setTextColor(ContextCompat.getColor(this, if (juste) R.color.correct else R.color.incorrect))

        val derniere = indiceQuestion == questions.lastIndex
        btnSuivant.setText(if (derniere) R.string.voir_resultat else R.string.question_suivante)
        btnSuivant.visibility = View.VISIBLE
    }

    /** Passe à la question suivante, ou affiche le résultat après la dernière. */
    private fun questionSuivante() {
        if (!reponduQuestion) return // impossible d'avancer sans avoir répondu
        if (indiceQuestion < questions.lastIndex) {
            indiceQuestion++
            reponduQuestion = false
            choixDonne = -1
            afficherQuestion()
        } else {
            partieTerminee = true
            afficherResultat()
        }
    }

    /** Met à jour le score et la barre de progression dans le bandeau. */
    private fun actualiserBandeau() {
        txtScore.text = getString(R.string.score, score, questions.size)
        progressQuiz.progress = nbReponses
        txtProgression.text = getString(R.string.progression, nbReponses, questions.size)
    }

    /** Fin du jeu : masque les choix et affiche le score final dans la même activité. */
    private fun afficherResultat() {
        layoutQuestion.visibility = View.GONE
        layoutResultat.visibility = View.VISIBLE
        txtResultat.text = getString(R.string.resultat_final, score, questions.size)
        txtAppreciation.setText(
            when {
                score == questions.size -> R.string.appreciation_parfait
                score >= 3 -> R.string.appreciation_bien
                else -> R.string.appreciation_moyen
            }
        )
        actualiserBandeau()
    }

    /** Réinitialise le quiz, le score et la progression. */
    private fun rejouer() {
        indiceQuestion = 0
        score = 0
        nbReponses = 0
        reponduQuestion = false
        choixDonne = -1
        partieTerminee = false
        afficherQuestion()
    }

    private fun dp(valeur: Int) = (valeur * resources.displayMetrics.density).toInt()

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(CLE_INDICE, indiceQuestion)
        outState.putInt(CLE_SCORE, score)
        outState.putInt(CLE_REPONSES, nbReponses)
        outState.putBoolean(CLE_REPONDU, reponduQuestion)
        outState.putInt(CLE_CHOIX, choixDonne)
        outState.putBoolean(CLE_TERMINEE, partieTerminee)
    }

    companion object {
        private const val CLE_INDICE = "indice"
        private const val CLE_SCORE = "score"
        private const val CLE_REPONSES = "reponses"
        private const val CLE_REPONDU = "repondu"
        private const val CLE_CHOIX = "choix"
        private const val CLE_TERMINEE = "terminee"
    }
}
