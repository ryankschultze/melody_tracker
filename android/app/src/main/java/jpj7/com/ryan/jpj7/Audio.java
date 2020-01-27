package jpj7.com.ryan.jpj7;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;
import be.tarsos.dsp.resample.Resampler;

public class Audio {
	
	WavFile wavFile;
	float fs;
	double[] signal;
	Complex[] cSignal;
	Helper help=new Helper();
	float[][] SPECTRUM;
	private Tracker t;
	Converter converter=new Converter();
	String name;
	String path;
	String src=" /storage/emulated/0/Download/Phone/Misc/";
	Resampler resampler;
	public Audio(String f, String name) {
		Log.d("MyApp","Constructing Audio...");
		Log.d("MyApp","File: "+f);
		Log.d("MyApp", "File Name: "+name);
		this.path=f;
		this.name=name;
		File file=new File(f);
		this.loadFile(file);
		this.fs=wavFile.getSampleRate();
		resampler=new Resampler(true,0.25,3);
		this.readFile();


	}
	
	public void loadFile(File file) {
		Log.d("MyApp","Loading File...");
		try {
			this.wavFile = WavFile.openWavFile(file);
			Log.d("MyApp","File loaded...");
			this.wavFile.display();
		} catch (IOException | WavFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void readFile() {
		try {
			//Careful Max Integer Size
			this.signal=new double[(int) wavFile.getFramesRemaining()];
			this.wavFile.readFrames(this.signal,  (int) wavFile.getFramesRemaining());

			float[] insig=new float[this.signal.length];
			for(int i=0; i<this.signal.length; i++){

				insig[i]=(float)this.signal[i];
//				Log.d("MyApp",""+insig[i]);
			}
			Log.d("MyApp","Resampling:");
			Log.d("MyApp","Buffer Length:"+insig.length);
			Log.d("MyApp","Resample factor:"+22050.0/this.fs);
			int new_sample_length= (int) ((22050.0/this.fs)*insig.length);
			float[] outsig=new float[new_sample_length];
			Log.d("MyApp","New sample length:"+outsig.length);
			Log.d("MyApp","Processing...");
			this.resampler.process(22050.0/this.fs,insig,0,insig.length,true, outsig,0,outsig.length);
			this.fs=22050;

			Log.d("MyApp","Resample complete.");
			//Convert to complex
			this.cSignal = new Complex[outsig.length];
	        for (int i = 0; i < outsig.length; i++)
	        	this.cSignal[i] = new Complex( outsig[i], 0.0f);

//	        help.arrayToFile(outsig,"/resources/signal.txt");
		} catch (IOException | WavFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	
	public float[][] getSTFT(){


		Complex[] cSeg;
		int c_len=cSignal.length;
		int c_seg_len=2000;


		STFT transform=new STFT(cSignal,fs);
		float[][] spectrum=transform.stft(cSignal);


//		File output=new File("resources/output.txt");
//		BufferedWriter bw=new BufferedWriter(new FileWriter(output));
//		HeatChart hc=new HeatChart(spectrum);
//		System.out.println("Spectrum Length:"+spectrum[0].length);
//		System.out.println("Frequency Bins:"+spectrum.length);
//		
//		for(int i=0; i<spectrum[0].length; i++) {
//			System.out.println(spectrum[0][i]+" ");
//		}
//		System.out.println();
//		System.out.println(spectrum[0].length);
//		for(int i=0; i<spectrum[0].length; i++)
//		{
//			for(int j=spectrum.length-1; j>=0; j--) {
//				System.out.print(spectrum[i][j]+" ");
//				bw.write((spectrum[j][i]+" "));
//			}
//			System.out.println(i);
//			bw.write("\n");
//		}
//		bw.close();
		
//		help.arrayToFile2D(spectrum,"resources/array.txt");
		
//		hc.setTitle("Spectrogram");
//		hc.setXAxisLabel("Time");
//		hc.setYAxisLabel("Frequency");
//		hc.saveToFile(new File("my-chart.png"));
		this.SPECTRUM=spectrum;
		return spectrum;
	}

	public float[][] getSTFT_short(){
		STFT transform=new STFT(cSignal,fs);
		float[][] spectrum=transform.stft_short(cSignal);
		this.SPECTRUM=spectrum;
		return spectrum;
	}
	
	public void printFrequency(int time) {
		for(int i=0; i<this.SPECTRUM[time].length; i++) {
			System.out.println(this.SPECTRUM[time][i]+" ");
		}
	}
	public void printFrequency(int time, int end) {
		for(int i=0; i<end; i++) {
			System.out.println(this.SPECTRUM[time][i]+" ");
		}
		System.out.println();
	}
	public void printFrequency(int time, int start, int end) {
		for(int i=start; i<end; i++) {
			System.out.println(this.SPECTRUM[time][i]+" ");
		}
		System.out.println();
	}
	public void printFrame(int freq) {
		System.out.println("Printing frames at frequency "+freq+"Hz\n");
		for(int i=0; i<this.SPECTRUM.length; i++) {
			System.out.print(this.SPECTRUM[i][freq]+" ");
		}
		System.out.println();
	}
	public void printFrame(int freq, int end) {
		System.out.println("Printing at frequency "+freq+"Hz");
		for(int i=0; i<end; i++) {
			System.out.println(this.SPECTRUM[i][freq]+" ");
		}
		System.out.println();
	}
	public void printFrame(int freq, int start, int end) {
		System.out.println("Printing at frequency "+freq+"Hz");
		for(int i=start; i<end; i++) {
			System.out.println(this.SPECTRUM[i][freq]+" ");
		}
		System.out.println();
	}
	
	public Double[][] readSTFT() throws IOException{
		BufferedReader br=new BufferedReader(new FileReader(new File("resources/spectrum.txt")));
		
		Double[][] spectrum=new Double[613][44100];
		String line;
		for(int i=0; i<44100; i++) {
			line=br.readLine();
			StringTokenizer st=new StringTokenizer(line);
			for(int j=0; j<613; j++) {
				spectrum[j][i]=Double.parseDouble(st.nextToken());
			}
		}
		br.close();
		return spectrum;
	}
	public ArrayList<Double> track() {

		this.t=new Tracker(this.getSTFT());
		ArrayList<Double> contour=t.track();
//		this.t.writeContourToFile(contour);
//		this.writeContour(contour);
//		this.copy();
		return contour;
	}

	public void printContour(ArrayList<Double> contour){
		ArrayList<Integer> piano =converter.convert_contout(contour);
		PianoWriter pw=new PianoWriter(piano,name.substring(0,name.length()-4));
		pw.write_image();
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public void writeContour(ArrayList<Double> contour){

		DecimalFormat df = new DecimalFormat("#.###");
		df.setRoundingMode(RoundingMode.CEILING);
		String newPath=this.path.substring(0,this.path.length()-4)+"_result.txt";

		try {
			File result=new File(newPath);
			result.createNewFile();
			FileOutputStream fOut = new FileOutputStream(result);
			PrintWriter pw = new PrintWriter(fOut);
			for(int i=0; i<contour.size(); i++){
				pw.println(i*0.025+"\t"+contour.get(i));
			}
			pw.flush();
			pw.close();
			fOut.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.i("MyApp", "******* File not found. ");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void copy() {

		String newPath=this.path.substring(0,this.path.length()-4)+"_result.txt";

		try {
			InputStream in = new FileInputStream(this.src+name.substring(0,name.length()-3)+"txt");
			OutputStream out = new FileOutputStream(newPath);
			try {
				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
			} finally {
				out.close();
				in.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
	}
}



 

