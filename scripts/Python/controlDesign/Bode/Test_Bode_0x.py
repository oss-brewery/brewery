# import
#from pylab import *
from scipy import signal
#import matplotlib.pyplot as plt
import numpy as np
from Bode.Bode import Bode_plot

   
#%matplotlib inline
#%matplotlib auto
#%matplotlib qt


# Code for testing
# PT2-Glied
D=200
omega_1=100
TOPI=2*np.pi
system_2 = signal.lti([1], [1/(pow(omega_1*TOPI,2)),2*D/(omega_1*TOPI),1])
system_3 = signal.lti([1/(pow(omega_1*TOPI,2)),2*D/(omega_1*TOPI),1],[1] )


##Bode_plot(system_2,100,1e5)
Bode_plot(system_2,omegamin='auto',omegamax='auto')



#### PT1-Glied
##system = signal.lti([1], [1/(4000*pi),1])
##f = logspace(1, 5)
##w = 2 * pi * f
##w, mag, phase = signal.bode(system,w)
##
##plt.figure(1)
##plt.semilogx(f,mag)
##plt.grid()
##
##
#### PT2-Glied
##D=0.2
##omega_1=1000
##system_1 = signal.lti([1], [1/((omega_1*2*pi)*(omega_1*2*pi)),2*D/(omega_1*2*pi),1])
##f_1 = logspace(1, 5)
##w_1 = 2 * pi * f_1
##w_1, mag_1, phase_1 = signal.bode(system_1,w_1)
##
##
#### Ticks
##yt=np.arange(0,-90.1,-15)
##yt_1=np.arange(0,-180.1,-30)
##
##fig, axs = plt.subplots(2, 1, constrained_layout=True)
##axs[0].semilogx(f,mag)
##axs[0].grid(True, which = "both", linestyle = "-")
##axs[1].semilogx(f,phase)
##axs[1].grid(True, which = "both", linestyle = "-")
##axs[1].yaxis.set_ticks(yt)
##
##fig_1, axs_1 = plt.subplots(2, 1, constrained_layout=True)
##axs_1[0].semilogx(f_1,mag_1)
##axs_1[0].semilogx(f,mag)
##axs_1[0].grid(True, which = "both", linestyle = "-")
##axs_1[1].semilogx(f_1,phase_1)
##axs_1[1].grid(True, which = "both", linestyle = "-")
##axs_1[1].yaxis.set_ticks(yt_1)


