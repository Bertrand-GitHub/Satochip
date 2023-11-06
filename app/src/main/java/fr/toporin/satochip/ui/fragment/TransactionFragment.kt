package fr.toporin.satochip.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import fr.toporin.satochip.R
import fr.toporin.satochip.model.enums.UserFriendlyRequest
import fr.toporin.satochip.ui.component.AcceptButton
import fr.toporin.satochip.ui.component.CommonBottomNavigation
import fr.toporin.satochip.ui.component.CommonColumn
import fr.toporin.satochip.ui.component.CommonContainer
import fr.toporin.satochip.ui.component.CommonHeader
import fr.toporin.satochip.ui.component.RejectButton
import fr.toporin.satochip.ui.theme.SatochipTheme
import fr.toporin.satochip.ui.theme.contentStyle
import fr.toporin.satochip.ui.theme.titleStyle
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
    fun DisplayRequestInfo(
        generatedId2FA: String,
        rawRequest: String,
        message: String,
        authentiKey: String
    ) {
        when (UserFriendlyRequest.fromRawRequest(rawRequest)) {
            UserFriendlyRequest.RESET_SEED -> {
                CommonColumn(title = "AuthentiKey:", content = authentiKey, titleStyle = titleStyle, contentStyle = contentStyle)
            }
            UserFriendlyRequest.SIGN_MESSAGE -> {
                CommonColumn(title = "Message:", content = message, titleStyle = titleStyle, contentStyle = contentStyle)
            }
            UserFriendlyRequest.RESET_2FA -> {
                CommonColumn(title = "Your 2FA ID is:", content = generatedId2FA, titleStyle = titleStyle, contentStyle = contentStyle)
            }
            else -> {
                Text(
                    text = "Unknown request",
                    style = TextStyle(
                        fontWeight = FontWeight.Light,
                        fontSize = 24.sp,
                        color = Color(0xFFFFFFFF)
                    )
                )
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
            Box(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxWidth()
            ) {
                CommonHeader(drawableId = R.drawable.title_transaction)
            }
            // Container
            Box(
                modifier = Modifier
                    .weight(5f)
                    .fillMaxSize(),
                contentAlignment = Alignment.TopCenter,
            ) {
                CommonContainer {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "The request is:",
                            style = titleStyle,
                        )
                        Text(
                            text = action,
                            style = contentStyle
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
                    .fillMaxWidth()
            ) {
                CommonBottomNavigation(navController, currentScreenId = R.id.transactionFragment)
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