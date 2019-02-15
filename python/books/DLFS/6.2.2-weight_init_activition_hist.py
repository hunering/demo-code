# coding: utf-8
import numpy as np
import matplotlib.pyplot as plt
from collections import OrderedDict
from utils import img_show, load_mnist, sigmoid, relu

x = np.random.randn(1000, 100)
activition = []
layer_num = 5
node_num = 100
activition_func = relu

for i in range(layer_num):
  if i != 0:
    x = activition[i-1]

  #w = np.random.randn(node_num, node_num)
  #w = np.random.randn(node_num, node_num)*0.01
  #w = np.random.randn(node_num, node_num)/np.sqrt(node_num) # for sigmoid function
  w = np.random.randn(node_num, node_num) *np.sqrt(2) /np.sqrt(node_num) # for ReLU function

  z = x.dot(w)
  a = activition_func(z)
  activition.append(a)

for i, a in enumerate(activition):
  plt.subplot(1, len(activition), i+1 )
  plt.title(str(i+1) + "-layer")
  plt.hist(a.flatten(), 30, range=(0,1))

plt.show()