import numpy as np
import matplotlib.pyplot as plt
from keras.datasets import mnist
from keras.utils import to_categorical
from utils import sigmoid, softmax, cross_entropy_error, numerical_gradient

class TwoLayerNet:
  def __init__(self, input_size, hidden_size, output_size, weight_init_std=0.01):
    self.param = {}
    self.param['W1'] = weight_init_std*np.random.randn(input_size, hidden_size)
    self.param['W2'] = weight_init_std*np.random.randn(hidden_size, output_size)
    self.param['b1'] = np.zeros(hidden_size)
    self.param['b2'] = np.zeros(output_size)


  def predict(self, x):
    W1, W2 = self.param['W1'], self.param['W2']
    b1, b2 = self.param['b1'], self.param['b2']

    a1 = np.dot(x, W1) + b1
    z1 = sigmoid(a1)

    a2 = np.dot(z1, W2) + b2
    y = softmax(a2)

    return y

  def loss(self, x, t):
    y = self.predict(x)
    return cross_entropy_error(y, t)
  
  def accuracy(self, x, t):
    y = self.predict(x)

    y = np.argmax(y, axis=1)
    t = np.argmax(t, axis=1)

    return np.sum(y==t)/float(t.shape[0])

  def numerical_gradient(self, x, t):
    loss_w = lambda W : self.loss(x, t)

    grads = {}
    grads['W1'] = numerical_gradient(loss_w, self.param['W1'])
    grads['b1'] = numerical_gradient(loss_w, self.param['b1'])
    grads['W2'] = numerical_gradient(loss_w, self.param['W2'])
    grads['b2'] = numerical_gradient(loss_w, self.param['b2'])

    return grads


net = TwoLayerNet(input_size=784, hidden_size=50, output_size=10)
(x_train, y_train), (x_test, y_test) = mnist.load_data()
x_train = x_train.reshape(x_train.shape[0], 28*28)
y_train = to_categorical(y_train, num_classes=10)

x_test = x_test.reshape(x_test.shape[0], 28*28)
y_test = to_categorical(y_test, num_classes=10)

train_loss_list = []
inters_num = 1000
train_size = x_train.shape[0]
batch_size = 100
learning_rate = 0.1

for i in range(inters_num):
  batch_mask = np.random.choice(train_size, batch_size)
  x_batch = x_train[batch_mask]
  y_batch = y_train[batch_mask]

  grad = net.numerical_gradient(x_batch, y_batch)

  for key in ['W1', 'b1', 'W2', 'b2']:
    net.param[key] -= learning_rate * grad[key]

  loss = net.loss(x_batch, y_batch)
  train_loss_list.append(loss)

plt.plot(train_loss_list)
plt.show()