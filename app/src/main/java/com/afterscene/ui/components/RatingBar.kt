package com.afterscene.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RatingBar(
    rating: Int,
    maxRating: Int = 10,
    onRatingSelected: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier,
    starColor: Color = Color(0xFFFFD700) // Gold
) {
    Row(modifier = modifier) {
        for (i in 1..maxRating) {
            val isFilled = i <= rating
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Estrela $i",
                tint = if (isFilled) starColor else Color.Gray.copy(alpha = 0.4f),
                modifier = Modifier
                    .size(20.dp)
                    .then(
                        if (onRatingSelected != null) {
                            Modifier.clickable { onRatingSelected(i) }
                        } else {
                            Modifier
                        }
                    )
            )
        }
    }
}
