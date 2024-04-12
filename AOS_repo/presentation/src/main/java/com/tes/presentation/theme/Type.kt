package com.tes.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.tes.presentation.R

val vodleMainFontFamily = FontFamily(
    Font(R.font.nanumpen, FontWeight.Normal, FontStyle.Normal)
)

val vodleSubFontFamily = FontFamily(
    Font(R.font.bmjua, FontWeight.Normal, FontStyle.Normal)
)

// Set of Material typography styles to start with
val vodleTypoGraphy = Typography(

    titleLarge = TextStyle(
        fontFamily = vodleMainFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.5.sp,
        textAlign = TextAlign.Center,
        color = smooth_black
    ),

    titleMedium = TextStyle(
        fontFamily = vodleMainFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp,
        textAlign = TextAlign.Center,
        color = smooth_black
    ),

    titleSmall = TextStyle(
        fontFamily = vodleMainFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 12.sp,
        letterSpacing = 0.5.sp,
        textAlign = TextAlign.Center,
        color = smooth_black
    ),

    bodyLarge = TextStyle(
        fontFamily = vodleSubFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.5.sp,
        textAlign = TextAlign.Center,
        color = smooth_black
    ),

    bodyMedium = TextStyle(
        fontFamily = vodleSubFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp,
        textAlign = TextAlign.Center,
        color = smooth_black
    ),

    bodySmall = TextStyle(
        fontFamily = vodleSubFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 12.sp,
        letterSpacing = 0.5.sp,
        textAlign = TextAlign.Center,
        color = smooth_black
    )
)
