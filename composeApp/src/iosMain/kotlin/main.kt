import androidx.compose.ui.window.ComposeUIViewController
import com.adden00.tkstoragekeys.App
import com.adden00.tkstoragekeys.di.initKoin
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    initKoin()
    return ComposeUIViewController { App() }
}
