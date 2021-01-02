# -*- coding: utf-8 -*-
"""
Created on Wed Dec 30 18:45:45 2020

@author: Peppel
"""
# imports
import numpy as np
import controlDesignCalc as cdc
#from scipy import signal
#import sympy as sym
from control import matlab as matlab
import matplotlib.pyplot as plt
from Bode.Bode import Bode_plot_save

##############################################################
## input values
##############################################################

# define the parameters measured
delta_T_boiler = 40;    # [K]
delta_T_burner = 300;   # [K]
TimeConstant = 170;     # [s]
C_S_value = 4e5;        # [J/K]


##############################################################
## do not touch my code
##############################################################
# creat symbolic parameters
R_LS=cdc.sym.Symbol('R_LS')
R_B=cdc.sym.Symbol('R_B')
C_S=cdc.sym.Symbol('C_S')
# creat Laplace-operator s
#s=sym.Symbol('s')
# symbolic values of measured values
P_meas=cdc.sym.Symbol('P_meas')
T_1_meas=cdc.sym.Symbol('T_1_meas')  

# calculat symbols for R_LS and R_B
# R_B is the thermal resistance between the burner and the boiler
# R_LS is the thermal resistance between the boiler and the ambient air
[calc_R_B,calc_R_LS,numNormcoef,denNormcoef]=cdc.calc1()

# calculate the value of R_LS and R_B
P_meas_value = delta_T_boiler/delta_T_burner;   # []
# calculation with substitution
R_B_value = calc_R_B.subs([(P_meas,P_meas_value),(T_1_meas,TimeConstant),(C_S,C_S_value)]);
R_LS_value = calc_R_LS.subs([(P_meas,P_meas_value),(T_1_meas,TimeConstant),(C_S,C_S_value)]);
    
# init list for numerator and denumerator
numNumbercoef=np.array([0.0]);                  
denNumbercoef=np.array([0.0,0.0]); 

# define the numerator and denumerator of the transfer function
numNumbercoef[0] = numNormcoef[0].subs([(R_B,R_B_value),(R_LS,R_LS_value),(C_S,C_S_value)])
denNumbercoef[0] = denNormcoef[0].subs([(R_B,R_B_value),(R_LS,R_LS_value),(C_S,C_S_value)])
denNumbercoef[1] = denNormcoef[1].subs([(R_B,R_B_value),(R_LS,R_LS_value),(C_S,C_S_value)])

# covert to float - maybe a bug - try to fix in future
denNumbercoef[0] = float(denNumbercoef[0]);
denNumbercoef[1] = float(denNumbercoef[1]);

# build the transfer function of the brewery
systemBrewery = matlab.TransferFunction(numNumbercoef,denNumbercoef)

# step response with input 1
[yout,T]=matlab.step(systemBrewery)
# change to meaured input
yout_meas = yout*delta_T_burner;

# plot the step response
plt.plot(T,yout_meas)

# drawing lines for understanding the PT1 step response
########################################################
# asymptote of maximum out value
stepLimit=[numNumbercoef[0]*delta_T_burner]*len(T);
plt.plot(T,stepLimit);

# auxiliary line for measuring the time constant
T_line = np.linspace(0,TimeConstant,len(T));
Line_1_out = T_line*(numNumbercoef[0]*delta_T_burner/TimeConstant);
plt.plot(T_line,Line_1_out);

# point for time constant at maximum value
plt.scatter(max(T_line),max(Line_1_out));
plt.annotate("T1",(max(T_line),max(Line_1_out)))

# adding the Bode-diagram
Bode_plot_save(syslist=systemBrewery,fileName='BreweryBodePlot.svg',labellist=['brewery_PT1'])
