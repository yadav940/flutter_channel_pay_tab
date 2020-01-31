// Copyright 2014 The Flutter Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class PlatformChannel extends StatefulWidget {
  @override
  _PlatformChannelState createState() => _PlatformChannelState();
}

class _PlatformChannelState extends State<PlatformChannel> {
  static const MethodChannel methodChannel =
      MethodChannel('samples.flutter.io/battery');
  static const EventChannel eventChannel =
      EventChannel('samples.flutter.io/charging');

  String _batteryLevel = 'Battery level: unknown.';
  String _chargingStatus = 'Battery status: unknown.';

  Future<void> _getBatteryLevel() async {
    String batteryLevel;
    try {
      final int result = await methodChannel.invokeMethod('getBatteryLevel',);
      batteryLevel = 'Battery level: $result%.';
    } on PlatformException {
      batteryLevel = 'Failed to get battery level.';
    }
    setState(() {
      _batteryLevel = batteryLevel;
    });
  }
  Future<void> _getPayTabs() async {
    String batteryLevel;
    try {
      debugPrint("this is dart getPayTabs");
      final Map result = await methodChannel.invokeMethod('getPayTabs');
      debugPrint("trasiction data $result");
    } on PlatformException {
      debugPrint("trasiction data Failed");
      batteryLevel = 'Failed to get battery level.';
    }
    /*setState(() {
      //_batteryLevel = batteryLevel;
    });*/
  }
  Future<void> _getAddTowNum(int a,int b) async {
    String resultNum;
    try {
      List list=List();
      list.add(a);
      list.add(b);
      final int result = await methodChannel.invokeMethod('getAdd',list);
      resultNum = 'Battery level: $result.';
    } on PlatformException {
      resultNum = 'Failed to get battery level.';
    }
    setState(() {
      _batteryLevel = resultNum;
    });
  }

  @override
  void initState() {
    super.initState();
    eventChannel.receiveBroadcastStream().listen(_onEvent, onError: _onError);
  }

  void _onEvent(Object event) {
    setState(() {
      _chargingStatus =
          "Battery status: ${event == 'charging' ? '' : 'dis'}charging.";
    });
  }

  void _onError(Object error) {
    setState(() {
      _chargingStatus = 'Battery status: unknown.';
    });
  }

  @override
  Widget build(BuildContext context) {
    return Material(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: <Widget>[
          Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              Text(_batteryLevel, key: const Key('Battery level label')),
              Padding(
                padding: const EdgeInsets.all(16.0),
                child: RaisedButton(
                  child: const Text('Refresh'),
                  onPressed: _getBatteryLevel,
                ),
              ),
              Padding(
                padding: const EdgeInsets.all(16.0),
                child: RaisedButton(
                  child: const Text('Add Num'),
                  onPressed: (){
                    _getAddTowNum(5,30);
                  },
                ),
              ),
              Padding(
                padding: const EdgeInsets.all(16.0),
                child: RaisedButton(
                  child: const Text('PayTabs'),
                  onPressed: (){
                    _getPayTabs();
                  },
                ),
              ),
            ],
          ),
          Text(_chargingStatus),
        ],
      ),
    );
  }
}

void main() {
  runApp(MaterialApp(home: PlatformChannel()));
}
