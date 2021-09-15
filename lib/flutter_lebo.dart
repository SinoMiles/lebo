import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter_lebo/tv_list.dart';

class FlutterLebo {
  static late Function _connectListener;
  static late Function _disConnectListener;
  static LbCallBack _lbCallBack = LbCallBack();
  //设备列表回调
  static late ValueChanged<List<TvData>> _serviecListener;
  static const MethodChannel _channel = const MethodChannel('flutter_lebo');
  static const EventChannel _eventChannel =
      const EventChannel("flutter_lebo_event");

  //初始化sdk
  //返回值：初始化成功与否
  static Future<bool> initLBSdk(
    String androidAppid,
    String androidSecretKey,
    String iosAppid,
    String iosSecretKey,
  ) async {
    //初始化的时候注册eventChannel回调
    eventChannelDistribution();
    return _channel.invokeMethod(
      "initLBSdk",
      {
        "androidAppid": androidAppid,
        "androidSecretKey": androidSecretKey,
        "iosAppid": iosAppid,
        "iosSecretKey": iosSecretKey,
      },
    ).then((data) {
      return data;
    });
  }

  //获取设备列表
  //回调：设备数组
  static getServicesList(ValueChanged<List<TvData>> serviecListener) {
    //开始搜索设备
    _channel.invokeMethod("beginSearchEquipment");

    _serviecListener = serviecListener;
  }

  //连接设备
  static connectToService(String ipAddress, String name,
      Function fConnectListener, Function fDisConnectListener) {
    _connectListener = fConnectListener;
    _disConnectListener = fDisConnectListener;
    _channel.invokeMethod(
        "connectToService", {"ipAddress": ipAddress, "name": name});
  }

  //断开连接
  static disConnect() {
    _channel.invokeMethod("disConnect");
//        .then((data){
//      if(data == 0){
//        _disConnectListener.call();
//      }
//    });
  }

  //暂停
  static pause() {
    _channel.invokeMethod("pause");
  }

  //继续播放
  static resumePlay() {
    _channel.invokeMethod("resumePlay");
  }

  //停止播放
  static stop() {
    _channel.invokeMethod("stop");
  }

  //播放
  static play(String playUrlString) {
    _channel.invokeMethod("play", {"playUrlString": playUrlString});
  }

  static eventChannelDistribution() {
    _eventChannel.receiveBroadcastStream().listen((data) {
      print(data);
      int type = data["type"];

      switch (type) {
        case -1:
          _disConnectListener.call();
          break;
        case 0:
          TvListResult _tvList = TvListResult();
          _tvList.getResultFromMap(data["data"]);
          _serviecListener.call(_tvList.tvList);
          break;
        case 1:
          _connectListener.call();
          break;
        case 2:
          _lbCallBack.loadingCallBack();
          break;
        case 3:
          _lbCallBack.startCallBack();
          break;
        case 4:
          _lbCallBack.pauseCallBack();
          break;
        case 5:
          _lbCallBack.pauseCallBack();
          break;
        case 6:
          _lbCallBack.stopCallBack();
          break;
        case 9:
          _lbCallBack.errorCallBack(data["data"]);
          break;
        default:
          print(data["data"]);
          break;
      }
    });
  }
}

class LbCallBack {
  void startCallBack() {}

  void loadingCallBack() {}

  void completeCallBack() {}

  void pauseCallBack() {}

  void stopCallBack() {}

  void errorCallBack(String errorDes) {}
}
