from collections import OrderedDict
import numpy as np
from layers import Convolution, Pooling, Affine, Relu, SoftmaxWithLoss
from utils import get_conv_result_shape, numerical_gradient


class ConvoNet:
    """conv - relu - pool - affine - relu - affine - softmax"""

    def __init__(self, input_shape=(1, 28, 28),
                 conv_param={'filter_num': 30,
                             'filter_size': 5, 'pad': 0, 'stride': 1},
                 hidden_size=100, output_size=10,
                 weight_init_std=0.01):

        self.input_shape = input_shape
        self.hidden_size = hidden_size
        self.output_size = output_size
        self.weight_init_std = weight_init_std
        self.pool_filter_size = (2, 2)
        self.pool_filter_stride = 2
        self.pool_pad = 0
        self.conv_param = conv_param
        self.init_params(input_shape, conv_param)
        self.init_layers()

    def init_params(self, input_shape, conv_param):
        C, H, W = input_shape
        filter_num = conv_param['filter_num']
        filter_h = conv_param['filter_size']
        filter_w = conv_param['filter_size']
        pad = conv_param['pad']
        stride = conv_param['stride']
        conv_output_size = get_conv_result_shape(
            H, W, filter_h, filter_w, stride, pad)

        pool_filter_h = self.pool_filter_size[0]
        pool_filter_w = self.pool_filter_size[1]
        pool_pad = self.pool_pad
        pool_stride = self.pool_filter_stride
        pooling_output_size = get_conv_result_shape(conv_output_size[0], conv_output_size[1],
                                                    pool_filter_h, pool_filter_w, pool_stride, pool_pad)
        pooling_output_total = filter_num * \
            pooling_output_size[0] * pooling_output_size[1]

        self.params = {}
        self.params['W1'] = self.weight_init_std * \
            np.random.randn(filter_num, C, filter_h, filter_w)
        self.params['b1'] = self.weight_init_std * np.random.randn(filter_num)
        self.params['W2'] = self.weight_init_std * \
            np.random.randn(pooling_output_total, self.hidden_size)
        self.params['b2'] = self.weight_init_std * \
            np.random.randn(self.hidden_size)
        self.params['W3'] = self.weight_init_std * \
            np.random.randn(self.hidden_size, self.output_size)
        self.params['b3'] = self.weight_init_std * \
            np.random.randn(self.output_size)

    def init_layers(self):
        self.layers = OrderedDict()
        self.layers['Convo1'] = Convolution(
            self.params['W1'], self.params['b1'], self.conv_param['stride'], self.conv_param['pad'])
        self.layers['Relu1'] = Relu()
        self.layers['Pool1'] = Pooling(
            pool_h=self.pool_filter_size[0], pool_w=self.pool_filter_size[1], stride=self.pool_filter_stride)
        self.layers['Affine1'] = Affine(self.params['W2'], self.params['b2'])
        self.layers['Relu2'] = Relu()
        self.layers['Affine2'] = Affine(self.params['W3'], self.params['b3'])

        self.last_layer = SoftmaxWithLoss()

    def predict(self, x):
        for layer in self.layers.values():
            x = layer.forward(x)

        return x

    def loss(self, x, t):
        y = self.predict(x)
        return self.last_layer.forward(y, t)

    def accuracy(self, x, t, batch_size=100):
        if t.ndim != 1:
            t = np.argmax(t, axis=1)

        acc = 0.0

        for i in range(int(x.shape[0] / batch_size)):
            tx = x[i*batch_size:(i+1)*batch_size]
            tt = t[i*batch_size:(i+1)*batch_size]
            y = self.predict(tx)
            y = np.argmax(y, axis=1)
            acc += np.sum(y == tt)

        return acc / x.shape[0]

    def numerical_gradient(self, x, t):
        def loss_w(w): return self.loss(x, t)

        grads = {}
        for idx in (1, 2, 3):
            grads['W' + str(idx)] = numerical_gradient(loss_w,
                                                       self.params['W' + str(idx)])
            grads['b' + str(idx)] = numerical_gradient(loss_w,
                                                       self.params['b' + str(idx)])

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
        grads['W1'], grads['b1'] = self.layers['Convo1'].dW, self.layers['Convo1'].db
        grads['W2'], grads['b2'] = self.layers['Affine1'].dW, self.layers['Affine1'].db
        grads['W3'], grads['b3'] = self.layers['Affine2'].dW, self.layers['Affine2'].db

        return grads
