package com.chc.parabolademo.pages

import androidx.compose.ui.geometry.Offset

data class BallAnimation(
    val id: Long = System.currentTimeMillis(),
    val startPosition: Offset
)
