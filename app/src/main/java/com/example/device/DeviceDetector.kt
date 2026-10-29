package com.example.device

import android.app.ActivityManager
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.util.DisplayMetrics
import android.view.WindowManager
import com.example.model.DeviceSpecs

class DeviceDetector(private val context: Context) {

    fun detectDevice(): DeviceSpecs {
        // Memory
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager?.getMemoryInfo(memInfo)
        val totalRam = memInfo.totalMem
        val availRam = memInfo.availMem

        // Storage
        var totalStorage = 0L
        var availStorage = 0L
        try {
            val stat = StatFs(Environment.getDataDirectory().path)
            totalStorage = stat.blockSizeLong * stat.blockCountLong
            availStorage = stat.blockSizeLong * stat.availableBlocksLong
        } catch (_: Exception) {
            // Fallback for restricted storage access
        }

        // Display
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        val display = wm?.defaultDisplay
        val metrics = DisplayMetrics()
        display?.getMetrics(metrics)
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        val densityDpi = metrics.densityDpi
        val refreshRate = display?.refreshRate ?: 60f

        val resolutionCategory = when {
            width >= 1440 || height >= 2560 -> "QHD+"
            width >= 1080 || height >= 2300 -> "FHD+"
            width >= 720 || height >= 1500 -> "HD+"
            else -> "SD"
        }
        val resolutionText = "$width × $height ($resolutionCategory)"

        // CPU & SoC
        val cpuCores = Runtime.getRuntime().availableProcessors()
        val cpuArch = Build.SUPPORTED_ABIS.firstOrNull() ?: "arm64-v8a"
        val socModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Build.SOC_MODEL.takeIf { it.isNotBlank() && it != Build.UNKNOWN } ?: detectCpuModelFallback()
        } else {
            detectCpuModelFallback()
        }
        val socManufacturer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Build.SOC_MANUFACTURER.takeIf { it.isNotBlank() && it != Build.UNKNOWN } ?: Build.MANUFACTURER
        } else {
            Build.MANUFACTURER
        }

        // Sensors
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val hasGyroscope = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null
        val sensorNames = mutableListOf<String>()
        if (sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) sensorNames.add("Accelerometer")
        if (sensorManager?.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null) sensorNames.add("Proximity")
        if (sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT) != null) sensorNames.add("Light")
        if (sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) sensorNames.add("Magnetic")
        if (hasGyroscope) sensorNames.add("Gyroscope")

        val manufacturer = Build.MANUFACTURER.replaceFirstChar { it.uppercase() }
        val rawModel = Build.MODEL
        val model = if (rawModel.contains("sdk_gphone", ignoreCase = true)) {
            "Pixel Virtual Device (${Build.HARDWARE})"
        } else {
            rawModel
        }
        val brand = Build.BRAND.replaceFirstChar { it.uppercase() }
        val deviceName = if (Build.DEVICE.contains("emu", ignoreCase = true)) "AVD Arm64" else Build.DEVICE

        val androidVersion = "${Build.VERSION.RELEASE} (${getAndroidCodename(Build.VERSION.SDK_INT)})"

        return DeviceSpecs(
            manufacturer = manufacturer,
            brand = brand,
            model = model,
            deviceName = deviceName,
            androidVersion = androidVersion,
            sdkInt = Build.VERSION.SDK_INT,
            socModel = socModel,
            socManufacturer = socManufacturer,
            cpuCores = cpuCores,
            cpuArchitecture = cpuArch,
            totalRamBytes = totalRam,
            availableRamBytes = availRam,
            totalStorageBytes = totalStorage,
            availableStorageBytes = availStorage,
            screenResolution = resolutionText,
            screenDensityDpi = densityDpi,
            refreshRateHz = refreshRate,
            hasGyroscope = hasGyroscope,
            sensorList = sensorNames
        )
    }

    private fun detectCpuModelFallback(): String {
        val hardware = Build.HARDWARE
        return if (hardware.isNotBlank() && hardware != Build.UNKNOWN) {
            hardware
        } else {
            "${Build.BOARD} (${cpuArchFallback()})"
        }
    }

    private fun cpuArchFallback(): String {
        return Build.SUPPORTED_ABIS.firstOrNull() ?: "64-bit"
    }

    private fun getAndroidCodename(sdk: Int): String {
        return when (sdk) {
            36 -> "Baklava"
            35 -> "Vanilla Ice Cream"
            34 -> "Upside Down Cake"
            33 -> "Tiramisu"
            32 -> "Sv2"
            31 -> "Snow Cone"
            30 -> "Red Velvet Cake"
            29 -> "Quince Tart"
            28 -> "Pie"
            27 -> "Oreo MR1"
            26 -> "Oreo"
            25 -> "Nougat MR1"
            24 -> "Nougat"
            else -> "Android"
        }
    }
}
