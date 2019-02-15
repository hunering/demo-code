import numpy as np
from collections import OrderedDict

from layers import Affine, Relu, SoftmaxWithLoss
from utils import numerical_gradient


class MultiLayerNet:
    def __init__(self, input_size, hidden_size_list, output_size, weight_init_std=0.01):
        self.input_size = input_size
        self.hidden_layer_list = hidden_size_list
        self.output_size = output_size
        layer_size_list = [input_size] + hidden_size_list + [output_size]
        self._init_weight_params(layer_size_list, weight_init_std)
        self._init_layers(layer_size_list)

    def _init_weight_params(self, layer_size_list, weight_init_std):
        self.params = {}
        for i in range(1, len(layer_size_list)):
            front_layer_size = layer_size_list[i-1]
            back_layer_size = layer_size_list[i]
            self.params['W'+str(i)] = weight_init_std * \
                np.random.randn(front_layer_size, back_layer_size)
            self.params['b'+str(i)] = np.zeros(back_layer_size)

    def _init_layers(self, layer_size_list):
        self.layers = OrderedDict()
        for i in range(1, len(layer_size_list)-1):
            self.layers['Affine'+str(i)] = Affine(
                self.params['W' + str(i)], self.params['b'+str(i)])
            self.layers['Activition'+str(i)] = Relu()

        output_layer_idx = len(layer_size_list)-1
        self.layers['Affine'+str(output_layer_idx)] = Affine(
            self.params['W'+str(output_layer_idx)], self.params['b'+str(output_layer_idx)])

        self.last_layer = SoftmaxWithLoss()
    
    def predict(self, x):
        for layer in self.layers.values():
            x = layer.forward(x)
        return x
    
    def loss(self, x, t):
        y = self.predict(x)
        loss_val = self.last_layer.forward(y, t)
        return loss_val
    
    def accuracy(self, x, y):
        y = self.predict(x)
        y = np.argmax(y, axis=1)
        if t.ndim != 1 : t = np.argmax(t, axis=1)

        accuracy = np.sum(y == t) / float(x.shape[0])
        return accuracy
    
    def numerical_gradient(self, x, t):
        loss_w = lambda W : self.loss(x, t)

        grads = {}
        layer_count = len(self.hidden_layer_list) + 1
        for i in range(1, layer_count+1):
            grads['W'+str(i)] = numerical_gradient(loss_w, self.params['W'+str(i)])
            grads['b'+str(i)] = numerical_gradient(loss_w, self.params['b'+str(i)])

        return grads
    
    def gradient(self, x, t):
        # forward
        self.loss(x, t)

        # backward
        dout = 1
        dout = self.last_layer.backward(dout)

        layers = list(self.layers.values())
        layers.reverse()
        for layer in layers:
            dout = layer.backward(dout)

        # 设定
        grads = {}
        layer_count = len(self.hidden_layer_list) + 1
        for idx in range(1, layer_count+1):
            grads['W' + str(idx)] = self.layers['Affine' + str(idx)].dW
            grads['b' + str(idx)] = self.layers['Affine' + str(idx)].db

        return grads


