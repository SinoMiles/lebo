import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_lebo/flutter_lebo.dart';
import 'package:flutter_lebo/tv_list.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  List<TvData> tvList = [];
  String status = '';
  @override
  void initState() {
    super.initState();

    FlutterLebo.initLBSdk("19153", "c048766a845d69e4a7d8e58c2fc869a1", "19142",
        "f4b4304041043db4c9a3d2bb760a7f90");
    FlutterLebo.getServicesList((value) {
      setState(() {
        tvList = value;
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          children: [
            Center(
              child: Container(
                height: 300,
                width: 400,
                child: ListView.separated(
                  itemCount: tvList.length,
                  itemBuilder: (ctx, index) {
                    return GestureDetector(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: <Widget>[
                          Text("设备名称：${tvList[index].name}"),
                          Text("ipAddress：${tvList[index].ipAddress}"),
                        ],
                      ),
                      onTap: () {
                        FlutterLebo.connectToService(
                            tvList[index].ipAddress, tvList[index].name, () {
                          setState(() {
                            status = "连接成功";
                          });
                        }, () {});
                      },
                    );
                  },
                  separatorBuilder: (BuildContext context, int index) =>
                      Container(
                    color: Colors.grey,
                    height: 1,
                  ),
                ),
              ),
            ),
            Text(status),
            ElevatedButton(
                onPressed: () {
                  FlutterLebo.getServicesList((value) {
                    setState(() {
                      tvList = value;
                    });
                  });
                },
                child: Text('search')),
            ElevatedButton(
                onPressed: () {
                  FlutterLebo.play(
                      'https://m.1080p.icu:9145/video/1628801983/25266.m3u8');
                },
                child: Text('播放1')),
            ElevatedButton(
                onPressed: () {
                  FlutterLebo.play(
                      'https://uf.haoyu666.top/uploads%2F20210827%2F%E6%89%AB%E9%BB%91%E9%A3%8E%E6%9A%B4_15.m3u8');
                },
                child: Text('播放2')),
            ElevatedButton(
                onPressed: () {
                  FlutterLebo.pause();
                },
                child: Text('暂停')),
            ElevatedButton(
                onPressed: () {
                  FlutterLebo.resumePlay();
                },
                child: Text('继续播放')),
            ElevatedButton(
                onPressed: () {
                  FlutterLebo.stop();
                },
                child: Text('停止播放'))
          ],
        ),
      ),
    );
  }
}
