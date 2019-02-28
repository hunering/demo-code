import numpy as np
import matplotlib.pyplot as plt
from multi_layer_net import MultiLayerNet
from optimizer import SGD
from utils import load_mnist

weight_init_types = {'std=0.01': 0.01, 'Xavier': 'sigmoid', 'He': 'relu'}

(x_train, t_train), (x_test, t_test) = load_mnist(
    normalize=True, one_hot_label=True)
iters_num = 2000
train_size = x_train.shape[0]
batch_size = 100

train_loss = {}

for key, weight_type in weight_init_types.items():
    network = MultiLayerNet(input_size=784, hidden_size_list=[100, 100, 100, 100],
                            output_size=10, weight_init_std=weight_type)
    optimizer = SGD()
    train_loss[key] = []

    for i in range(iters_num):
        mask = np.random.choice(train_size, batch_size)
        x_batch = x_train[mask]
        t_batch = t_train[mask]

        grads = network.gradient(x_batch, t_batch)
        optimizer.update(network.params, grads)
        train_loss[key].append(network.loss(x_batch, t_batch))

markers = {'std=0.01': 'o', 'Xavier': 's', 'He': 'D'}
x = np.arange(iters_num)
for key in weight_init_types.keys():
    plt.plot(x, train_loss[key], marker=markers[key], markevery=100, label=key)
plt.xlabel("iterations")
plt.ylabel("loss")
plt.ylim(0, 2.5)
plt.legend()
plt.show()