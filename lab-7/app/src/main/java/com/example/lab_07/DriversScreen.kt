package com.example.lab_07

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun DriversScreen(navController: NavController) {
    var driverName by remember { mutableStateOf("") }
    val drivers = remember { mutableStateListOf<String>() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.testTag("driversBackButton")
        ) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("FormulaZero Drivers", fontSize = 30.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = driverName,
                onValueChange = { driverName = it },
                label = { Text("Driver Name") },
                modifier = Modifier.weight(1f).testTag("driverInput")
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    val name = driverName.trim()
                    if (name.isNotEmpty()) {
                        drivers.add(name)
                        driverName = ""
                    }
                },
                modifier = Modifier.testTag("addDriverButton")
            ) {
                Text("Add")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { drivers.clear() },
            modifier = Modifier.testTag("clearDriversButton")
        ) {
            Text("Clear all drivers")
        }

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(drivers) { driver ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(driver, fontSize = 24.sp)
            }
        }
    }
}
