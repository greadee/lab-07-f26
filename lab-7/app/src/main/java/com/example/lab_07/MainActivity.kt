package com.example.lab_07

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.lab_07.ui.theme.Lab07Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab07Theme {
                FormulaZeroApp()
            }
        }
    }
}
