package app.krafted.jokersgrandtheatre.model

import androidx.annotation.DrawableRes
import app.krafted.jokersgrandtheatre.R

enum class JokerExpression(@DrawableRes val drawableRes: Int) {
    NEUTRAL(R.drawable.jok019_sym_1),
    AMUSED(R.drawable.jok019_sym_2),
    IMPRESSED(R.drawable.jok019_sym_3),
    SINISTER(R.drawable.jok019_sym_4),
    GLEEFUL(R.drawable.jok019_sym_5),
    UNHINGED(R.drawable.jok019_sym_6),
    TRIUMPHANT(R.drawable.jok019_sym_7)
}