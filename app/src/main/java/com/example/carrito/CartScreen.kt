package com.example.carrito

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen() {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val dao = db.cartDao()
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var items by remember { mutableStateOf(listOf<CartItem>()) }

    LaunchedEffect(true) {
        items = dao.getAll()
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(title = {
                Text("Carrito de Compras", color = Color.White)
            }, colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = Color.Black
            ))
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre de prenda") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = precio,
                onValueChange = { precio = it },
                label = { Text("Precio") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            if (nombre.isNotBlank() && precio.toDoubleOrNull() != null) {
                                dao.insert(CartItem(nombre = nombre, precio = precio.toDouble()))
                                items = dao.getAll()
                                nombre = ""
                                precio = ""
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("Agregar", color = Color.White)
                }

                Button(
                    onClick = {
                        scope.launch {
                            dao.clearCart()
                            items = dao.getAll()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("Vaciar carrito", color = Color.White)
                }
            }

            Spacer(Modifier.height(16.dp))

            LazyColumn {
                items(items) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.LightGray)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(item.nombre, fontSize = 18.sp)
                                Text("S/ ${item.precio}", fontSize = 14.sp)
                            }
                            IconButton(onClick = {
                                scope.launch {
                                    dao.delete(item)
                                    items = dao.getAll()
                                }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }
}

