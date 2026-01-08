package com.ipca.socialstore.presentation.views.scan

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ipca.socialstore.data.enums.UserRole
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning


@Composable
fun ScanView(modifier: Modifier, navController: NavController, userRole: UserRole){

}

@Composable
fun ScanContent(modifier: Modifier){

}
@Composable
fun SimpleBarcodeScanner() {
    val context = LocalContext.current
    var barcodeResult by remember { mutableStateOf("Nenhum código lido") }

    val scanner = remember { GmsBarcodeScanning.getClient(context) }

    Column(modifier = Modifier.padding(16.dp)) {

        Text(text = "Resultado: $barcodeResult")

        Button(
            onClick = {
                scanner.startScan()
                    .addOnSuccessListener { barcode ->
                        barcodeResult = barcode.rawValue ?: "Erro na leitura"
                        Toast.makeText(context, "Lido: $barcodeResult", Toast.LENGTH_SHORT).show()
                    }
                    .addOnCanceledListener {
                        barcodeResult = "Cancelado pelo utilizador"
                    }
                    .addOnFailureListener { e ->
                        barcodeResult = "Erro: ${e.message}"
                    }
            }
        ) {
            Text("Ler Código de Barras")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScanPreview(){
    ScanContent(
        modifier = Modifier,
    )
}