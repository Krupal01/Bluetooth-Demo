package com.example.bluetoothdemo

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.bluetoothdemo.launcher.rememberBluetoothLauncher
import com.example.bluetoothdemo.ui.theme.BluetoothDemoTheme
import com.google.accompanist.permissions.*


const val TAG = "TAG"
class MainActivity : ComponentActivity() {

    private lateinit var discoveryReceiver : BroadcastReceiver

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val discoverDevice = mutableStateListOf<BluetoothDevice>()
        val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        var scanning = false
        val handler = Handler()
        val SCAN_PERIOD: Long = 10000

        discoveryReceiver = object : BroadcastReceiver(){
            override fun onReceive(p0: Context?, p1: Intent?) {
                when(intent.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        Log.i(TAG,"action found")
                        val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        Log.i(TAG,device.toString())
                        if (device != null) {
                            discoverDevice.add(device)
                        }
                    }
                    BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                        Log.i(TAG,"action discovery stared")
                    }
                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                        Log.i(TAG,"action discovery finished")
                    }
                    BluetoothAdapter.ACTION_STATE_CHANGED -> {
                        Log.i(TAG,"action action state changed")
                    }
                    BluetoothAdapter.ACTION_SCAN_MODE_CHANGED -> {
                        val modeValue = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE,BluetoothAdapter.ERROR)
                        when(modeValue){
                            BluetoothAdapter.SCAN_MODE_CONNECTABLE ->{Log.i(TAG,"action SCAN_MODE_CONNECTABLE")}
                            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE ->{Log.i(TAG,"action SCAN_MODE_CONNECTABLE_DISCOVERABLE")}
                            BluetoothAdapter.SCAN_MODE_NONE ->{Log.i(TAG,"action SCAN_MODE_NONE")}
                            else ->{Log.i(TAG,"ACTION_SCAN_MODE_CHANGED else ")}
                        }
                    }
                    else ->{
                        Log.i(TAG,"no action match ${intent.action}")
                    }

                }
            }
        }
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND)
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)
        this.registerReceiver(discoveryReceiver,intentFilter)

