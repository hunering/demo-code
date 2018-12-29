import pandas as pd
import matplotlib.pyplot as plt

info = pd.DataFrame({'a':[1,2,3,4], 'b':[5,6,7,8], 'c':[9,10,11,12]})
info.plot()
plt.show()