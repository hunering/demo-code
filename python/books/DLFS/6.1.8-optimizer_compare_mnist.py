# coding: utf-8
import numpy as np
import matplotlib.pyplot as plt
from collections import OrderedDict
from utils import img_show, load_mnist
from two_layer_net import TwoLayerNet

from optimizer import *

(x_train, y_train), (x_test, y_test) = load_mnist(normalize=True, one_hot_label=True)

optimizers = OrderedDict()
optimizers["SGD"] = SGD()
optimizers["Momentum"] = Momentum()
optimizers["AdaGrad"] = AdaGrad()
optimizers["Adam"] = Adam()


inters_num = 2000
train_size = x_train.shape[0]
batch_size = 100

markers = {"SGD": "o", "Momentum": "x", "AdaGrad": "s", "Adam": "D"}
train_loss_list = {}

for key in optimizers:
  net = TwoLayerNet(input_size=784, hidden_size=50, output_size=10)
  optimizer = optimizers[key]
  train_loss_list[key] = []
  for i in range(inters_num):
      batch_mask = np.random.choice(train_size, batch_size)
      x_batch = x_train[batch_mask]
      y_batch = y_train[batch_mask]

      grads = net.gradient(x_batch, y_batch)      
      optimizer.update(net.params, grads)

      loss = net.loss(x_batch, y_batch)
      train_loss_list[key].append(loss)
  
  x = np.arange(inters_num)
  plt.plot(x, train_loss_list[key], marker=markers[key], markevery=100, label=key)

plt.xlabel("iterations")
plt.ylabel("loss")
#plt.ylim(0, 5)
plt.legend()
plt.show()
