package jpj7.com.ryan.jpj7;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.event.meta.TimeSignature;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PianoWriter {


    Bitmap piano_roll;
    ArrayList<Integer> contour;
    ArrayList<Integer> gt;
    boolean hasTruth=false;
    String filename;

    public PianoWriter(ArrayList<Integer> contour,String filename){
        this.filename=filename;
        piano_roll=Bitmap.createBitmap(contour.size(),84,Bitmap.Config.ARGB_8888);
        for(int i=0; i<contour.size(); i++){
            for (int j=0; j<84; j++){
                piano_roll.setPixel(i,j, Color.WHITE);
            }
        }
        this.contour=contour;
    }
    public PianoWriter(ArrayList<Integer> contour, ArrayList<Integer> ground_truth, String filename){
        this.filename=filename;
        piano_roll=Bitmap.createBitmap(contour.size(),84,Bitmap.Config.ARGB_8888);
        for(int i=0; i<contour.size(); i++){
            for (int j=0; j<84; j++){
                piano_roll.setPixel(i,j, Color.WHITE);
            }
        }
        hasTruth=true;
        this.gt=ground_truth;
        this.contour=contour;
    }
    private int getColorWithAlpha(int color, float ratio) {
        int newColor = 0;
        int alpha = Math.round(Color.alpha(color) * ratio);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        newColor = Color.argb(alpha, r, g, b);
        return newColor;
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    public String write_image(){
        int w = piano_roll.getWidth(), h = piano_roll.getHeight();
        int scale=12;
        // create the binary mapping
        System.out.println(gt.size());
        System.out.println(contour.size());
        for(int i=0; i<piano_roll.getWidth(); i++){

//            Log.d("MyApp", "write_image: not sure whats up. contour value:"+contour.get(i).doubleValue());
            piano_roll.setPixel(i, 3,Color.LTGRAY);
            piano_roll.setPixel(i, 15,Color.LTGRAY);
            piano_roll.setPixel(i, 27,Color.LTGRAY);
            piano_roll.setPixel(i, 39,Color.BLUE);
            piano_roll.setPixel(i, 51,Color.LTGRAY);
            piano_roll.setPixel(i, 63,Color.LTGRAY);
            piano_roll.setPixel(i, 75,Color.LTGRAY);
            if(contour.get(i).doubleValue()>piano_roll.getHeight() ||contour.get(i).doubleValue()<=0 ) {
                Log.d("MyApp", "write_image: Note out of range:" + contour.get(i).doubleValue());
                contour.set(i, 1);
            }
            else{
                piano_roll.setPixel(i, (int) contour.get(i).doubleValue() - 1, Color.BLACK);
                if (i<gt.size()){
                    if(hasTruth && gt.get(i)>21){

                        System.out.println("GT: "+(int)gt.get(i)+"\tC:"+((int)contour.get(i)-1));
//                        System.out.println(contour.get(i));
                        if((int) gt.get(i).doubleValue()==(int)contour.get(i).doubleValue()-1)
                        {
                            piano_roll.setPixel(i, (int) gt.get(i).doubleValue() , Color.YELLOW);
                        }
                        else{
                            piano_roll.setPixel(i, (int) gt.get(i).doubleValue() , Color.RED);
                        }
                    }
                }




            }
        }
        int min= Collections.min(contour)-3;
        int max= Collections.max(contour)+3;
        if(min<0)
            min=0;
        System.out.println("Min Note:"+min+"\nMax Note:"+max);
        System.out.println("Score Size:"+piano_roll.getHeight());

        File p_roll= new File("/storage/emulated/0/Download/Phone/Piano_Rolls/"+filename+".png");
        String pathname="/storage/emulated/0/Download/Phone/Piano_Rolls/"+filename+".png";


        Bitmap score=piano_roll.createScaledBitmap(piano_roll, piano_roll.getWidth(), (int)piano_roll.getHeight()*10, false);
        score=this.mirrorBitmap(score);

        for(int i=0; i<score.getHeight(); i+=10){
            for(int j=0; j<score.getWidth(); j++){
                score.setPixel(j,i,Color.GRAY);

            }
        }



        Bitmap piano=this.buildPiano(score.getHeight());
        piano=piano.createScaledBitmap(piano,piano.getWidth()*3,piano.getHeight(),false);
        System.out.println("Min Note:"+min+"\nMax Note:"+max);
        System.out.println("Score Size:"+piano_roll.getHeight());

        //add grid



        piano_roll=combineImages(piano,score);

        System.out.println("Piano and score successfully combined. Width="+piano_roll.getWidth()+"\nHeight="+piano_roll.getHeight());

//        piano_roll=Bitmap.createScaledBitmap(piano_roll, piano_roll.getWidth()*scale, piano_roll.getWidth()*scale/2, false);

        for(int i=piano.getWidth(); i<=piano_roll.getWidth(); i+=piano.getWidth()){
            for(int j=0; j<piano_roll.getHeight(); j++){
                piano_roll.setPixel(i,j,Color.RED);
            }
        }
        min=min*10;
        max=max*10;
        System.out.println("Min Note:"+min+"\nMax Note:"+max);
        System.out.println("Score Size:"+piano_roll.getHeight());


//        if(min+max==piano_roll.getHeight()){
//            max=((piano_roll.getHeight()-min));
//        }
//        System.out.println("Max + Min:"+min+max);
        Bitmap cropped=Bitmap.createBitmap(piano_roll, 0,min,piano_roll.getWidth(), (max-min));
        //Crop score




        try (FileOutputStream out = new FileOutputStream(p_roll)) {

            cropped.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored

        } catch (IOException e) {
            e.printStackTrace();
        }
        return pathname;
    }

    private Bitmap buildPiano(int height) {

        Bitmap octave=buildOctave();
        Bitmap piano = Bitmap.createBitmap(octave.getWidth(), octave.getHeight()*7, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(piano);
        comboImage.drawBitmap(octave, 0f, 0f, null);
        for(int i=0; i<7; i++){
            comboImage.drawBitmap(octave, 0f, i*octave.getHeight(), null);
        }
        float scale=((float)(height)/(float)(octave.getHeight()));
        piano=Bitmap.createScaledBitmap(piano,octave.getWidth()*(2),height,false);
        Log.d("MyApp", "buildPiano: \nPiano Height is"+piano.getHeight()+"\nPiano Height is"+piano.getHeight()+"\nScale="+scale);
        return piano;
    }

    private Bitmap buildOctave(){
        int height=7*4;
        int white_keyWidth=4;
        int white_keyLength=white_keyWidth*6;
        int black_keyLength=white_keyLength/2;
        Bitmap piano=Bitmap.createBitmap(white_keyLength+1,height,Bitmap.Config.ARGB_8888);

        for(int i=0; i<height; i++){
            for(int j=0; j<(white_keyLength+1); j++){
                if(i%4==0){
                    piano.setPixel(j,i,Color.BLACK);
                }
                else{
                    if(j==white_keyLength){
                        piano.setPixel(j,i,Color.BLACK);
                    }
                    piano.setPixel(j,i,Color.WHITE);
                }

            }
        }

        for(int i=0; i<height; i++){
            if (i>=0 && i<=1)
            {
                for(int j=0; j<black_keyLength; j++)
                {
                    piano.setPixel(j,i,Color.BLACK);
                }

            }
            if (i>=3 && i<=5)
            {
                for(int j=0; j<black_keyLength; j++)
                {
                    piano.setPixel(j,i,Color.BLACK);
                }

            }
            if (i>=11 && i<=13)
            {
                for(int j=0; j<black_keyLength; j++)
                {
                    piano.setPixel(j,i,Color.BLACK);
                }

            }
            if (i>=15 && i<=17)
            {
                for(int j=0; j<black_keyLength; j++)
                {
                    piano.setPixel(j,i,Color.BLACK);
                }

            }
            if (i>=23 && i<=25)
            {
                for(int j=0; j<black_keyLength; j++)
                {
                    piano.setPixel(j,i,Color.BLACK);
                }

            }
            if (i>=27 && i<=28)
            {
                for(int j=0; j<black_keyLength; j++)
                {
                    piano.setPixel(j,i,Color.BLACK);
                }

            }

        }
        File octave= new File("/storage/emulated/0/Download/octave.png");
        try (FileOutputStream out = new FileOutputStream(octave)) {

            piano.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored

        } catch (IOException e) {
            e.printStackTrace();
        }
        return piano;
    }




    public Bitmap combineImages(Bitmap c, Bitmap s) { // can add a 3rd parameter 'String loc' if you want to save the new image - left some code to do that at the bottom
        Bitmap cs = null;

        int  height =c.getHeight();
        int width = c.getWidth()+s.getWidth();


        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, c.getWidth(), 0f, null);

        // this is an extra bit I added, just incase you want to save the new image somewhere and then return the location
    /*String tmpImg = String.valueOf(System.currentTimeMillis()) + ".png";

    OutputStream os = null;
    try {
      os = new FileOutputStream(loc + tmpImg);
      cs.compress(CompressFormat.PNG, 100, os);
    } catch(IOException e) {
      Log.e("combineImages", "problem combining images", e);
    }*/

        return cs;
    }

    public Bitmap combineVerticalImages(Bitmap c, Bitmap s) { // can add a 3rd parameter 'String loc' if you want to save the new image - left some code to do that at the bottom
        Bitmap cs = null;

        int  height =c.getHeight();
        int width = c.getWidth()+s.getWidth();


        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, c.getWidth(), 0f, null);

        // this is an extra bit I added, just incase you want to save the new image somewhere and then return the location
    /*String tmpImg = String.valueOf(System.currentTimeMillis()) + ".png";

    OutputStream os = null;
    try {
      os = new FileOutputStream(loc + tmpImg);
      cs.compress(CompressFormat.PNG, 100, os);
    } catch(IOException e) {
      Log.e("combineImages", "problem combining images", e);
    }*/

        return cs;
    }

    @TargetApi(Build.VERSION_CODES.DONUT)
    public static Bitmap mirrorBitmap(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.preScale(1.0f, -1.0f);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        return newBitmap;
    }
    public void write_midi(String filename){
        // 1. Create some MidiTracks
        MidiTrack tempoTrack = new MidiTrack();
        MidiTrack noteTrack = new MidiTrack();

// 2. Add events to the tracks
// Track 0 is the tempo map
        TimeSignature ts = new TimeSignature();
        ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION);

        Tempo tempo = new Tempo();
        tempo.setBpm(228);

        tempoTrack.insertEvent(ts);
        tempoTrack.insertEvent(tempo);

// Track 1 will have some notes in it
        final int NOTE_COUNT = 80;

        for(int i = 0; i < NOTE_COUNT; i++)
        {
            int channel = 0;
            int pitch = 1 + i;
            int velocity = 100;
            long tick = i * 480;
            long duration = 120;

            noteTrack.insertNote(channel, pitch, velocity, tick, duration);
        }

// 3. Create a MidiFile with the tracks we created
        List<MidiTrack> tracks = new ArrayList<MidiTrack>();
        tracks.add(tempoTrack);
        tracks.add(noteTrack);

        MidiFile midi = new MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks);

