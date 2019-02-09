import numpy as np
import matplotlib.pyplot as plt
from keras.datasets import mnist
from keras.utils import to_categorical
from two_layer_net import TwoLayerNet

net = TwoLayerNet(input_size=784, hidden_size=50, output_size=10)
(x_train, y_train), (x_test, y_test) = mnist.load_data()
x_train = x_train.reshape(x_train.shape[0], 28*28)
y_train = to_categorical(y_train, num_classes=10)

x_test = x_test.reshape(x_test.shape[0], 28*28)
y_test = to_categorical(y_test, num_classes=10)

train_loss_list = []
inters_num = 10000
train_size = x_train.shape[0]
batch_size = 100
learning_rate = 0.1

for i in range(inters_num):
  batch_mask = np.random.choice(train_size, batch_size)
  x_batch = x_train[batch_mask]
  y_batch = y_train[batch_mask]

  grad = net.gradient(x_batch, y_batch)

  for key in ['W1', 'b1', 'W2', 'b2']:
    net.param[key] -= learning_rate * grad[key]

  loss = net.loss(x_batch, y_batch)
  train_loss_list.append(loss)

plt.plot(train_loss_list)
plt.show()