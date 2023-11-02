package fr.toporin.satochip.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import fr.toporin.satochip.R
import fr.toporin.satochip.ui.component.AcceptButton
import fr.toporin.satochip.ui.component.RejectButton
import fr.toporin.satochip.ui.theme.SatochipTheme
import fr.toporin.satochip.util.DisplayRequestInfo
import fr.toporin.satochip.viewmodel.TransactionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TransactionFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private val viewModel: TransactionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            viewModel.initializeId2FA()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SatochipTheme {
                    DisplayValues(viewModel = viewModel)
                }
            }
        }
    }

    @Composable
    fun DisplayValues(viewModel: TransactionViewModel) {
        val navController = findNavController()
        val generatedId2FA = viewModel.generatedId2FA.observeAsState("").value
        val rawRequest = viewModel.rawRequestLiveData.observeAsState("").value
        val message = viewModel.messageLiveData.observeAsState("").value
        val authentiKey = viewModel.authentiKeyLiveData.observeAsState("").value
        val action = viewModel.requestLiveData.observeAsState("").value

        Column (
            Modifier.fillMaxSize(),
        ){
            // Title
            Box(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxWidth()
                    .background(color = Color(0xFF1A1B2B)),
            ) {
                Image(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                        .size(width = 300.dp, height = 300.dp),
                    painter = painterResource(id = R.drawable.title_transaction), contentDescription = "" )
            }
            // Container
            Box(
                modifier = Modifier
                    .weight(5f)
                    .fillMaxSize()
                    .background(color = Color(0xFF1A1B2B)),
                contentAlignment = Alignment.TopCenter,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .background(
                            color = Color(0xFF2A2B3B),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        Text(
                            text = "The request is:",
                            color = Color(0xFFFFBB0B),
                            style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 24.sp),
                            modifier = Modifier
                                .padding(top = 24.dp)
                        )
                        Text(
                            text = action,
                            color = Color(0xFFFFFFFF),
                            style = TextStyle(
                                fontWeight = FontWeight.Light,
                                fontSize = 24.sp,
                                fontStyle = FontStyle.Italic
                            )
                        )

                        DisplayRequestInfo(
                            generatedId2FA = generatedId2FA,
                            rawRequest = rawRequest,
                            message = message,
                            authentiKey = authentiKey
                        )

                        Spacer(modifier = Modifier.weight(1f))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, start = 8.dp, end = 8.dp, bottom = 24.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            // Accept Button
                            AcceptButton(onClick = { /*TODO*/ })
                            // Reject Button
                            RejectButton(onClick = { /*TODO*/ })
                        }
                    }
                }
            }
            //Navigation
            Box(
                modifier = Modifier
                    .weight(0.6f)
                    .background(color = Color(0xFF1A1B2B))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(40.dp),
                        painter = painterResource(id = R.drawable.ic_transaction_active),
                        contentDescription = ""
                    )
                    Image(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(40.dp)
                            .clickable { navController.navigate(R.id.action_transactionFragment_to_qrCodeFragment) },
                        painter = painterResource(id = R.drawable.ic_qrcode_default),
                        contentDescription = "Navigate to QRCode"
                    )
                    Image(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(40.dp)
                            .clickable { navController.navigate("settings") },
                        painter = painterResource(id = R.drawable.ic_settings_default),
                        contentDescription = ""
                    )
                    Image(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(40.dp)
                            .clickable { navController.navigate("logs") },
                        painter = painterResource(id = R.drawable.ic_logs_default),
                        contentDescription = ""
                    )
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DisplayValuesPreview() {
        SatochipTheme {
            DisplayValues(viewModel = TransactionViewModel())
        }
    }
}