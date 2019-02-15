import numpy as np
import matplotlib.pyplot as plt

from two_layer_net import TwoLayerNet
from utils import img_show, load_mnist

net = TwoLayerNet(input_size=784, hidden_size=50, output_size=10)

(x_train, y_train), (x_test, y_test) = load_mnist(normalize=True, one_hot_label=True)

"""
img = x_train[0]*255
img = img.reshape(28, 28).astype(np.int8)
img_show(img) 
"""

train_loss_list = []
inters_num = 10000
train_size = x_train.shape[0]
batch_size = 100
learning_rate = 0.1

train_loss_list = []
train_acc_list = []
test_acc_list = []

iter_per_epoch = max(train_size / batch_size, 1)

for i in range(inters_num):
    batch_mask = np.random.choice(train_size, batch_size)
    x_batch = x_train[batch_mask]
    y_batch = y_train[batch_mask]

    grad = net.gradient(x_batch, y_batch)

    for key in ['W1', 'b1', 'W2', 'b2']:
        net.params[key] -= learning_rate * grad[key]

    loss = net.loss(x_batch, y_batch)
    train_loss_list.append(loss)

    if i % iter_per_epoch == 0:
        train_acc = net.accuracy(x_train, y_train)
        test_acc = net.accuracy(x_test, y_test)
        train_acc_list.append(train_acc)
        test_acc_list.append(test_acc)
        print(train_acc, test_acc)

plt.plot(train_loss_list)
plt.show()
