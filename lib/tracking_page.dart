import 'package:downloads_path_provider/downloads_path_provider.dart';
import 'package:flutter/material.dart';
import 'package:audio_recorder/audio_recorder.dart';
import 'package:jpj7/metric_page.dart';
import 'package:path_provider/path_provider.dart';
import 'dart:io';
import 'package:flutter/services.dart';
import 'package:file_picker/file_picker.dart';

class TrackingPage extends StatefulWidget {
  TrackingPage({Key key}) : super(key: key);

  // This widget is the home page of your application. It is stateful, meaning
  // that it has a State object (defined below) that contains fields that affect
  // how it looks.

  // This class is the configuration for the state. It holds the values (in this
  // case the title) provided by the parent (in this case the App widget) and
  // used by the build method of the State. Fields in a Widget subclass are
  // always marked "final".

  final String title='Track';

  @override
  __TrackingPageState createState() => __TrackingPageState();
}

class __TrackingPageState extends State<TrackingPage> {
  static const platform= const MethodChannel("com.ryan.jpj7/track");


  bool _task = false;
  int  _state = 0;
  List _contour;
  bool _loading = false;
  String _fileName;
  String _file_path;
  String _file_name;
  String _path;
  Map<String, String> _paths;
  String _extension;
  bool _loadingPath = false;
  bool _multiPick = false;
  bool _hasValidMime = false;
  FileType _pickingType= FileType.AUDIO;
  TextEditingController _controller = new TextEditingController();

  @override
  void initState() {
    super.initState();
    _controller.addListener(() => _extension = _controller.text);
  }


  void _openFileExplorer() async {
    if (_pickingType != FileType.CUSTOM || _hasValidMime) {
      setState(() => _loadingPath = true);
      try {
        if (_multiPick) {
          _path = null;
          _paths = await FilePicker.getMultiFilePath(
              type: _pickingType, fileExtension: _extension);
        } else {
          _paths = null;
          _path = await FilePicker.getFilePath(
              type: _pickingType, fileExtension: _extension);
        }

      } on PlatformException catch (e) {
        print("Unsupported operation" + e.toString());
      }
      if (!mounted) return;
      setState(() {
        _state=1;
        _loadingPath = false;
        _fileName = _path != null
            ? _path
            .split('/')
            .last
            : _paths != null ? _paths.keys.toString() : '...';
      });
    }
  }


  void _track() async {



    if(_state!=0 && _state!=2){
      setState(() {
        _state=2;
      });

      await _getContour().then((List contour){
        setState(() {
          _state=3;
          _contour=contour;

        });
      });
    }

   }

  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      home: new Scaffold(
        appBar: new AppBar(
          title: const Text('Choose audio'),
        ),
        body: new Center(
            child: new Padding(
              padding: const EdgeInsets.only(left: 10.0, right: 10.0),
              child: new SingleChildScrollView(
                child: new Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: <Widget>[

                    new Padding(
                      padding: const EdgeInsets.only(top: 20.0, bottom: 20.0),
                      child: new RaisedButton(
                        onPressed: () => _openFileExplorer(),
                        child:new Text("Open File Explorer")
                      ),
                    ),
                    new Builder(
                      builder: (BuildContext context) => _loadingPath
                          ? Padding(
                          padding: const EdgeInsets.only(bottom: 5.0),
                          child: const CircularProgressIndicator())
                          : _path != null || _paths != null
                          ? new Container(
                            padding: const EdgeInsets.only(bottom: 5.0),
                            height: MediaQuery.of(context).size.height * 0.20,
                            child: new Scrollbar(
                              child: new ListView.separated(
                                itemCount: _paths != null && _paths.isNotEmpty
                                    ? _paths.length
                                    : 1,
                                itemBuilder: (BuildContext context, int index) {
                                  final bool isMultiPath =
                                      _paths != null && _paths.isNotEmpty;
                                  final String name =
                                      (isMultiPath
                                          ? _paths.keys.toList()[index]
                                          : _fileName ?? '...');
                                  final path = isMultiPath
                                      ? _paths.values.toList()[index].toString()
                                      : _path;
                                  _file_path=path.toString();
                                  _file_name=_fileName;

                                  return new ListTile(
                                    title: new Text(
                                      path,
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
                      new RaisedButton(

                          child: setupTrackingButtonChild(),
                          onPressed: () =>_track()
                      ),

                   new RaisedButton(

                        // ignore: sdk_version_ui_as_code
                        child: setupPrintButtonChild(),
                        onPressed: () {
                          Navigator.of(context).push(MaterialPageRoute<Null>(builder: (BuildContext context){
                            return new MetricPage();
                          }));
                        },
                    ),
                  ],
                ),
              ),
            )),
      ),
    );
  }

  Future<List> _getContour() async{
    String file=_fileName;
    List contour;

    try{
      print(_fileName);
      contour=await platform.invokeMethod("getContour",<String,dynamic>{
        'name':_file_name,
        'path':_file_path,
      });
    } catch(e){
      print(e);
    }
    return contour;
  }

  Widget setupTrackingButtonChild() {

    if (_state == 1 || _state==0) {
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

        return new Text(
        "View Results",
        style: const TextStyle(
        color: Colors.black,
        fontSize: 16.0,
      )
      );


  }
}