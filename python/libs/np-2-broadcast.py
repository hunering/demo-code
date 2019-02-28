import numpy as np

a = np.array([1, 2, 3])
b = np.array([[4,5,6],[7,8,9]])
print(f"a.shape:{a.shape}")
print(f"b.shape:{b.shape}")

c = a*b
print(f"c.shape:{c.shape}, c.ndim:{c.ndim}")