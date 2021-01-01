# import
from scipy import signal
import matplotlib.pyplot as plt
import numpy as np
from control import matlab as matlab


# define a functions

# Bode-diagram with magnitude and phase
def Bode_plot(system,omegamin='auto',omegamax='auto',**kwargs):
    """
    old lib --> lti function
    e.g.    
        Bode_plot(signal.lti) 
    
    Parameters
    ----------
    system : TYPE
        matlab.sys / list of matlab.sys
    omegamin : int, optional
        :math:`\omega` The default is 'auto'.
    omegamax : int, optional
        The default is 'auto'.
    **kwargs : TYPE
        DESCRIPTION.
    Returns
    -------
    None.
    
    """
    # bode-diagram of a system

    # const
    n_omega = 1000  # number of points - frequency omega

    # var
    degRes = 15

    # allocation
    omegamin_temp=np.ones(2)*np.inf
    omegamax_temp=np.ones(2)*0

    # read kwargs
    for key, value in kwargs.items():
        if key == 'omegamin':
            omegamin = value
        elif key == 'omegamax':
            omegamax = value
        elif key == 'degRes':
            degRes = value
        

    if omegamin != 'auto':
        # Manual fmin
        omegamin=omegamin
    else:
        # Auto fmin
        if system.zeros.size > 0:
            omegamin_temp[0] = min(abs(system.zeros))
        elif system.poles.size > 0:
            omegamin_temp[1] = min(abs(system.poles))

        # one decade before the lowest frequency
        omegamin=min(omegamin_temp)/10

    if omegamax != 'auto':
        # Manual fmax
        omegamin=omegamin
    else:
        # Auto fmax
        if system.zeros.size > 0:
            omegamax_temp[0] = max(abs(system.zeros))
        elif system.poles.size > 0:
            omegamax_temp[1] = max(abs(system.poles))

        # one decade after the highest frequency
        omegamax=max(omegamax_temp)*10

    # variables
    omega = np.logspace(np.log10(omegamin),np.log10(omegamax),num=n_omega)

    # bode-diagram of system
    omega, mag, phase = signal.bode(system,omega)

    # plot
    fig, axs = plt.subplots(2, 1, constrained_layout=True)
    axs[0].semilogx(omega,mag)
    axs[0].grid(True, which = "both", linestyle = "-")
    axs[1].semilogx(omega,phase)
    axs[1].grid(True, which = "both", linestyle = "-")

    # ticks Y-axis

    # magnitude

    # phase
    # from degMin to degMax with degRes width
    degMin = np.sign(axs[1].get_ylim()[0]) * (abs(axs[1].get_ylim()[0]/degRes))*degRes
    degMax = np.sign(axs[1].get_ylim()[1]) * np.floor(abs(axs[1].get_ylim()[1]/degRes))*degRes
    yt=np.arange(degMax,degMin,-degRes)
    axs[1].yaxis.set_ticks(yt)

    plt.show()
    return

def Bode_plot_save(syslist,fileName=-1,omegamin='auto',omegamax='auto',**kwargs):
    """
    Plot Bode-diaragm of system(s).\n
    Can be saved in a file.

    Parameters
    ----------
    syslist : control.xferfcn.TransferFunction
        list of systems or system
    fileName : TYPE, optional
        Name of the saved file. The default is -1.
    omegamin : TYPE, optional
        :math:`\omega_{min}` for ploting. The default is 'auto'.
    omegamax : TYPE, optional
        :math:`\omega_{max}` for ploting. The default is 'auto'.
    **kwargs : TYPE
        DESCRIPTION.

    Examples
    ----------
    >>>     Bode_plot_save(syslist,fileName='Test.svg',labellist=['label1','label2'])


    .. todo::

    """

    
    # bode-diagram of a system

    # const
    n_omega = 1000  # number of points - frequency omega

    # var
    degRes = 15

    # allocation
    omegamin_temp=np.ones(2)*np.inf
    omegamax_temp=np.ones(2)*0
    omega_list=np.zeros(2)

    # search for all systems
    #system=systems
    if isinstance(syslist,list):
        # ok
        None
    elif matlab.issys(syslist):
        # list with size 1
        systemp=syslist
        del syslist
        syslist = []
        syslist.append(systemp)
    else:
        print('Error, you need an system or a list of systems for argument "syslist"')
        return

    # temp
    system = syslist[0]

    
    fig, axs = plt.subplots(2, 1, constrained_layout=True)
   

    # read kwargs
    for key, value in kwargs.items():
        if key == 'omegamin':
            omegamin = value
        elif key == 'omegamax':
            omegamax = value
        elif key == 'degRes':
            degRes = value
        elif key == 'fileName':
            fileName = value
        elif key == 'labellist':
            labellist = value
            
            # make a labellist
            if isinstance(labellist,list):
                # ok
                None
            elif isinstance(labellist,str):
                # list with size 1
                labeltemp=labellist
                del labellist
                labellist = []
                labellist.append(labeltemp)
            else:
                print('Error, you need a label or a list of labels for argument "labellist"')
                return
            

    # Omega min value
    if omegamin != 'auto':
        # Manual fmin
        omegamin=omegamin
    else:
        # Auto fmin
        if system.zero().size > 0:
            omegamin_temp[0] = min(abs(system.zero()))
        elif system.pole().size > 0:
            omegamin_temp[1] = min(abs(system.pole()))

        # one decade before the lowest frequency
        omegamin=min(omegamin_temp)/10

    # Omega max value
    if omegamax != 'auto':
        # Manual fmax
        omegamax=omegamax
    else:
        # Auto fmax
        if system.zero().size > 0:
            omegamax_temp[0] = max(abs(system.zero()))
        elif system.pole().size > 0:
            omegamax_temp[1] = max(abs(system.pole()))

        # one decade after the highest frequency
        omegamax=max(omegamax_temp)*10

    # variables
    omega_list[0] = omegamin
    omega_list[1] = omegamax

    # bode-diagram of system
    i = 0
    BodeList=[]
    
    while (i<len(syslist)):
        BodeData = matlab.bode(syslist[i],Hz=False,omega_limits=omega_list,omega_num=n_omega,Plot=False)
        # BodeData ist a tuple --> (mag,phase,omega)
        BodeList.append(BodeData)
        
        #plot
        axs[0].semilogx(BodeList[i][2],20*np.log10(BodeList[i][0]))
        axs[0].grid(True, which = "both", linestyle = "-")
        axs[1].semilogx(BodeList[i][2],BodeList[i][1]*180/np.pi)
        axs[1].grid(True, which = "both", linestyle = "-") 
        
        i=i+1  

    # phase
    # from degMin to degMax with degRes width
    degMin = np.sign(axs[1].get_ylim()[0]) * (abs(axs[1].get_ylim()[0]/degRes))*degRes
    degMax = np.sign(axs[1].get_ylim()[1]) * np.floor(abs(axs[1].get_ylim()[1]/degRes))*degRes
    yt=np.arange(degMax,degMin,-degRes)
    # ticks Y-axis
    axs[1].yaxis.set_ticks(yt)
    
    # legend   
    axs[0].legend(labellist,loc='best')    
    # x-Label
    axs[0].set_xlabel('frequency (rad/s)')
    axs[1].set_xlabel('frequency (rad/s)')    
    # y-Label
    axs[0].set_ylabel('Magnitude (dB)')
    axs[1].set_ylabel('Phase (deg)')
    
    # save plot as ''
    if isinstance(fileName,str):
        plt.savefig(fname=fileName)
    else:
        print('file was not saved')

    plt.show()
    return 


