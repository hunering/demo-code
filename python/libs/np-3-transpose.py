import numpy as np

a = np.arange(16).reshape((2, 2, 4))
print(a.strides)

print(a)

print(a.transpose(1,0,2))