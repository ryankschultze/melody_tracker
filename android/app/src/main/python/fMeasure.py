#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import numpy as np
import Convert

class FMeasure():

    def __init__(self,f1,f2):

        f1 = open(f1, "r")
        f2 = open(f2, "r")

        results = f1.readlines()
        ground_truths = f2.readlines()

        self.result=[]
        self.gt=[]
        for line in results:
            self.result.append(float(line.split()[1].rstrip()))

        for line in ground_truths:
            self. gt.append(float(line.split()[1].rstrip()))

        self.convert=Convert.Convert()
        #        self.result=self.normalizeResult(self.result)
        self.gt=np.interp(np.arange(0, len(self.gt), len(self.gt)/len(self.result)), np.arange(0, len(self.gt)), self.gt)
    #        print("Ground truth length: ",len(self.gt))

    def getRPA(self):



        positives=0
        unvoiced=0
        gtchroma=[]
        resultchroma=[]
        for i in range(0,len(self.gt)-1):
            #            print(self.gt[i]," ",self.result[i])
            if(self.gt[i]==0):
                unvoiced+=1
            elif(self.result[i]==0):
                positives+=0
            elif(self.result[i] < self.gt[i]*np.float_power(2,1/12) and self.result[i] > self.gt[i]*np.float_power(2,-1/12)):
                positives+=1

            if(self.gt[i]!=0):
                gtchroma.append(self.convert.freqToRawChroma(self.gt[i]))
            else:
                gtchroma.append('N')
            if(self.result[i]>0):
                resultchroma.append(self.convert.freqToRawChroma(self.result[i]))
            else:
                resultchroma.append('N')



        #        print("Ground Truth Chroma:\n",self.gt[34:])
        #        print("Result Chroma:\n",self.result[34:])

        return positives/(len(self.gt)-unvoiced)







    def getRCA(self):

        positives=0
        unvoiced=0
        gtchroma=[]
        resultchroma=[]
        for i in range(0,len(self.gt)-1):
            #            print(self.gt[i])
            #            print(self.result[i])
            #            print(self.gt[i]," ",self.result[i])
            if(self.gt[i]==0):
                unvoiced+=1
            elif(self.result[i]==0):
                positives+=0
            elif(self.convert.freqToRawChroma(self.gt[i])>self.convert.freqToRawChroma(self.result[i])-2 and self.convert.freqToRawChroma(self.gt[i])<self.convert.freqToRawChroma(self.result[i])+2):
                positives+=1

            if(self.gt[i]!=0):
                gtchroma.append(self.convert.freqToRawChroma(self.gt[i]))
            else:
                gtchroma.append('N')
            if(self.result[i]>0):
                resultchroma.append(self.convert.freqToRawChroma(self.result[i]))
            else:
                resultchroma.append('N')



        #        print("Ground Truth Chroma:\n",self.gt[34:])
        #        print("Result Chroma:\n",self.result[34:])

        return positives/(len(self.gt)-unvoiced)

    def getFA(self):
        unvoiced=0
        fas=0
        for i in range(0,len(self.gt)-1):
            #            print(self.gt[i])
            #            print(self.result[i])
            #            print(self.gt[i]," ",self.result[i])
            if(self.gt[i]==0&&self.result[i]!=0):
                fas+=1
                unvoiced+=1
            elif(self.gt[i]==0&&self.result[i]==0):
                unvoiced+=1



        #        print("Ground Truth Chroma:\n",self.gt[34:])
        #        print("Result Chroma:\n",self.result[34:])

        return fas/unvoiced

    #        a2=numpy.interp(np.arange(0, len(a), 1.5), np.arange(0, len(a)), a)
    def getNoteOnset(self):
        onsets=0.0
        caught=0.0
        for i in range(1,len(self.gt)-2):
            if(self.gt[i-1]==0 and self.gt[i]!=0):
                onsets+=1.0
                j=i+1

                if(self.convert.freqToRawChroma(self.gt[i])>self.convert.freqToRawChroma(self.result[i])-2 and self.convert.freqToRawChroma(self.gt[i])<self.convert.freqToRawChroma(self.result[i])+2):
                    caught+=1.0
                else:
                    if(self.gt[j]<1):
                        onsets-=1
                    elif(self.convert.freqToRawChroma(self.gt[j])>self.convert.freqToRawChroma(self.result[j])-2 and self.convert.freqToRawChroma(self.gt[j])<self.convert.freqToRawChroma(self.result[j])+2):
                        caught+=1.0

        if onsets==0:
            return 1.0
        else:
            return caught/onsets


    def normalizeResult(self,result):
        #        print(result)
        for i in range(1,len(result)-2):
            window=result[i-1:i+2]
            #            print(window)
            if(self.convert.freqToDegree(window[0])==self.convert.freqToDegree(window[2]) and self.convert.freqToDegree(result[i])!=self.convert.freqToDegree(window[0])):
                result[i]=(window[0])
            else:
                result[i]=result[i]
        #        print(result)
        return result


#file="Contours/Jazz/example1"
#fmeasure=FMeasure(file+".txt",file+"REF.txt")
#print("RCA: ",fmeasure.getRCA(),"%")
#print("RPA: ",fmeasure.getRPA(),"%")