//        val leScanCallback: ScanCallback = object : ScanCallback() {
//            override fun onScanResult(callbackType: Int, result: ScanResult) {
//                super.onScanResult(callbackType, result)
//                Log.i(TAG,"scan callback")
//                discoverDevice.add(result.device)
//            }
//        }

        setContent {
            val context = LocalContext.current
            val lifecycleOwner = LocalLifecycleOwner.current
            val bleLauncher = rememberBluetoothLauncher()
            val discoverableLauncher = rememberBluetoothLauncher()
            val pairedDevices = remember{ mutableStateListOf<BluetoothDevice>()}
            val discoveredDevices = remember { discoverDevice }
//            val blePermission = rememberMultiplePermissionsState(
//                permissions = listOf(
//                    Manifest.permission.ACCESS_COARSE_LOCATION,
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.BLUETOOTH_ADMIN
//                )
//            )
//
//            DisposableEffect(key1 = lifecycleOwner, effect ={
//                val observer = LifecycleEventObserver{ _, evet ->
//                    if(evet == Lifecycle.Event.ON_START){
//                        blePermission.launchMultiplePermissionRequest()
//                    }
//                }
//                lifecycleOwner.lifecycle.addObserver(observer)
//
//                onDispose {
//                    lifecycleOwner.lifecycle.removeObserver(observer)
//                }
//            })
//
//            blePermission.permissions.forEach {
//                when(it.permission){
//                    Manifest.permission.ACCESS_COARSE_LOCATION ->{
//                        when{
//                            it.status.isGranted ->{
//                                if (bluetoothAdapter.isDiscovering){
//                                    bluetoothAdapter.cancelDiscovery()
//                                }
//                                bluetoothAdapter.startDiscovery()
//                            }
//                            it.status.shouldShowRationale->{
//                                Toast.makeText(context,"permission dined",Toast.LENGTH_LONG).show()
//                            }
//                            else->{
//                                Log.i(TAG,it.status.toString())
//                            }
//                        }
//                    }
//                    Manifest.permission.ACCESS_FINE_LOCATION ->{
//                        when{
//                            it.status.isGranted ->{
//                                if (bluetoothAdapter.isDiscovering){
//                                    bluetoothAdapter.cancelDiscovery()
//                                }
//                                bluetoothAdapter.startDiscovery()
//                            }
//                            it.status.shouldShowRationale->{
//                                Toast.makeText(context,"permission dined",Toast.LENGTH_LONG).show()
//                            }
//                            else->{
//                                Log.i(TAG,it.status.toString())
//                            }
//                        }
//                    }
//                    Manifest.permission.BLUETOOTH_ADMIN ->{
//                        when{
//                            it.status.isGranted ->{
//                                if (bluetoothAdapter.isDiscovering){
//                                    bluetoothAdapter.cancelDiscovery()
//                                }
//                                bluetoothAdapter.startDiscovery()
//                            }
//                            it.status.shouldShowRationale->{
//                                Toast.makeText(context,"permission dined",Toast.LENGTH_LONG).show()
//                            }
//                            else->{
//                                Log.i(TAG,it.status.toString())
//                            }
//                        }
//                    }
//                }
//            }

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
                                    if (
                                        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                                        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                                        ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                                    ) {
                                        onResume()
                                        Log.i(TAG,"request permission")
                                    }else{
                                        Log.i(TAG,"onclick start scanning")
//                                        if (!scanning) {
//                                            handler.postDelayed({
//                                                scanning = false
//                                                bluetoothLeScanner.stopScan(leScanCallback)
//                                            }, SCAN_PERIOD)
//                                            scanning = true
//                                            bluetoothLeScanner.startScan(leScanCallback)
//                                        } else {
//                                            scanning = false
//                                            bluetoothLeScanner.stopScan(leScanCallback)
//                                        }
                                        bluetoothAdapter.startDiscovery()
                                    }
                                }
                            ) {
                                Text(text = "scan devices")
                            }
                            Button(
                                modifier = Modifier
                                    .weight(1F)
                                    .padding(all = 2.dp),
                                onClick = {
                                    val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
                                    discoverableIntent.putExtra(
                                        BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
                                        15             // discoverable for 15 seconds
                                    )
                                    discoverableLauncher.lunch(discoverableIntent)
                                }
                            ) {
                                Text(text = "Discoverable")
                            }
                            Button(
                                modifier = Modifier
                                    .weight(1F)
                                    .padding(all = 2.dp),
                                onClick = {
                                    if (bluetoothAdapter.isEnabled){
                                        bluetoothAdapter.disable()
                                    }
                            }) {
                                Text(text = "stop bluetooth")
                            }
                        }
                        LazyColumn(modifier = Modifier
                            .padding(all = 5.dp)
                            .weight(1f),
                            content = {
                            item { Text(text = "Paired Deices" , fontSize = 20.sp, fontWeight = FontWeight.Bold ,modifier = Modifier.padding(5.dp)) }
                            items(pairedDevices){item: BluetoothDevice ->
                                Text(text = item.name , modifier = Modifier.padding(5.dp))
                            }
                        })
                        LazyColumn(modifier = Modifier
                            .padding(all = 5.dp)
                            .weight(1f),
                            content = {
                                item { Text(text = "Discovered Deices" , fontSize = 20.sp, fontWeight = FontWeight.Bold ,modifier = Modifier.padding(5.dp)) }
                                items(discoveredDevices){item: BluetoothDevice ->
                                    Text(text = item.name , modifier = Modifier.padding(5.dp))
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG,"onResume")
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Log.i(TAG,"request Manifest.permission.ACCESS_COARSE_LOCATION")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),0)
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Log.i(TAG,"request Manifest.permission.ACCESS_FINE_LOCATION")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED){
            Log.i(TAG,"request Manifest.permission.BLUETOOTH_ADMIN")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_ADMIN),2)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG,"onDestroy")
        unregisterReceiver(discoveryReceiver)
    }
}