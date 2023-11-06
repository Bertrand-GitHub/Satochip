package fr.toporin.satochip.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import fr.toporin.satochip.R
import fr.toporin.satochip.ui.component.CommonBottomNavigation
import fr.toporin.satochip.ui.component.CommonContainer
import fr.toporin.satochip.ui.component.CommonHeader
import fr.toporin.satochip.ui.theme.SatochipTheme

class LogFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SatochipTheme {
                    LogsScreen()
                }
            }
        }
    }


    @Composable
    fun LogsScreen() {
        val navController = findNavController()
        Column(
            Modifier.fillMaxSize(),
        ) {
            // Title
            Box(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxWidth()
            ) {
                CommonHeader(drawableId = R.drawable.title_logs)
            }
            // Container
            Box(
                modifier = Modifier
                    .weight(5f)
                    .fillMaxSize()
                    .background(color = Color(0xFF1A1B2B)),
                contentAlignment = Alignment.TopCenter,
            ) {
                CommonContainer {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(32.dp)
                    ) {

                    }
                }
            }
            //Navigation
            Box(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxWidth()
            ) {
                CommonBottomNavigation(navController, currentScreenId = R.id.logsFragment)
            }
        }

    }
}




