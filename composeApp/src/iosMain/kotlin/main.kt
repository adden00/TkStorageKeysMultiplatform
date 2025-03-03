import androidx.compose.ui.window.ComposeUIViewController
import com.adden00.tkstoragekeys.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
