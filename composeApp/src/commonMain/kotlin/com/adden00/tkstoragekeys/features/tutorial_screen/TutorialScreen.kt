package com.adden00.tkstoragekeys.features.tutorial_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import tkstoragekeysmultiplatform.composeapp.generated.resources.Res
import tkstoragekeysmultiplatform.composeapp.generated.resources.ic_back
import tkstoragekeysmultiplatform.composeapp.generated.resources.tutorial_1
import tkstoragekeysmultiplatform.composeapp.generated.resources.tutorial_2
import tkstoragekeysmultiplatform.composeapp.generated.resources.tutorial_3
import tkstoragekeysmultiplatform.composeapp.generated.resources.tutorial_4
import tkstoragekeysmultiplatform.composeapp.generated.resources.tutorial_5
import tkstoragekeysmultiplatform.composeapp.generated.resources.tutorial_6
import tkstoragekeysmultiplatform.composeapp.generated.resources.tutorial_app_1
import tkstoragekeysmultiplatform.composeapp.generated.resources.tutorial_app_2
import tkstoragekeysmultiplatform.composeapp.generated.resources.tutorial_common
import tkstoragekeysmultiplatform.composeapp.generated.resources.tutorial_rules

@Composable
fun TutorialScreen(
    navigator: Navigator = LocalNavigator.currentOrThrow
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(modifier = Modifier
        .fillMaxSize()
        .imePadding(), snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomStart) {
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedIconButton(
                    onClick = {
                        navigator.pop()
                    }
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_back),
                        contentDescription = "back"
                    )
                }
            }
            Text(text = stringResource(Res.string.tutorial_common))
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = stringResource(Res.string.tutorial_app_1))
            Row {
                Image(modifier = Modifier.weight(1f), painter = painterResource(Res.drawable.tutorial_1), contentDescription = "t1")
                Image(modifier = Modifier.weight(1f), painter = painterResource(Res.drawable.tutorial_2), contentDescription = "t1")
                Image(modifier = Modifier.weight(1f), painter = painterResource(Res.drawable.tutorial_3), contentDescription = "t1")
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = stringResource(Res.string.tutorial_app_2))
            Row {
                Image(modifier = Modifier.weight(1f), painter = painterResource(Res.drawable.tutorial_4), contentDescription = "t1")
                Image(modifier = Modifier.weight(1f), painter = painterResource(Res.drawable.tutorial_5), contentDescription = "t1")
                Image(modifier = Modifier.weight(1f), painter = painterResource(Res.drawable.tutorial_6), contentDescription = "t1")
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = stringResource(Res.string.tutorial_rules))

        }
    }
}
