package jpj7.com.ryan.jpj7;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import be.tarsos.dsp.mfcc.*;
public class VAD {


    int FEATURES=60;
    float OBSERVATION_WINDOW=0.8f;
    float CLASSIFICATION_WINDOW=0.2f;
    float FS=22050.0f;
    int NCEP=30;
    int NFILT=30;
    float PAD= (int) (FS*(OBSERVATION_WINDOW-CLASSIFICATION_WINDOW)/2.0f);
    int C_FRAMES= (int) (this.FS*this.CLASSIFICATION_WINDOW);
    int O_FRAMES= (int) (this.FS*this.OBSERVATION_WINDOW);
    int FFT_SIZE= this.O_FRAMES;
    Helper help=new Helper();
    public VAD(float[] signal){

        float[] padded=pad(signal);
//        ArrayList<float[]> mfccs=new ArrayList<float[]>();
//        final MFCC mfcc = new MFCC((int) (OBSERVATION_WINDOW*FS), FS, 30, 530, 0, 11025);
//        float[] buffer=new float[O_FRAMES];
//
//        for(int i=0; i< C_FRAMES; i++){
//            float[] features=new float[60];
//
//            for(int j=0; j<O_FRAMES; j++){
//                buffer[j]=padded[j+i*C_FRAMES];
//            }
//            float[]coeffs=mfcc.cepCoefficients(buffer);
//            for(int j=0; j<30; j++){
//                features[j]=coeffs[j];
//            }
//
//            mfccs.add(features);
//        }
//
//        mfccs=addDeltas(mfccs);
    }

    private ArrayList<float[]> addDeltas(ArrayList<float[]> mfccs) {
        int NUMFRAMES=mfccs.size();
        ArrayList<float[]> features=new ArrayList<float[]>();
        int N=2;
        float denominator=12;
        float[] padding=new float[60];
        for(int i=0; i<padding.length; i++){
            padding[i]=0f;
        }
        features.add(padding);
        features.add(padding);
        for(int i=0; i< mfccs.size(); i++){
            features.add(mfccs.get(i));
        }
        features.add(padding);
        features.add(padding);

        for(int t=2; t<NUMFRAMES+2; t++){

            float[] feat=features.get(t);
            float[] feat_p1=features.get(t+1);
            float[] feat_p2=features.get(t+2);
            float[] feat_m1=features.get(t-1);
            float[] feat_m2=features.get(t-1);
            for(int i=30; i<60; i++){
                System.out.println(mfccs.get(t-2)[i]+"=>");
                mfccs.get(t-2)[i]=(feat_p1[i-30]-feat_m1[i-30])-(feat_p2[i-30]-feat_m2[i-30]);
                System.out.println(mfccs.get(t-2)[i]+"\n");
            }
        }
        return mfccs;
    }

    private float[] pad(float[] signal) {
        System.out.println("Initial Signal Length:"+signal.length+"" +
                "\nAfter padding:"+PAD*2+signal.length);
        float[]padded=new float[(int) (PAD*2+signal.length)];

        for(int i=0; i<padded.length; i++){
            if(i<PAD){
                padded[i]=0f;
            }
            else if(i<PAD+signal.length){
                padded[i]=signal[(int) (i-PAD)];
            }
            else {
                padded[i]=0f;
            }
        }
        return padded;
    }

    public ArrayList<Double> mask(ArrayList<Double> contour, String s) {
        System.out.println("Beginning mask...\n");
        System.out.println("OG contour");
        for(int i=0; i<contour.size(); i++){
            System.out.println(contour.get(i));
        }
        ArrayList<Double> masked=new ArrayList<Double>();
        try {
            ArrayList<Double> voice_vector=help.arrayFromFile(s);
            for(int i=0; i<contour.size(); i++){
                if(voice_vector.get(i)>0.5){
                    masked.add(contour.get(i));
                }
                else{
                    masked.add(0.0);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }



        System.out.println("Finished Masking!");
        System.out.println("New contour");
        for(int i=0; i<masked.size(); i++){
            System.out.println(masked.get(i));
        }
        return masked;



    }
}
