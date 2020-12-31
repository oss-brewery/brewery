# -*- coding: utf-8 -*-
"""
Created on Thu Dec 31 13:43:19 2020

@author: Peppel
"""
# calculation of both PI-controller | burner & boiler

import numpy as np
import controlDesignCalc as cdc
#from scipy import signal
#import matplotlib.pyplot as plt
#from Bode.Bode import Bode_plot

# calc burner
#############
T_sensor = 0.6;             # [s] time constant
bandwidthDiv = 2.0;         # [] factor for reducing controller bandwidth
MaxNorm = 100.0;            # [0..100] procent
MaxBurnerTemp = 1200.0;     # [K] maximum burner temparture

# calc temp values from parameters
normalizationFaktor = MaxBurnerTemp /MaxNorm;   # norming factor

# calc parameters for PI-controller
[K_PP_burner,K_IP_burner]=cdc.calcburnerPID(T_sensor, normalizationFaktor, bandwidthDiv);


#calc boiler
############
RadiusBottom = 0.3;                             # [m] radius 
HeatTransferCoefficientBottom = 1000.0;         # [W/(m^2*K)] coeff
m_water = 55;                                   # [Kg] weigth 
m_cereals = 40;                                 # [Kg] weigth
 
# calc temp values from parameters
ABottom = np.pi*RadiusBottom^2;                             # [m^2] area 
R_th_Bottom=1/(ABottom*HeatTransferCoefficientBottom);      # [W/K] thermal resistor bottom 

# thermal capacity
c_water = 4184;                             # [J/(kg*K)] thermal capacity water 
c_cereals = 1500;                           # [J/(kg*K)] thermal capacity cereals
R_th_Air_Sud = 0.0005;                      # [W/K] thermal resistor air to sud 

C_cap_water = c_water*m_water;              # [J/K] capacity
C_cap_cereals = c_cereals*m_cereals;        # [J/K] capacity
C_cap_Sud = C_cap_water + C_cap_cereals;    # [J/K] capacity

# Feedforward controle - ambiant air
P_FF_controle = R_th_Bottom/R_th_Air_Sud;   # [] gain
