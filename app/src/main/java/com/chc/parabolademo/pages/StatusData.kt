package com.chc.parabolademo.pages

import androidx.compose.ui.geometry.Offset

data class BallAnimation(
    val id: Long = System.currentTimeMillis(),
    val startPosition: Offset,
    var state: AnimationState = AnimationState.Running
)

enum class AnimationState {
    Running, Completed, Removed
}
