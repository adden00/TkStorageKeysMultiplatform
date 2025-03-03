import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.adden00.tkstoragekeys.App
import com.adden00.tkstoragekeys.di.initKoin
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val body = document.body ?: return
    initKoin()
    ComposeViewport(body) {
        App()
    }
}
