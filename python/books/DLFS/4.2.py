import numpy as np
from utils import gradient_descent
from keras.utils import to_categorical

def function_2(x):
  return x[0]**2 + x[1]**2

init_x = np.array([1.0, 2.0])
x = gradient_descent(function_2, init_x, lr=0.1, step_num=100)
print(x)

