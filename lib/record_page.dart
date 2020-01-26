import 'package:flutter/material.dart';
import 'package:audio_recorder/audio_recorder.dart';
import 'package:path_provider/path_provider.dart';
import 'tracking_page.dart';
import 'package:downloads_path_provider/downloads_path_provider.dart';
class RecordPage extends StatefulWidget {
  RecordPage({Key key}) : super(key: key);

  // This widget is the home page of your application. It is stateful, meaning
  // that it has a State object (defined below) that contains fields that affect
  // how it looks.

  // This class is the configuration for the state. It holds the values (in this
  // case the title) provided by the parent (in this case the App widget) and
  // used by the build method of the State. Fields in a Widget subclass are
  // always marked "final".

  final String title='Record';

  @override
  __RecordPageState createState() => __RecordPageState();
}

class __RecordPageState extends State<RecordPage> {
  bool _task = false;
  String _status = "Awaiting Recording";


  record(String filename) async {
    bool hasPermissions = await AudioRecorder.hasPermissions;
    if (hasPermissions) {
      bool isRecording = await AudioRecorder.isRecording;

      if (isRecording) {
        Recording recording = await AudioRecorder.stop();
        setState(() {
          // This call to setState tells the Flutter framework that something has
          // changed in this State, which causes it to rerun the build method below
          // so that the display can reflect the updated values. If we changed
          // _counter without calling setState(), then the build method would not be
          // called again, and so nothing would appear to happen.

          _task = !_task;
          _status = "Path : ${recording.path},  Format : ${recording
              .audioOutputFormat},  Duration : ${recording
              .duration},  Extension : ${recording.extension},";
        });
      }
      else {
        String appDocPath = (await DownloadsPathProvider.downloadsDirectory).path+"/"+filename+".wav";

//        await AudioRecorder.start(path: '/storage/emulated/0/Download/', audioOutputFormat: AudioOutputFormat.AAC);
        await AudioRecorder.start(
            path: appDocPath, audioOutputFormat: AudioOutputFormat.WAV);
        setState(() {
          // This call to setState tells the Flutter framework that something has
          // changed in this State, which causes it to rerun the build method below
          // so that the display can reflect the updated values. If we changed
          // _counter without calling setState(), then the build method would not be
          // called again, and so nothing would appear to happen.
          _task = !_task;
        });
      }
    }
  }

  Future<bool> isRecording() async{
    bool flag=await AudioRecorder.isRecording;

    return flag;
  }

  Future<String> createAlertDialog(BuildContext context){
    TextEditingController customController= TextEditingController();
    
    return showDialog(context: context, builder: (context){
      return AlertDialog(
        title: Text("File Name?"),
        content: TextField(
          controller: customController,
        ),
        actions: <Widget>[
          MaterialButton(
            elevation: 5.0,
            child: Text("Submit"),
            onPressed: (){Navigator.of(context).pop(customController.text.toString());
            },
          )
        ],
      );
    });
  }
  @override
  Widget build(BuildContext context) {
    // This method is rerun every time setState is called, for instance as done
    // by the _incrementCounter method above.
    //
    // The Flutter framework has been optimized to make rerunning build methods
    // fast, so that you can just rebuild anything that needs updating rather
    // than having to individually change instances of widgets.
    return Scaffold(
      appBar: AppBar(
        // Here we take the value from the MyHomePage object that was created by
        // the App.build method, and use it to set our appbar title.
        title: Text(widget.title),
      ),
      body: Center(
        // Center is a layout widget. It takes a single child and positions it
        // in the middle of the parent.

        child: Column(
          // Column is also a layout widget. It takes a list of children and
          // arranges them vertically. By default, it sizes itself to fit its
          // children horizontally, and tries to be as tall as its parent.
          //
          // Invoke "debug painting" (press "p" in the console, choose the
          // "Toggle Debug Paint" action from the Flutter Inspector in Android
          // Studio, or the "Toggle Debug Paint" command in Visual Studio Code)
          // to see the wireframe for each widget.
          //
          // Column has various properties to control how it sizes itself and
          // how it positions its children. Here we use mainAxisAlignment to
          // center the children vertically; the main axis here is the vertical
          // axis because Columns are vertical (the cross axis would be
          // horizontal).
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(
              _status,
            ),
            Text(
              (_task == true) ? 'Recording' : 'Stopped Recording',
              style: Theme
                  .of(context)
                  .textTheme
                  .display1,
            ),
            new FlatButton(
              child: Text("Track"),
              onPressed: () {
                Navigator.of(context).push(MaterialPageRoute<Null>(builder: (BuildContext context){
                  return new TrackingPage();
                }));
              },
            ),

          ],
        ),

      ),
      floatingActionButton: FloatingActionButton(
        onPressed: (){
          if(_task){
            record("sample_text");
          }
          else{
            createAlertDialog(context).then((onValue){
              record(onValue);
            });
          }

        },
        tooltip: 'Record',
        child: Icon(Icons.record_voice_over),
      ), // This trailing comma makes auto-formatting nicer for build methods.

    );
  }

}