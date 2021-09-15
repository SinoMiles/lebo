package com.example.flutter_lebo

import android.content.Context
import androidx.annotation.NonNull
import com.example.lblelinkplugin.LeBUtil

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry

/** FlutterLeboPlugin */
class FlutterLeboPlugin: FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_lebo")
    channel.setMethodCallHandler(this)
    ctx = flutterPluginBinding.applicationContext
    EventChannel(flutterPluginBinding.binaryMessenger, "flutter_lebo_event").setStreamHandler(object : EventChannel.StreamHandler {
      override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        LeBUtil.instance.initEvent(events)
      }

      override fun onCancel(arguments: Any?) {
        LeBUtil.instance.removeEvent()
      }
    })
  }
  companion object {
    var ctx: Context? = null
    @JvmStatic
    fun registerWith(registrar: PluginRegistry.Registrar) {
      val channel = MethodChannel(registrar.messenger(), "flutter_lebo")
      channel.setMethodCallHandler(FlutterLeboPlugin())
      ctx = registrar.context().applicationContext
      EventChannel(registrar.messenger(), "flutter_lebo_event").setStreamHandler(object : EventChannel.StreamHandler {
        override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
          LeBUtil.instance.initEvent(events)
        }

        override fun onCancel(arguments: Any?) {
          LeBUtil.instance.removeEvent()
        }
      })
    }
  }
  ///调用具体方法 dart端调用invokeMethod()时执行对应方法
  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when (call.method) {
      "initLBSdk" -> {
        ctx?.run {
          LeBUtil.instance.initUtil(this, call.argument<String>("androidAppid")!!, call.argument<String>("androidSecretKey")!!, result)
        }
      }
      "connectToService" -> {
        LeBUtil.instance.connectService(call.argument<String>("ipAddress")!!, call.argument<String>("name")!!)
      }
      "disConnect" -> {
        LeBUtil.instance.disConnect(result)
      }
      "pause" -> {
        LeBUtil.instance.pause()
      }
      "resumePlay" -> {
        LeBUtil.instance.resumePlay()
      }
      "stop" -> {
        LeBUtil.instance.stop()
      }
      "beginSearchEquipment" -> {
        LeBUtil.instance.searchDevice()
      }
      "stopSearchEquipment" -> {
        LeBUtil.instance.stopSearch()
      }
      "play" -> {
        LeBUtil.instance.play(call.argument<String>("playUrlString")!!)
      }
      "getLastConnectService"->{
        LeBUtil.instance.getLastIp(result)
      }
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
