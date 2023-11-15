package fr.toporin.satochip.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import fr.toporin.satochip.R
import fr.toporin.satochip.ui.theme.SatochipTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SatochipTheme {
                    SplashScreen()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Stockez la référence de la coroutine pour pouvoir l'annuler plus tard.
        val job = viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                delay(1000)
                if (isAdded) {
                    findNavController().navigate(R.id.action_splashFragment_to_transactionFragment)
                }
            }
        }

        // Annulez la coroutine lorsque le fragment n'est plus visible.
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.DESTROYED) {
                job.cancel()
            }
        }
    }

    @Preview
    @Composable
    private fun SplashScreen() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = Color(0xFF1A1B2B)
                ),
            contentAlignment = Alignment.Center,
        )
        {
            Image(
                painter = painterResource(id = R.drawable.satochip2fa_logo),
                modifier = Modifier.size(350.dp),
                contentDescription = null,
            )
        }
    }
}