
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import cafe.adriel.voyager.navigator.Navigator
import data.di.initializeKoin
import org.jetbrains.compose.ui.tooling.preview.Preview
import ui.presentation.screen.HomeScreen


@Composable
@Preview
fun App() {
    initializeKoin()

    MaterialTheme {
        Navigator(HomeScreen())
    }
}