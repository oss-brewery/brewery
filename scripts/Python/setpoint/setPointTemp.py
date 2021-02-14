# -*- coding: utf-8 -*-
"""
Created on Sun Jan 17 14:39:04 2021

@author: Peppel
"""
import numpy as np
import matplotlib.pyplot as plt

def main(intervals,accAreas,timeSecondsAreas,fileName):

    # Vars
    AREAS = len(accAreas)
    
    # Allocation
    tempDot2=np.zeros(AREAS*intervals+1)    # [K/s^2]
    tempDot1=np.zeros(AREAS*intervals+1)    # [K/s]
    temp=np.zeros(AREAS*intervals+1)        # [K]
    time=np.zeros(AREAS*intervals+1)        # [s]
    
    i = 0
    tempDot1TimeOld = 0
    while i < AREAS:
        # Creat accelaration vector --> delta delta Temp
        tempDot2[intervals*i:intervals*(i+1)+1]=accAreas[i]
    
        # Creat time vector
        tempDot1TimeNew = timeSecondsAreas[i] + tempDot1TimeOld
        time[intervals*i:intervals*(i+1)+1]=np.linspace(tempDot1TimeOld,tempDot1TimeNew,intervals+1)
        tempDot1TimeOld = tempDot1TimeNew
    
        # Integration from tempDot2 --> tempDot1
        n = 1
        deltaT = timeSecondsAreas[i]/intervals
        while n < intervals:
            tempDot1[i*intervals+n]=(tempDot1[i*intervals+n-1]
                                      +tempDot2[i*intervals+n-1]*deltaT)
            n += 1
    
        tempDot1[(i+1)*intervals]=(tempDot1[(i+1)*intervals-1]
                                    +tempDot2[(i+1)*intervals-1]*deltaT)
    
        # Integration from tempDot1 --> temp
        k = 1
        deltaT = timeSecondsAreas[i]/intervals
        while k < intervals:
            temp[i*intervals+k]=temp[i*intervals+k-1]+tempDot1[i*intervals+k-1]*deltaT
            k += 1
    
        temp[(i+1)*intervals]=temp[(i+1)*intervals-1]+tempDot1[(i+1)*intervals-1]*deltaT
    
        i += 1
    
    
    fig, ax = plt.subplots(3,1)
    ax[0].set_title('Temperatur- Target')
    ax[1].set_title('Speed of temperature - Target')
    ax[2].set_title('Accelaration of temperature - Target')
    #ax.ax-hline(0, color='black', lw=2)
    
    ax[0].plot(time,temp)
    ax[1].plot(time,tempDot1)
    ax[2].plot(time,tempDot2)
    
    # label
    ax[0].set_xlabel('Time')
    ax[0].set_ylabel('°C')
    ax[1].set_xlabel('Time')
    ax[1].set_ylabel('°C/s')
    ax[2].set_xlabel('Time')
    ax[2].set_ylabel('°C/s²')
    
    for x in ax:
        x.grid(True)
    
    plt.tight_layout()
    plt.show()
    
    # save plot as ''
    if isinstance(fileName,str):
        plt.savefig(fname=fileName)
    else:
        print('file was not saved')

if __name__ == '__main__':
    # Const
    intervals = 100
    accAreas = np.array([0.0,10,0,-10,0.0])
    timeSecondsAreas = np.array([1,0.1,1,0.1,1])
    main(intervals,accAreas,timeSecondsAreas)