// 4. Write the MIDI data to a file
        File output = new File("exampleout.mid");
        try
        {
            midi.writeToFile(output);
        }
        catch(IOException e)
        {
            System.err.println(e);
        }

    }

    public void write_truth(ArrayList<Integer> ground_truth) {
        int w = piano_roll.getWidth(), h = piano_roll.getHeight();
        int scale=12;
        // create the binary mapping
        System.out.println(gt.size());
        System.out.println(contour.size());
        for(int i=0; i<piano_roll.getWidth(); i++){

//            Log.d("MyApp", "write_image: not sure whats up. contour value:"+contour.get(i).doubleValue());

            if(contour.get(i).doubleValue()>piano_roll.getHeight() ||contour.get(i).doubleValue()<=0 ) {
                Log.d("MyApp", "write_image: Note out of range:" + contour.get(i).doubleValue());
                contour.set(i, 1);
            }
            else{
                piano_roll.setPixel(i, (int) contour.get(i).doubleValue() - 1, Color.BLACK);

                if (i<gt.size()){
                    if(hasTruth && gt.get(i)>21){

//                        System.out.println(gt.get(i));
                        piano_roll.setPixel(i, (int) gt.get(i).doubleValue() - 1, Color.RED);
                    }
                }

            }
        }
        int min= Collections.max(contour)-3;
        int max= Collections.max(contour)+3;
        if(min<0)
            min=0;
        System.out.println("Min Note:"+min+"\nMax Note:"+max);
        System.out.println("Score Size:"+piano_roll.getHeight());

        File p_roll= new File("/storage/emulated/0/Download/Phone/Piano_Rolls/"+filename+".png");
        String pathname="/storage/emulated/0/Download/Phone/Piano_Rolls/"+filename+".png";


        Bitmap score=piano_roll.createScaledBitmap(piano_roll, piano_roll.getWidth(), (int)piano_roll.getHeight()*10, false);
        score=this.mirrorBitmap(score);

        for(int i=0; i<score.getHeight(); i+=10){
            for(int j=0; j<score.getWidth(); j++){
                score.setPixel(j,i,Color.GRAY);

            }
        }



        Bitmap piano=this.buildPiano(score.getHeight());
        piano=piano.createScaledBitmap(piano,piano.getWidth()*3,piano.getHeight(),false);
        System.out.println("Min Note:"+min+"\nMax Note:"+max);
        System.out.println("Score Size:"+piano_roll.getHeight());

        //add grid



        piano_roll=combineImages(piano,score);




        int newHeight=piano_roll.getWidth()*scale/2;
        int oldHeight=piano_roll.getHeight();
        float height_scale=(float)newHeight/(float) oldHeight;
        piano_roll=piano_roll.createScaledBitmap(piano_roll, piano_roll.getWidth()*scale, piano_roll.getWidth()*scale/2, false);

//        Bitmap resized_p_roll=Bitmap.createBitmap(piano_roll, 0,min,piano_roll.getWidth(), Collections.max(contour)+3);
        min= (int) (min*height_scale);
        max= (int) (max*height_scale);
        for(int i=piano.getWidth()*scale; i<=piano_roll.getWidth(); i+=piano.getWidth()*scale){
            for(int j=0; j<piano_roll.getHeight(); j++){
                piano_roll.setPixel(i,j,Color.RED);
            }
        }

        System.out.println("Min Note:"+min+"\nMax Note:"+max);
        System.out.println("Score Size:"+piano_roll.getHeight());
//        Bitmap cropped=Bitmap.createBitmap(piano_roll, 0,min,piano_roll.getWidth(), (max+min));
        //Crop score




        try (FileOutputStream out = new FileOutputStream(p_roll)) {

            piano_roll.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
