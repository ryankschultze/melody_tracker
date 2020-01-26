package jpj7.com.ryan.jpj7;

import android.os.Bundle;

import java.util.ArrayList;

import io.flutter.Log;
import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends FlutterActivity {
  private static final String CHANNEL1="com.ryan.jpj7/track";
  private static final String CHANNEL2="com.ryan.jpj7/metric";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    GeneratedPluginRegistrant.registerWith(this);

    new MethodChannel(getFlutterView(), CHANNEL1).setMethodCallHandler((methodCall, result) -> {
      String file=methodCall.argument("path");
      String name=methodCall.argument("name");
      Log.d("MyApp","Method call: "+ methodCall.method);
      if (methodCall.method.equals("getContour")){
        Log.d("MyApp","Valid method call...");
        Audio song=new Audio(file,name);
        ArrayList<Double> contour=song.track();
        song.printContour(contour);
        result.success(contour);
      }
      else{
        Log.d("MyApp","Incorrect method call...\nFilename:"+file);

      }
    });
    new MethodChannel(getFlutterView(), CHANNEL2).setMethodCallHandler((methodCall, result) -> {

      String gt=methodCall.argument("groundTruth");
      String res=methodCall.argument("result");
      Log.d("MyApp","Method call: "+ methodCall.method);
      if (methodCall.method.equals("getMetrics")){
        Log.d("MyApp","Valid method call...\nGround Truth File: "+gt+"\nResult File:"+res);
        Converter c = new Converter();
        result.success(c.compareFiles(gt,res));
      }
      else{
        Log.d("MyApp","Incorrect method call...");
      }
    });
  }
}
