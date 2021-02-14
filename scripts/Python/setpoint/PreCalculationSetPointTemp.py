# -*- coding: utf-8 -*-
"""
Created on Sun Jan 17 20:09:17 2021

@author: Peppel
"""

import sympy as sym
import setPointTemp as spt
import numpy as np

subsList=[('V_L',1),('V_M',1.2),('dS',40)]
fileName='TrajcetorieTemperature.svg'

def preSymCalc(subsListIn):
    deltaT1,deltaT2,deltaT3=sym.symbols('dT1,dT2,dT3')
    velocityLimit,velocityMaximum=sym.symbols('V_L,V_M')
    deltaPosition=sym.Symbol('dS')
    accDeltaT2=sym.Symbol('acc_dT2')

    Eqn_0=deltaT1*velocityLimit-deltaPosition
    Eqn_1=2*deltaT2+deltaT3-deltaT1
    Eqn_2=(deltaT3+deltaT2)*velocityMaximum-deltaPosition
    Eqn_3=(velocityMaximum/deltaT2)-accDeltaT2

    # build the equation system
    Eqn = [Eqn_0, Eqn_1, Eqn_2, Eqn_3]
    symbols = [deltaT1, deltaT2, deltaT3, accDeltaT2]

    # solve the equation system
    Result = sym.nonlinsolve(Eqn,symbols).args[0]

    ResultNum=Result.subs(subsListIn)

    [dT1Num,dT2Num,dT3Num,accdT2Num]=ResultNum
    return [float(dT1Num),float(dT2Num),float(dT3Num),float(accdT2Num)]

def main(fileNameIn):
    [dT1Num,dT2Num,dT3Num,accdT2Num]=preSymCalc(subsList)
    del dT1Num
   
    # Const
    intervals = 1000
    accAreas = np.array([0.0,accdT2Num,0,-accdT2Num,0.0])
    timeSecondsAreas = np.array([1,dT2Num,dT3Num,dT2Num,1])
    spt.main(intervals,accAreas,timeSecondsAreas,fileNameIn)

if __name__ == '__main__':
    main(fileName)
