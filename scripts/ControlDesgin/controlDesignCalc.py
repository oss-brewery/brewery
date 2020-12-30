# -*- coding: utf-8 -*-
"""
Created on Tue Dec 29 19:01:12 2020

@author: Peppel
"""
import sympy as sym
#from scipy import signal
import numpy as np

# creat symbolic parameters
R_LS=sym.Symbol('R_LS')
R_B=sym.Symbol('R_B')
C_S=sym.Symbol('C_S')
# creat Laplace-operator s
s=sym.Symbol('s')

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

numden = sym.fraction(sym.simplify(G_W_sub2));
num = sym.poly(numden[0],s).coeffs();
den = sym.poly(numden[1],s).coeffs();

numNormcoef = num/(np.array(den[len(den)-1]))
denNormcoef = den/(np.array(den[len(den)-1]))

P_meas=sym.Symbol('P_meas')
T_1_meas=sym.Symbol('T_1_meas')

Eqn_1=numNormcoef[0]-P_meas;
Eqn_2=denNormcoef[0]-T_1_meas;

Eqn_sys = [Eqn_1,Eqn_2];
symbols = [R_B,R_LS];

Result = sym.nonlinsolve(Eqn_sys,symbols).args[0];

calc_R_B=Result[0];
calc_R_LS=Result[1];



#tempNorm = den.coeffs();



# get coeffs of the tf
#[a,b]=sym.numden(G_W_sub2);
#numcoef=sym.coeffs(a,s);
#denumcoef=sym.coeffs(b,s);