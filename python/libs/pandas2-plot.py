import matplotlib.pyplot as plt
import pandas as pd
import numpy as np

s = pd.Series([1,2,3,4,5])
s.plot()
plt.show()

s = pd.Series([1,1,1,2,2,3,4,4,4,4])
s.plot.hist()
plt.show()

s = pd.Series((list(range(10)) + list(range(30)) + list(range(50)) + list(range(70))) *30)
s.plot.hist(bins=5)
plt.show()

s = pd.Series((list(range(10)) + list(range(30)) + list(range(50)) + list(range(70))) *30)
s.plot.density()
plt.show()

s = pd.Series([1,1,1,2,2,3,4,4,4,4])
s.plot.bar()
plt.show()

