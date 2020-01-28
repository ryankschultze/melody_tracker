package jpj7.com.ryan.jpj7;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Helper {

	public Helper() {
		
	}
	
	public void arrayToFile(Object arr[], String file) {		
		try {
			BufferedWriter bw=new BufferedWriter(new FileWriter(new File(file)));
			for(int i=0; i<arr.length;i++) {
				bw.write(arr[i].toString()+" ");
			}
			bw.write("\n");
			bw.close();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public ArrayList<Double> arrayFromFile(String file) throws NumberFormatException, IOException{
		BufferedReader br=new BufferedReader(new FileReader(new File(file)));
		ArrayList<Double> data=new ArrayList<Double>();
		String line;
		while((line=br.readLine())!=null) {
			StringTokenizer st=new StringTokenizer(line);
			Double element=Double.parseDouble(st.nextToken());
			data.add(element);
		}
		br.close();
		return data;
		
	}
	public ArrayList<Double> groundTruthFromFile(String file) throws NumberFormatException, IOException{
		BufferedReader br=new BufferedReader(new FileReader(new File(file)));
		ArrayList<Double> data=new ArrayList<Double>();
		String line;
		System.out.println("Fetching ground truth...");
		while((line=br.readLine())!=null) {
			StringTokenizer st=new StringTokenizer(line);
			double time=Double.parseDouble(st.nextToken());
			Double element=Double.parseDouble(st.nextToken());
			System.out.print(element);
			data.add(element);
		}
		br.close();
		return data;

	}
	public void arrayToFile(double arr[], String file) {		
		try {
			BufferedWriter bw=new BufferedWriter(new FileWriter(new File(file)));
			for(int i=0; i<arr.length;i++) {
				bw.write(arr[i]+" ");
			}
			bw.write("\n");
			bw.close();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public void arrayToFile(float arr[], String file) {
		try {
			BufferedWriter bw=new BufferedWriter(new FileWriter(new File("/storage/emulated/0/Download/Phone/Tracks/Original/signal.txt")));
			for(int i=0; i<arr.length;i++) {
				bw.write(arr[i]*32767.0+" ");
			}
			bw.write("\n");
			bw.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	
	public void arrayToFile(Complex arr[], String file) {		
		try {
			BufferedWriter bw=new BufferedWriter(new FileWriter(new File(file)));
			for(int i=0; i<arr.length;i++) {
				bw.write(arr[i].re+"\n");
			}
//			bw.write("\n");
			bw.close();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void arrayToFile2D(Object arr[][], String file) {
		try {
			BufferedWriter bw=new BufferedWriter(new FileWriter(new File(file)));
			for(int i=0; i<arr.length;i++) {
				for(int j=0; j<arr[j].length;j++)
					bw.write(arr[i].toString()+" ");
			}
			bw.write("\n");
			bw.close();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void arrayToFile2D(double arr[][], String file) {
		try {
			BufferedWriter bw=new BufferedWriter(new FileWriter(new File(file)));
			for(int i=0; i<arr.length;i++) {
				for(int j=0; j<arr[j].length;j++)
					bw.write(arr[i][j]+" ");
			}
			bw.write("\n");
			bw.close();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Complex[] cArrayFromFile(String file) {
		ArrayList<Complex> data=new ArrayList<Complex>();

		try {
			BufferedReader br=new BufferedReader(new FileReader(new File(file)));
			String line;
			while((line=br.readLine())!=null) {
//			System.out.println(line);
				StringTokenizer st=new StringTokenizer(line);
				Float e1=Float.parseFloat(st.nextToken());
				Float e2=Float.parseFloat(st.nextToken());
				Complex c=new Complex(e1,e2);
				data.add(c);
			}
			br.close();
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cListToArr(data);
	}

	private Complex[] cListToArr(ArrayList<Complex> data) {
		Complex[] cArray=new Complex[data.size()];
		for(int i=0; i<data.size(); i++) {
			cArray[i]=data.get(i);
		}
		return cArray;
	}
}
