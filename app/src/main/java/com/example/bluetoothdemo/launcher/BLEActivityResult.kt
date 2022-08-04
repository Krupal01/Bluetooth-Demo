package com.example.bluetoothdemo.launcher

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable

class BLEActivityResult(
    private val launcher : ManagedActivityResultLauncher<Intent,ActivityResult>,
    var isEnable : Boolean?
) {
    fun lunch(intent: Intent){
        launcher.launch(intent)
    }
}

@Composable
fun rememberBluetoothLauncher() : BLEActivityResult {

    val isEnable = rememberSaveable{
        mutableStateOf<Boolean?>(null)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            if (it.resultCode == Activity.RESULT_OK){
                isEnable.value = true
            }
        })

    return remember(launcher,isEnable) {
        BLEActivityResult(launcher,isEnable.value)
    }
}


//you can try this also

//val bleLauncher = rememberLauncherForActivityResult(
//    contract = ActivityResultContracts.StartActivityForResult()
//) {
//    if (it.resultCode != RESULT_OK) {
//        return@rememberLauncherForActivityResult
//    }
//
//    our code
//}


//LaunchedEffect(Unit) {
//    phoneNumberHintLauncher.launch(
//       intent
//    )
//}