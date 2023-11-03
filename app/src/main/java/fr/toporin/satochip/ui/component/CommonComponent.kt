package fr.toporin.satochip.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.toporin.satochip.R


@Composable
fun CommonHeader(drawableId: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFF1A1B2B)),
    ) {
        Image(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
                .size(width = 300.dp, height = 300.dp),
            painter = painterResource(id = drawableId),
            contentDescription = ""
        )
    }
}

@Composable
fun CommonContainer(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 4.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
            .background(
                color = Color(0xFF2A2B3B),
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.TopCenter,
    ) {
        content()
    }
}

@Composable
fun NavigationButton (icon: Int, onClick: () -> Unit) {
    Image(
        modifier = Modifier
            .padding(16.dp)
            .size(40.dp)
            .clickable(onClick = onClick),
        painter = painterResource(id = icon),
        contentDescription = ""
    )
}


@Composable
fun CommonBottomNavigation(
    navController: NavController,
    currentScreenId: Int
) {
    //Navigation
    Box(
        modifier = Modifier
            .background(color = Color(0xFF1A1B2B))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            // Transaction button
            NavigationButton(
                icon = if (currentScreenId == R.id.transactionFragment) R.drawable.ic_transaction_active else R.drawable.ic_transaction_default,
                onClick = { navController.navigate(R.id.transactionFragment) }
            )
            // QrCode button
            NavigationButton(
                icon = if (currentScreenId == R.id.qrCodeFragment) R.drawable.ic_qrcode_active else R.drawable.ic_qrcode_default,
                onClick = { navController.navigate(R.id.qrCodeFragment) }
            )
            // Settings button
            NavigationButton(
                icon = if (currentScreenId == R.id.settingsFragment) R.drawable.ic_settings_active else R.drawable.ic_settings_default,
                onClick = { navController.navigate(R.id.settingsFragment) }
            )
            // Logs button
            NavigationButton(
                icon = if (currentScreenId == R.id.logsFragment) R.drawable.ic_logs_active else R.drawable.ic_logs_default,
                onClick = { navController.navigate(R.id.logsFragment) }
            )
        }
    }
}

@Composable
fun CommonColumn(
    title: String,
    content: String,
    titleStyle: TextStyle,
    contentStyle: TextStyle,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(text = title, style = titleStyle)
        Text(text = content, style = contentStyle)
    }
}