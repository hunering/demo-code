import numpy as np

a = np.array([1, 2, 3, 4, 5])
print(a)

a = np.arange(10, 20, 2, dtype=np.float64)
print(a)
a[3] = 0
print(a)

print(a[3:])

a = np.zeros(5)
print(a)
a = np.zeros((5, 5))  # create two demensional array
print(a)
a[4][4] = 1
print(a)

a = np.ones(5)
print(a)

a = np.random.rand(5)
print(a)
