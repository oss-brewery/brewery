# -*- coding: utf-8 -*-
"""
Created on Wed Dec 30 18:45:45 2020

@author: Peppel
"""
# imports
import controlDesignCalc as cdc
from scipy import signal
#import sympy as sym
import matplotlib.pyplot as plt
from Bode.Bode import Bode_plot

##############################################################
## input values
##############################################################

# define the parameters measured
delta_T_boiler = 40;    # [K]
delta_T_burner = 300;   # [K]
TimeConstant = 170;     # [s]
C_S_value = 4e5;     # [J/K]


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
numNumbercoef=[0]*1;                  
denNumbercoef=[0]*2;
# define the numerator and denumerator of the transfer function
numNumbercoef[0] = numNormcoef[0].subs([(R_B,R_B_value),(R_LS,R_LS_value),(C_S,C_S_value)])
denNumbercoef[0] = denNormcoef[0].subs([(R_B,R_B_value),(R_LS,R_LS_value),(C_S,C_S_value)])
denNumbercoef[1] = denNormcoef[1].subs([(R_B,R_B_value),(R_LS,R_LS_value),(C_S,C_S_value)])

# covert to float - maybe a bug - try to fix in future
denNumbercoef[1] = float(denNumbercoef[1]);
denNumbercoef[0] = float(denNumbercoef[0]);

# build the transfer function of the brewery
systemBrewery=signal.lti(numNumbercoef,denNumbercoef);

# step response with input 1
[T,yout]=signal.step(systemBrewery)
# change to meaured input
yout_meas = yout*delta_T_burner;

# plot the step response
plt.plot(T,yout_meas)

# adding the Bode-diagram
Bode_plot(systemBrewery,omegamin='auto',omegamax='auto')
