package com.chc.parabolademo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.chc.parabolademo.pages.Cart
import com.chc.parabolademo.ui.theme.ParabolaDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ParabolaDemoTheme {
                Cart()
            }
        }
    }
}
