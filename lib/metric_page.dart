import 'package:downloads_path_provider/downloads_path_provider.dart';
import 'package:flutter/material.dart';
import 'package:audio_recorder/audio_recorder.dart';
import 'package:path_provider/path_provider.dart';
import 'dart:io';
import 'package:flutter/services.dart';
import 'package:file_picker/file_picker.dart';

class MetricPage extends StatefulWidget {
  MetricPage({Key key}) : super(key: key);

  // This widget is the home page of your application. It is stateful, meaning
  // that it has a State object (defined below) that contains fields that affect
  // how it looks.

  // This class is the configuration for the state. It holds the values (in this
  // case the title) provided by the parent (in this case the App widget) and
  // used by the build method of the State. Fields in a Widget subclass are
  // always marked "final".

  final String title='Metrics';

  @override
  __MetricPageState createState() => __MetricPageState();
}

class __MetricPageState extends State<MetricPage> {
  static const platform= const MethodChannel("com.ryan.jpj7/metric");


  bool _task = false;
  int  _state = 0;
  List _accuracies;
  bool _loading = false;
  String _fileName;
  String _groundTruth="Select File";
  String _result="Select File";
  String _path;
  String _path_result;
  String _path_gt;
  Map<String, String> _paths_result;
  Map<String, String> _paths_gt;
  Map<String, String> _paths;
  String _extension;
  bool _loadingPath = false;
  bool _multiPick = false;
  bool _hasValidMime = false;
  FileType _pickingType= FileType.ANY;
  TextEditingController _controller = new TextEditingController();

  @override
  void initState() {
    super.initState();
    _controller.addListener(() => _extension = _controller.text);
  }


  void _pickResult() async {
    if (_pickingType != FileType.CUSTOM || _hasValidMime) {
      setState(() => _loadingPath = true);
      try {
        if (_multiPick) {
          _path_result = null;
          _paths_result = await FilePicker.getMultiFilePath(
              type: _pickingType, fileExtension: _extension);
        } else {
          _paths_result = null;
          _path_result = await FilePicker.getFilePath(
              type: _pickingType, fileExtension: _extension);
          setState(() {
            _groundTruth=_path_result;
          });
        }

      } on PlatformException catch (e) {
        print("Unsupported operation" + e.toString());
      }
      if (!mounted) return;
      setState(() {
        _state=1;
        _loadingPath = false;
        _fileName = _path_result != null
            ? _path_result
            .split('/')
            .last
            : _paths_result != null ? _paths_result.keys.toString() : '...';
      });
    }
  }

  void _pickGroundTruth() async {
    if (_pickingType != FileType.CUSTOM || _hasValidMime) {
      setState(() => _loadingPath = true);
      try {
        if (_multiPick) {
          _path_gt = null;
          _paths_gt = await FilePicker.getMultiFilePath(
              type: _pickingType, fileExtension: _extension);
        } else {
          _paths_gt = null;
          _path_gt = await FilePicker.getFilePath(
              type: _pickingType, fileExtension: _extension);
          setState(() {
            _groundTruth=_path_gt;
          });
        }

      } on PlatformException catch (e) {
        print("Unsupported operation" + e.toString());
      }
      if (!mounted) return;
      setState(() {
        _state=1;
        _loadingPath = false;
        _fileName = _path_gt != null
            ? _path_gt
            .split('/')
            .last
            : _paths_gt != null ? _paths_gt.keys.toString() : '...';
      });
    }
  }




