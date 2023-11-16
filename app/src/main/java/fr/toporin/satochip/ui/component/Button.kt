package fr.toporin.satochip.ui.component

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.toporin.satochip.ui.theme.Orange


@Composable
fun AcceptButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .width(140.dp)
            .height(100.dp),
        shape = RoundedCornerShape(10.dp)
    )
    {
        Text(
            text = "ACCEPT REQUEST",
            textAlign = TextAlign.Center,
            color = Orange,
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
            )
        )
    }
}


@Composable
fun RejectButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .width(140.dp)
            .height(100.dp),
        shape = RoundedCornerShape(10.dp)
    )
    {
        Text(
            text = "REJECT REQUEST",
            textAlign = TextAlign.Center,
            color = Color.LightGray,
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
            )
        )
    }
}

@Composable
fun ScanButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .width(140.dp)
            .height(100.dp),
        shape = RoundedCornerShape(10.dp)
    )
    {
        Text(
            text = "SCAN   QR CODE",
            textAlign = TextAlign.Center,
            color = Color.LightGray,
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
            )
        )
    }
}

@Composable
fun ConfirmQrCodeButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .width(140.dp)
            .height(100.dp),
        shape = RoundedCornerShape(10.dp)
    )
    {
        Text(
            text = "CONFIRM QR CODE",
            textAlign = TextAlign.Center,
            color = Orange,
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
            )
        )
    }
}

@Preview
@Composable
fun AcceptButtonPreview() {
    AcceptButton(onClick = { /*TODO*/ })
}

@Preview
@Composable
fun RejectButtonPreview() {
    RejectButton(onClick = { /*TODO*/ })
}

@Preview
@Composable
fun ScanButtonPreview() {
    ScanButton(onClick = { /*TODO*/ })
}
