package com.example.bluetoothdemo.launcher

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

@Composable
fun rememberStartDiscoverableLauncher(
    onResultOkay : ()->Unit,
    onResultNotOkay : ()->Unit
): ManagedActivityResultLauncher<Intent, ActivityResult> {

    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            if (it.resultCode == Activity.RESULT_OK){
                onResultOkay()
            }else{
                onResultNotOkay()
            }
        }
    )
}


