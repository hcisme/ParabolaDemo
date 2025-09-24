package com.chc.parabolademo.pages

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun ParabolaBall(
    ball: BallAnimation,
    targetPosition: Offset,
    onAnimationEnd: (id: Long) -> Unit
) {
    val animationProgress = remember(ball.id) { Animatable(0f) }
    var currentPosition by remember { mutableStateOf(ball.startPosition) }

    LaunchedEffect(ball.id) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = LinearOutSlowInEasing)
        ) {
            currentPosition = parabolicOffset(
                start = ball.startPosition,
                end = targetPosition,
                progress = value
            )
        }
        onAnimationEnd(ball.id)
    }

    Canvas(
        modifier = Modifier
            .offset {
                IntOffset(
                    currentPosition.x.roundToInt(),
                    currentPosition.y.roundToInt()
                )
            }
            .size(22.dp)
    ) {
        drawCircle(
            color = Color.Red,
            radius = size.minDimension / 2
        )
    }
}

fun parabolicOffset(
    start: Offset,
    end: Offset,
    progress: Float,
    peakHeight: Float = -400f
): Offset {
    // 线性插值 X
    val x = start.x + (end.x - start.x) * progress

    // 线性插值 Y 基础值
    val yLinear = start.y + (end.y - start.y) * progress

    // 抛物线偏移：在 progress=0.5 时达到 peakHeight
    val parabolaFactor = -(progress - 0.5f).pow(2) + 0.25f
    val y = yLinear + peakHeight * (parabolaFactor / 0.25f)

    return Offset(x, y)
}
