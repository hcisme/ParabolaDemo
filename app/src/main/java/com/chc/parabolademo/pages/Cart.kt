package com.chc.parabolademo.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun Cart(modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    var itemCount by remember { mutableIntStateOf(0) }
    var parentWindowPos by remember { mutableStateOf(Offset.Zero) }
    var cartPosition by remember { mutableStateOf(Offset.Zero) }
    val ballList = remember { mutableStateListOf<BallAnimation>() }
    val iconSize = remember { 24 }
    val ballHalfSize = remember { with(density) { (iconSize / 2).dp.toPx() } }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .onGloballyPositioned { parentCoords ->
                parentWindowPos = parentCoords.positionInWindow()
            }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(count = 24, key = { it }) { index ->
                ListItem(
                    headlineContent = {
                        Text("${index + 1}")
                    },
                    leadingContent = {
                        AsyncImage(
                            model = "https://api.dicebear.com/7.x/miniavs/svg?seed=${index + 1}",
                            contentDescription = "图片 $index",
                        )
                    },
                    trailingContent = {
                        var iconPos by remember { mutableStateOf(Offset.Zero) }

                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = null,
                            modifier = Modifier
                                .size(iconSize.dp)
                                .onGloballyPositioned { layoutCoordinates ->
                                    iconPos =
                                        layoutCoordinates.positionInWindow() - parentWindowPos
                                }
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    ballList.add(BallAnimation(startPosition = iconPos))
                                    itemCount += 1
                                }
                        )
                    }
                )
            }
        }

        ballList.forEach { ball ->
            key(ball.id) {
                ParabolaBall(
                    ball = ball,
                    targetPosition = cartPosition,
                    onAnimationEnd = { id ->
                        ballList.removeIf { it.id == id }
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .offset(x = 24.dp, y = (-24).dp)
                .size(58.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.errorContainer)
                .border(2.dp, Color.White, CircleShape)
                .align(Alignment.BottomStart)
                .onGloballyPositioned { layoutCoordinates ->
                    val topLeftInWindow = layoutCoordinates.positionInWindow()
                    val centerOffset = Offset(
                        x = layoutCoordinates.size.width / 2f - ballHalfSize,
                        y = layoutCoordinates.size.height / 2f - ballHalfSize
                    )
                    cartPosition = topLeftInWindow - parentWindowPos + centerOffset
                },
            contentAlignment = Alignment.Center
        ) {
            BadgedBox(
                badge = {
                    if (itemCount > 0) {
                        Badge(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        ) {
                            Text("$itemCount")
                        }
                    }
                }
            ) {
                Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null)
            }
        }
    }
}