package com.chc.parabolademo.nestedscroll

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlin.math.abs
import kotlin.math.min

@Composable
fun DouyinScroll(
    modifier: Modifier = Modifier,
    headerHeightDp: Dp = 320.dp,
    navHeightDp: Dp = 56.dp
) {
    val density = LocalDensity.current
    val listState = rememberLazyListState()
    val statusBarHeightPx = WindowInsets.statusBars.getTop(density).toFloat()
    // nav高度 和 statusBar高度
    val navHeightWithStatusBarDp = with(density) { navHeightDp + statusBarHeightPx.toDp() }
    val navHeightWithStatusBarPx by rememberUpdatedState(with(density) { navHeightWithStatusBarDp.toPx() })
    // 头部 偏移的距离
    var headerOffsetPx by remember { mutableFloatStateOf(0f) }
    val headerOffsetDp = with(density) { headerOffsetPx.toDp() }
    // 头部高度 单位 px
    val headerHeightPx = with(density) { headerHeightDp.toPx() }
    // 列表顶部是否显示出来了
    val listAtTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 &&
                    listState.firstVisibleItemScrollOffset == 0
        }
    }
    val maxFoldPx = remember(headerHeightPx, navHeightWithStatusBarPx) {
        (headerHeightPx - navHeightWithStatusBarPx).coerceAtLeast(0f)
    }
    val foldProgress by remember(maxFoldPx) {
        derivedStateOf {
            if (maxFoldPx <= 0f) 1f
            else (abs(headerOffsetPx) / maxFoldPx).coerceIn(0f, 1f)
        }
    }
    val navAlphaAnim by animateFloatAsState(
        targetValue = foldProgress,
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing)
    )
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val deltaY = available.y

                if (deltaY < 0) {
                    val canFold = headerHeightPx - navHeightWithStatusBarPx + headerOffsetPx

                    if (canFold > 0) {
                        // 计算实际消耗的滚动距离
                        val scrollToConsume = abs(deltaY)
                        val actualConsumed = min(scrollToConsume, canFold)

                        headerOffsetPx -= actualConsumed
                        return Offset(0f, -actualConsumed)
                    }
                }
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val deltaY = available.y

                if (deltaY > 0 && headerOffsetPx < 0 && listAtTop) {
                    val consumedY = min(a = deltaY, b = abs(headerOffsetPx))
                    headerOffsetPx += consumedY
                    return Offset(x = 0f, y = consumedY)
                }
                return Offset.Zero
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding()
            .nestedScroll(nestedScrollConnection)
    ) {
        Box(
            modifier = Modifier
                .offset(offset = { IntOffset(x = 0, y = headerOffsetPx.toInt()) })
                .fillMaxWidth()
                .height(headerHeightDp)
                .background(Color(0xFFFFAB00)),
            contentAlignment = Alignment.CenterStart
        ) {
            AsyncImage(
                model = "https://api.dicebear.com/7.x/miniavs/svg?seed=1",
                contentDescription = "图片 1"
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = headerHeightDp + headerOffsetDp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .drawWithContent {
                        drawContent()
                        drawLine(
                            color = Color.Black.copy(alpha = 0.4f),
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = 1.dp.toPx()
                        )
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "作品 1139",
                    modifier = Modifier.padding(start = 32.dp, end = 2.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = Icons.Default.ArrowDropDown.name
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 32.dp),
                state = listState
            ) {
                items(100) {
                    Text(text = "第 $it 项", modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(navHeightWithStatusBarDp)
                .graphicsLayer { alpha = navAlphaAnim }
                .background(MaterialTheme.colorScheme.background)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(with(density) { statusBarHeightPx.toDp() })
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(navHeightDp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = Icons.AutoMirrored.Filled.KeyboardArrowLeft.name
                    )
                }
                AsyncImage(
                    model = "https://api.dicebear.com/7.x/miniavs/svg?seed=1",
                    contentDescription = "图片 1",
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color(0xFFFFAB00), CircleShape)
                        .padding(3.dp)
                )
            }
        }
    }
}