  void _getMetrics() async{
    String gt=_groundTruth;
    String res=_result;
    List accuracies;

    try{
      print(_fileName);
      accuracies=await platform.invokeMethod("getMetrics",<String, dynamic>{
          "groundTruth":gt,
          "result":res
      });
      String RCA=accuracies.elementAt(0);
      String RPA=accuracies.elementAt(1);
      String NOS=accuracies.elementAt(2);
      String FA=accuracies.elementAt(3);
      showDialog(
          context: context,
          builder: (context) {
            return AlertDialog(
              title: Text('Performance Metrics'),
              actions: <Widget>[
                Text(

                  "RCA Metric:"+RCA+"%\n"+
                  "RPA Metric:"+RPA+"%\n"+
                  "Note Onset Accuracy:"+NOS+"%\n"+
                  "Voicing False Alarm:"+FA+"%\n",
                  maxLines: null,
                  style: const TextStyle(
                    color: Colors.black,
                    fontSize: 16.0,
                  ),
                ),
                SizedBox(height: 100),
                FlatButton(
                    onPressed: () => Navigator.pop(context),
                    child: Text('Close')),

              ],
            );
          });
    } catch(e){
      showDialog(
          context: context,
          builder: (context) {
            return AlertDialog(
              title: Text('Performance Metrics'),
              actions: <Widget>[
                Text(
                  "Failed to get metrics",
                  style: const TextStyle(
                    color: Colors.black,
                    fontSize: 16.0,
                  ),
                ),
              ],
            );
          });
      print(e);
    }

  }
  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      home: new Scaffold(
        appBar: new AppBar(
          title: const Text('Choose Reference File'),
        ),
        body: new Center(
            child: new Padding(
              padding: const EdgeInsets.only(left: 10.0, right: 10.0),
              child: new SingleChildScrollView(
                child: new Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: <Widget>[
                    new RaisedButton(
                        onPressed: ()=>_getMetrics(),
                        child: new Text("Compare Files")),
                    Row(
                      children: <Widget>[
                        Expanded(
                          child: new RaisedButton(
                              onPressed: () => _pickResult(),
                              child:new Text("Pick Result")
                          ),
                        ),
                        Expanded(
                          child: new RaisedButton(
                              onPressed: () => _pickGroundTruth(),
                              child:new Text("Pick Ground Truth")
                          ),
                        )
                      ],
                    ),
                    Row(
                      children: <Widget>[

                      ],
                    ),
                    SizedBox(height: 50),
                    new Text("Path to ground truth:"),
                    new Builder(
                      builder: (BuildContext context) => _loadingPath
                          ? Padding(
                          padding: const EdgeInsets.only(bottom: 5.0),
                          child: const CircularProgressIndicator())
                          : _path_gt != null || _paths_gt != null
                          ? new Container(
                        padding: const EdgeInsets.only(bottom: 5.0),
                        height: MediaQuery.of(context).size.height * 0.1,
                        child: new Scrollbar(
                            child: new ListView.separated(
                              itemCount: _paths_gt != null && _paths_gt.isNotEmpty
                                  ? _paths_gt.length
                                  : 1,
                              itemBuilder: (BuildContext context, int index) {
                                final bool isMultiPath =
                                    _paths_gt != null && _paths_gt.isNotEmpty;
                                final String name =
                                (isMultiPath
                                    ? _paths_gt.keys.toList()[index]
                                    : _fileName ?? '...');
                                final path = isMultiPath
                                    ? _paths_gt.values.toList()[index].toString()
                                    : _path_gt;
                                _groundTruth=_path_gt;

                                return new ListTile(
                                  title: new Text(
                                    _groundTruth,
                                  ),
                                );
                              },
                              separatorBuilder:
                                  (BuildContext context, int index) =>
                              new Divider(),
                            )),
                      )
                          : new Container(),

                    ),
                    SizedBox(height: 50),
                    new Text("Path to result:"),
                    new Builder(
                      builder: (BuildContext context) => _loadingPath
                          ? Padding(
                          padding: const EdgeInsets.only(bottom: 5.0),
                          child: const CircularProgressIndicator())
                          : _path_result != null || _paths_result != null
                          ? new Container(
                            padding: const EdgeInsets.only(bottom: 5.0),
                            height: MediaQuery.of(context).size.height * 0.1,
                            child: new Scrollbar(
                                child: new ListView.separated(
                                  itemCount: _paths_result != null && _paths_result.isNotEmpty
                                      ? _paths_result.length
                                      : 1,
                                  itemBuilder: (BuildContext context, int index) {
                                    final bool isMultiPath =
                                        _paths_result != null && _paths_result.isNotEmpty;
                                    final String name =
                                    (isMultiPath
                                        ? _paths_result.keys.toList()[index]
                                        : _fileName ?? '...');
                                    final path = isMultiPath
                                        ? _paths_result.values.toList()[index].toString()
                                        : _path_result;
                                    _result=_path_result;

                                return new ListTile(
                                  title: new Text(
                                    _result,
                                  ),
                                );
                              },
                              separatorBuilder:
                                  (BuildContext context, int index) =>
                              new Divider(),
                            )),
                      )
                          : new Container(),

                    ),

                  ],
                ),
              ),
            )),
      ),
    );
  }


  Widget setupTrackingButtonChild() {

    if (_state == 1) {
      return new Text(
        "Start Tracking",
        style: const TextStyle(
          color: Colors.black,
          fontSize: 16.0,
        ),
      );
    } else if (_state == 2) {
      return CircularProgressIndicator();
    } else if(_state==3){
      return new Text(
        "Finished Tracking!",
        style: const TextStyle(
          color: Colors.black,
          fontSize: 16.0,
        ),
      );
    }
  }

  Widget setupPrintButtonChild() {
    if (_state == 3) {
      return new Text(
          "Print Results",
          style: const TextStyle(
            color: Colors.black,
            fontSize: 16.0,
          )
      );

    }
  }


}