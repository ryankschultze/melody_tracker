package jpj7.com.ryan.jpj7;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Converter {

	double TUNING=440.0;
	
	public Converter(double tuning) {
		this.TUNING=tuning;
//		if (! Python.isStarted()) {
//			Python.start(new AndroidPlatform(context));
//		}
	}
	
	public Converter() {
		
	}

    public  ArrayList<Integer> convert_contour(ArrayList<Double> contour) {
		ArrayList<Integer> piano=new ArrayList<Integer>();
		for (int i=0; i<contour.size(); i++){
			if(contour.get(i)<85.0){
				piano.add(1);
			}
			else{
				piano.add(freqToDegree(contour.get(i)));
			}

		}
		return piano;

    }

    public double noteToFreq(int note) {
		return Math.pow(2,(note-49)/12)*this.TUNING;
	}
	
	public String freqToNote(double freq) {
		int degree= (int) Math.round(12*((Math.log(freq/this.TUNING)/Math.log(2)+1e-10))+49);
		return this.getNote(degree);
	}
	
	public int freqToChroma(double freq) {
		return getRawChroma((int) Math.round(12*((Math.log(freq/this.TUNING)/Math.log(2)+1e-10))+49));
	}
	
	public int freqToDegree(double freq) {
		if(freq==0)
			return 0;
		return (int) Math.round(12*((Math.log(freq/this.TUNING)/Math.log(2)+1e-10))+49);
	}
	
	public double quantize(double freq) {
		return noteToFreq((int) Math.round(12*((Math.log(freq/this.TUNING)/Math.log(2)+1e-10))+49));
	}
	
	private String getNote(int degree) {
//		while(degree<0) {
//			degree+=12;
//		}
		int octave=getOctave(degree);
		while(degree>12) {
			degree-=12;
		}
		String note="N/A";
		switch(degree) {
		case 1:
			note= "A("+octave+")";
		case 2:
			note= "A#/Bb("+octave+")";
		case 3:
			note= "B("+octave+")";
		case 4:
			note= "C("+octave+")";
		case 5:
			note= "C#/Db("+octave+")";
		case 6:
			note= "D("+octave+")";
		case 7:
			note= "D#/Eb("+octave+")";
		case 8:
			note= "E("+octave+")";
		case 9:
			note= "F("+octave+")";
		case 10:
			note= "F#/Gb("+octave+")";
		case 11:
			note= "G("+octave+")";
		case 12:
			note= "G#/Ab("+octave+")";

		}
		return note;
		
	}

	private int getOctave(int degree) {
		int octave=0;
		while(degree>12) {
			octave+=1;
			degree-=12;
		}
		return octave;
	}
	
	private int getRawChroma(int degree) {
		
		while(degree>12)
			degree-=12;
		return degree;
	}

	public ArrayList<String> compareFiles(String gt,String res){
		File ground_truth=new File(gt);

		File result=new File(res);
		Log.d("MyApp", "compareFiles: We got the files...");
		double correct=0;
		double non_vocal=0;
		double chroma=0;
		double vocal=0;
		double false_voice=0;
		int lines = 0;
		ArrayList<Double> truths=new ArrayList<Double>();
		ArrayList<Double> records=new ArrayList<Double>();
		Log.d("MyApp", "compareFiles: Token: "+res);
		try{
			BufferedReader greader = new BufferedReader(new FileReader(ground_truth));
			BufferedReader rreader = new BufferedReader(new FileReader((result)));
			String gline="";
			String rline="";
			while ((gline=greader.readLine()) != null) {
				rline=rreader.readLine();
				StringTokenizer gst=new StringTokenizer(gline);
				StringTokenizer rst=new StringTokenizer(rline);
				gst.nextToken();
				rst.nextToken();
				truths.add(Double.parseDouble(gst.nextToken()));
				records.add(Double.parseDouble(rst.nextToken()));
				lines++;
			}



			rreader.close();
			greader.close();
		} catch(IOException e){
			e.printStackTrace();
		}
//		PianoWriter pw=new PianoWriter(convert_contout(truths),"truth");
////		pw.write_image();
//		PianoWriter pw=new PianoWriter(convert_contout(records),"output");
//		pw.write_image();
		for(int i=0; i<lines; i++)
		{
			if(truths.get(i)<85){
				non_vocal+=1;
			}
			else{
				vocal+=1;
			}
			int rchroma=this.freqToChroma(Math.round(records.get(i)));
			int tchroma=this.freqToChroma(Math.round(truths.get(i)));
			if(Math.abs(rchroma-tchroma)<2){
				chroma++;
			}
			if((records.get(i))<=(truths.get(i))*Math.pow(2.0,1.0/12.0) && (records.get(i))>=(truths.get(i))*Math.pow(2.0,-1.0/12.0)){
				correct+=1;
			}
			if(records.get(i)>=85&&truths.get(i)==0){
				false_voice+=1;
			}
		}
		Log.d("MyApp", "compareFiles: We here ");
		Log.d("MyApp", "Correct Chroma: "+chroma);
		Log.d("MyApp", "Correct Pitch: "+correct);
		Log.d("MyApp", "Number of vocal frames: "+vocal);
		Log.d("MyApp", "Number of non-vocal frames: "+non_vocal);
		Double NOS= Double.valueOf(Math.round(getNoteOnsets(truths,records)*10000)/100);
		Double RCA= Double.valueOf(Math.round(chroma/vocal*10000)/100);
		Double RPA= Double.valueOf(Math.round(correct/vocal*10000)/100);
		Double FA= Double.valueOf(Math.round(false_voice/non_vocal*10000)/100);

		ArrayList<String> metrics=new ArrayList<String>();
		metrics.add(RCA.toString());
		Log.d("MyApp", "RCA: "+RCA.toString());
		metrics.add(RPA.toString());
		Log.d("MyApp", "RPA: "+RPA.toString());
		metrics.add(NOS.toString());
		Log.d("MyApp", "NOS: "+NOS.toString());
		metrics.add(FA.toString());
		Log.d("MyApp", "FA: "+FA.toString());
		return metrics;
	}

	Double getNoteOnsets(ArrayList<Double> gt, ArrayList<Double> res){
		double onsets=0.0;
		double caught=0.0;
		for(int i=1;i<gt.size()-1; i++){
			if(gt.get(i-1)==0 && gt.get(i)!=0){
				onsets+=1.0;
				int j=i+1;
				if(freqToChroma(gt.get(j))>freqToChroma(res.get(i))-2 && freqToChroma(gt.get(i))<freqToChroma(res.get(i))+2){
					caught+=1.0;
				}
				else{
					if(gt.get(j)<1) {
						onsets -= 1;
					}
					else if(freqToChroma(gt.get(j))>freqToChroma(res.get(j))-2 && freqToChroma(gt.get(j))<freqToChroma(res.get(j))+2){
						caught+=1.0;
					}

				}
			}

		}





		if(onsets==0)
			return 1.0;
        else
			return caught/onsets;

	}



}
