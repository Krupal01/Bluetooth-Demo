package com.example.bluetoothdemo

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.example.bluetoothdemo.launcher.rememberBluetoothLauncher
import com.example.bluetoothdemo.ui.theme.BluetoothDemoTheme
import com.google.accompanist.permissions.*

const val TAG = "TAG"
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    @ExperimentalPermissionsApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        setContent {
            val context = LocalContext.current
            val bleLauncher = rememberBluetoothLauncher()
            val pairedDevices = remember{ mutableStateListOf<BluetoothDevice>()}
            val blePermission = rememberPermissionState(permission = Manifest.permission.BLUETOOTH_CONNECT)
            bleLauncher.isEnable.let {
                Log.i(TAG,"bluetooth enable")
                Log.i(TAG,bluetoothAdapter.bondedDevices.toString())
                pairedDevices.addAll(bluetoothAdapter.bondedDevices)
            }

            BluetoothDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                modifier = Modifier
                                    .weight(1F)
                                    .padding(all = 2.dp),
                                onClick = {
                                if (bluetoothAdapter != null){
                                    if (!bluetoothAdapter.isEnabled){
                                        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                                        bleLauncher.lunch(intent)
                                    }else{
                                        Log.i(TAG,"bluetooth already enable")
                                    }
                                }else{
                                    Log.i(TAG,"bluetooth not found")
                                }

                            }
                            ) {
                                Text(text = "start bluetooth")
                            }
                            Button(
                                modifier = Modifier
                                    .weight(1F)
                                    .padding(all = 2.dp),
                                onClick = {

                            }) {
                                Text(text = "scan devices")
                            }
                            Button(
                                modifier = Modifier
                                    .weight(1F)
                                    .padding(all = 2.dp),
                                onClick = {
                                    if (bluetoothAdapter.isEnabled){
                                        if (ActivityCompat.checkSelfPermission(
                                                context,
                                                Manifest.permission.BLUETOOTH_CONNECT
                                            ) != PackageManager.PERMISSION_GRANTED
                                        ) {
                                            blePermission.launchPermissionRequest()
                                            when{
                                                blePermission.status.isGranted ->{
                                                    bluetoothAdapter.disable()
                                                }
                                                blePermission.status.shouldShowRationale->{
                                                    Toast.makeText(context,"Please allow bluetooth permission",Toast.LENGTH_LONG).show()
                                                }
                                            }
                                        }
                                        bluetoothAdapter.disable()
                                    }
                            }) {
                                Text(text = "stop bluetooth")
                            }
                        }
                        LazyColumn(content = {
                            item { Text(text = "Paired Deices" , fontSize = 20.sp, fontWeight = FontWeight.Bold ,modifier = Modifier.padding(5.dp)) }
                            items(pairedDevices){item: BluetoothDevice ->
                                Text(text = item.name , modifier = Modifier.padding(5.dp))
                            }
                        })
                    }
                }
            }
        }
    }
}