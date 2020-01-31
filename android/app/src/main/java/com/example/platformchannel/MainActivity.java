// Copyright 2014 The Flutter Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.example.platformchannel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;

import com.paytabs.paytabs_sdk.payment.ui.activities.PayTabActivity;
import com.paytabs.paytabs_sdk.utils.PaymentParams;

import java.util.List;

import androidx.annotation.NonNull;
import io.flutter.Log;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.EventChannel.EventSink;
import io.flutter.plugin.common.EventChannel.StreamHandler;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends FlutterActivity {
  private static final String BATTERY_CHANNEL = "samples.flutter.io/battery";
  private static final String CHARGING_CHANNEL = "samples.flutter.io/charging";
  private Result result_globale;

  @Override
  public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
    new EventChannel(flutterEngine.getDartExecutor(), CHARGING_CHANNEL).setStreamHandler(
      new StreamHandler() {
        private BroadcastReceiver chargingStateChangeReceiver;
        @Override
        public void onListen(Object arguments, EventSink events) {
          chargingStateChangeReceiver = createChargingStateChangeReceiver(events);
          registerReceiver(
              chargingStateChangeReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        }

        @Override
        public void onCancel(Object arguments) {
          unregisterReceiver(chargingStateChangeReceiver);
          chargingStateChangeReceiver = null;
        }
      }
    );

    new MethodChannel(flutterEngine.getDartExecutor(), BATTERY_CHANNEL).setMethodCallHandler(
      new MethodCallHandler() {
        @Override
        public void onMethodCall(MethodCall call, Result result) {
          result_globale=result;
          if (call.method.equals("getBatteryLevel")) {
            System.out.println("getBatteryLevel");
            int batteryLevel = getBatteryLevel();
            if (batteryLevel != -1) {
              result.success(batteryLevel);
            } else {
              result.error("UNAVAILABLE", "Battery level not available.", null);
            }
          }else if (call.method.equals("getAdd")){
            int addResult=addTowNub(((List<Integer>)call.arguments).get(0),((List<Integer>)call.arguments).get(1));

            System.out.println("getAdd");
            if (addResult==0){
              result.success(545);
            }else {
              result.success(addResult);
            }

          }else if (call.method.equals("getPayTabs")){

            System.out.println("getPayTabs");

            Intent in = new Intent(getApplicationContext(), PayTabActivity.class);
            in.putExtra(PaymentParams.MERCHANT_EMAIL, "vinod@modistabox.com"); //this a demo account for testing the sdk
            in.putExtra(PaymentParams.SECRET_KEY,"3pOh7a0Cr0T8bhtgDMPnRjLr8iCb2lR6LGnTQovhopqAHTRkGGIx0uZ9MCkPXcVJTjCJH816xsHemzz2ghAgLzqybLo9YkTRDvBl");//Add your Secret Key Here
            in.putExtra(PaymentParams.LANGUAGE,PaymentParams.ENGLISH);
            in.putExtra(PaymentParams.TRANSACTION_TITLE, "Test Paytabs android library");
            in.putExtra(PaymentParams.AMOUNT, 5.0);

            in.putExtra(PaymentParams.CURRENCY_CODE, "BHD");
            in.putExtra(PaymentParams.CUSTOMER_PHONE_NUMBER, "009733");
            in.putExtra(PaymentParams.CUSTOMER_EMAIL, "customer-email@example.com");
            in.putExtra(PaymentParams.ORDER_ID, "123456");
            in.putExtra(PaymentParams.PRODUCT_NAME, "Product 1, Product 2");

//Billing Address
            in.putExtra(PaymentParams.ADDRESS_BILLING, "Flat 1,Building 123, Road 2345");
            in.putExtra(PaymentParams.CITY_BILLING, "Manama");
            in.putExtra(PaymentParams.STATE_BILLING, "Manama");
            in.putExtra(PaymentParams.COUNTRY_BILLING, "BHR");
            in.putExtra(PaymentParams.POSTAL_CODE_BILLING, "00973"); //Put Country Phone code if Postal code not available '00973'

//Shipping Address
            in.putExtra(PaymentParams.ADDRESS_SHIPPING, "Flat 1,Building 123, Road 2345");
            in.putExtra(PaymentParams.CITY_SHIPPING, "Manama");
            in.putExtra(PaymentParams.STATE_SHIPPING, "Manama");
            in.putExtra(PaymentParams.COUNTRY_SHIPPING, "BHR");
            in.putExtra(PaymentParams.POSTAL_CODE_SHIPPING, "00973"); //Put Country Phone code if Postal code not available '00973'

//Payment Page Style
            in.putExtra(PaymentParams.PAY_BUTTON_COLOR, "#d6ba06");

//Tokenization
            in.putExtra(PaymentParams.IS_TOKENIZATION, true);
            startActivityForResult(in, PaymentParams.PAYMENT_REQUEST_CODE);

          }else {
            System.out.println("Not Implemented");
            result.notImplemented();

          }
        }
      }
    );
  }

  private BroadcastReceiver createChargingStateChangeReceiver(final EventSink events) {
    return new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

        if (status == BatteryManager.BATTERY_STATUS_UNKNOWN) {
          events.error("UNAVAILABLE", "Charging status unavailable", null);
        } else {
          boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                               status == BatteryManager.BATTERY_STATUS_FULL;
          events.success(isCharging ? "charging" : "discharging");
        }
      }
    };
  }

  private int getBatteryLevel() {
    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
      return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    } else {
      Intent intent = new ContextWrapper(getApplicationContext()).
          registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
      return (intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100) /
          intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
    }
  }

  private int addTowNub(int a,int b){
    return a+b;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK && requestCode == PaymentParams.PAYMENT_REQUEST_CODE) {
      Log.e("Tag", data.getStringExtra(PaymentParams.RESPONSE_CODE));
      Log.e("Tag", data.getStringExtra(PaymentParams.TRANSACTION_ID));
      if (data.hasExtra(PaymentParams.TOKEN) && !data.getStringExtra(PaymentParams.TOKEN).isEmpty()) {
        Log.e("Tag", data.getStringExtra(PaymentParams.TOKEN));
        Log.e("Tag", data.getStringExtra(PaymentParams.CUSTOMER_EMAIL));
        Log.e("Tag", data.getStringExtra(PaymentParams.CUSTOMER_PASSWORD));
      }
      result_globale.success(data);
    }
  }
}

