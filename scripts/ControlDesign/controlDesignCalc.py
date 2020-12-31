# -*- coding: utf-8 -*-
"""
Created on Tue Dec 29 19:01:12 2020

@author: Peppel
"""
import sympy as sym
#from scipy import signal
import numpy as np

def calc1():
    # creat symbolic parameters
    R_LS=sym.Symbol('R_LS')
    R_B=sym.Symbol('R_B')
    C_S=sym.Symbol('C_S')
    # creat Laplace-operator s
    s=sym.Symbol('s')
    # symbolic values of measured values
    P_meas=sym.Symbol('P_meas')
    T_1_meas=sym.Symbol('T_1_meas')
    
    # creat subsystem-path
    path_1_sub1 = 1/(C_S*s);
    #signal.lti([0],[C_S]);
    path_2_sub1 = 1/R_LS;
    
    # open-loop
    G_0_sub1 = path_1_sub1 *path_2_sub1;
    # closed-loop
    G_W_sub1 = path_1_sub1/(1+G_0_sub1);
    
    # closed-loop
    path_1_sub2 = 1/R_B;
    G_0_sub2 = path_1_sub2 *G_W_sub1;
    G_W_sub2 = G_0_sub2/(1+G_0_sub2);
    
    # get the numerator and denum of the plant
    numden = sym.fraction(sym.simplify(G_W_sub2));
    num = sym.poly(numden[0],s).coeffs();
    den = sym.poly(numden[1],s).coeffs();
    
    # normalization the coefficient
    numNormcoef = num/(np.array(den[len(den)-1]))
    denNormcoef = den/(np.array(den[len(den)-1]))
            
    # set up the equations
    Eqn_1=numNormcoef[0]-P_meas;
    Eqn_2=denNormcoef[0]-T_1_meas;
    
    # build list of equations and symbols
    Eqn_sys = [Eqn_1,Eqn_2];
    symbols = [R_B,R_LS];
    
    # solve the equation system
    Result = sym.nonlinsolve(Eqn_sys,symbols).args[0];
    
    # show the calculated parameters as a function of the measured values
    calc_R_B=Result[0];
    calc_R_LS=Result[1];
    #print(calc_R_B);
    
    outList=[calc_R_B,calc_R_LS,numNormcoef,denNormcoef];
    return outList;

def calcburnerPID(T_sensor,normalizationFaktor,bandwidthDiv):
    
    # checking input
    if (True!=isinstance(T_sensor,float)):
        # error
        print('T_sensor must be a float')
        return -1;
    if (True!=isinstance(normalizationFaktor,float)):
        # error
        print('normalizationFaktor must be a float')
        return -1;
    if (True!=isinstance(bandwidthDiv,float)):
        # error
        print('bandwidth must be a float')
        return -1;
       
    # calculating the parameters for an PI-controller
    w_d = 1/(T_sensor*bandwidthDiv);            # controller bandwidth

    # serial
    K_PS = T_sensor*w_d/normalizationFaktor;    # Proportional-gain 
    w_IS = 1/T_sensor;                          # Integrator-base frequency [rad/s]
    
    # parallel
    K_PP_B = K_PS;                              # Proportional-gain [xx/rad]
    K_IP_B = K_PS*w_IS;                         # Integrator-gain [xx/rads]
       
    # output
    outList=[K_PP_B,K_IP_B];
    return outList;