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
  private Audio song;
  private ArrayList<Double> cur_cont;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    GeneratedPluginRegistrant.registerWith(this);

    new MethodChannel(getFlutterView(), CHANNEL1).setMethodCallHandler((methodCall, result) -> {
      String file=methodCall.argument("path");
      String name=methodCall.argument("name");
      int v_range=methodCall.argument("v_range");
//      String v_range=methodCall.argument("v_range");
      Log.d("MyApp","Method call: "+ methodCall.method);
      if (methodCall.method.equals("getContour")){
        Log.d("MyApp","Valid method call...");
        song=new Audio(file,name);
//        int vr=Integer.parseInt(v_range);
        ArrayList<Double> contour=song.track(v_range);
//        ArrayList<Double> contour=song.track();
        cur_cont=contour;
        song.printContour(cur_cont);
        result.success(contour);
      }
      else if(methodCall.method.equals("printContour")){
        Log.d("MyApp","Valid method call...");
        result.success(song.printContour(cur_cont));
//        result.success(contour);
      }
      else if(methodCall.method.equals("getContour_vad")){
        Log.d("MyApp","Valid method call...");
        song=new Audio(file,name);
        cur_cont=song.track_with_vad(v_range);
//        song.printContour(cur_cont);
        result.success(cur_cont);
//        result.success(contour);
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
