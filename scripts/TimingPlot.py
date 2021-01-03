# -*- coding: utf-8 -*-
"""
Spyder Editor

This is a temporary script file.

plot timing
"""
import numpy as np
import matplotlib.pyplot as plt

timingIntervalProcent = 3.3;

TimeIntervalls = 20;

a=np.linspace(0,TimeIntervalls,TimeIntervalls+1);
x=a*0;
t=a*0;
b=((a+1)%3.3)-1;

i=0;
while i < len(b):
     
    if (b[i]<=0):
       x[i]=1;   
       
    i += 1
    
fig, ax = plt.subplots()
ax.set_title('using span_where')
ax.axhline(0, color='black', lw=2)

for rr in a:
    ax.axvline(rr, color='red', lw=2)

plt.scatter(a,x);
plt.scatter(a,(t-0.05));
plt.grid(1)
plt.show
