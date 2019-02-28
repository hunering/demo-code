import numpy as np
from utils import cross_entropy_error, softmax, im2col, get_conv_result_shape


class MulLayer:
    def __init__(self):
        self.x = None
        self.y = None

    def forward(self, x, y):
        self.x = x
        self.y = y
        return x*y

    def backward(self, dout):
        dx = dout * self.y
        dy = dout * self.x

        return dx, dy


class AddLayer:
    def __init__(self):
        self.x = None
        self.y = None

    def forward(self, x, y):
        self.x = x
        self.y = y
        return x + y

    def backward(self, dout):
        dx = dout
        dy = dout
        return dx, dy


class Relu:
    def __init__(self):
        self.x = None

    def forward(self, x):
        self.x = x
        out = x.copy()
        self.mask = (x <= 0)
        out[self.mask] = 0
        return out

    def backward(self, dout):
        dout[self.mask] = 0
        dx = dout
        return dx


class Sigmoid:
    def __self(self):
        self.out = None

    def forward(self, x):
        out = 1/(1+np.exp(-x))
        self.out = out
        return out

    def backward(self, dout):
        dx = dout * self.out * (1.0-self.out)
        return dx


class Affine:
    def __init__(self, W, b, name=None):
        self.W = W
        self.b = b
        self.x = None
        self.dW = None
        self.db = None
        self.name = name

    def forward(self, x):
        out = np.dot(x, self.W) + self.b
        self.x = x
        return out

    def backward(self, dout):
        dx = np.dot(dout, self.W.T)
        # why not divide batch_size here?
        # seems because we divide batch_size in the SoftmaxWithLoss layer
        self.dW = np.dot(self.x.T, dout)
        self.db = np.sum(dout, axis=0)

        return dx


class SoftmaxWithLoss:
    def __init__(self):
        self.loss = None
        self.y = None
        self.t = None

    def forward(self, x, t):
        self.t = t
        self.y = softmax(x)
        self.loss = cross_entropy_error(self.y, self.t)

        return self.loss

    def backward(self, dout=1):
        batch_size = self.t.shape[0]
        dx = (self.y-self.t)/batch_size

        return dx


class BatchNormalization:
    """
    http://arxiv.org/abs/1502.03167
    """

    def __init__(self, gamma, beta, momentum=0.9, running_mean=None, running_var=None):
        self.gamma = gamma
        self.beta = beta
        self.momentum = momentum
        self.input_shape = None  # Conv层的情况下为4维，全连接层的情况下为2维

        # 测试时使用的平均值和方差
        self.running_mean = running_mean
        self.running_var = running_var

        # backward时使用的中间数据
        self.batch_size = None
        self.xc = None
        self.std = None
        self.dgamma = None
        self.dbeta = None

    def forward(self, x, train_flg=True):
        self.input_shape = x.shape
        if x.ndim != 2:
            N, C, H, W = x.shape
            x = x.reshape(N, -1)

        out = self.__forward(x, train_flg)

        return out.reshape(*self.input_shape)

    def __forward(self, x, train_flg):
        if self.running_mean is None:
            N, D = x.shape
            self.running_mean = np.zeros(D)
            self.running_var = np.zeros(D)

        if train_flg:
            mu = x.mean(axis=0)
            xc = x - mu
            var = np.mean(xc**2, axis=0)
            std = np.sqrt(var + 10e-7)
            xn = xc / std

            self.batch_size = x.shape[0]
            self.xc = xc
            self.xn = xn
            self.std = std
            self.running_mean = self.momentum * \
                self.running_mean + (1-self.momentum) * mu
            self.running_var = self.momentum * \
                self.running_var + (1-self.momentum) * var
        else:
            xc = x - self.running_mean
            xn = xc / ((np.sqrt(self.running_var + 10e-7)))

        out = self.gamma * xn + self.beta
        return out

    def backward(self, dout):
        if dout.ndim != 2:
            N, C, H, W = dout.shape
            dout = dout.reshape(N, -1)

        dx = self.__backward(dout)

        dx = dx.reshape(*self.input_shape)
        return dx

    def __backward(self, dout):
        dbeta = dout.sum(axis=0)
        dgamma = np.sum(self.xn * dout, axis=0)
        dxn = self.gamma * dout
        dxc = dxn / self.std
        dstd = -np.sum((dxn * self.xc) / (self.std * self.std), axis=0)
        dvar = 0.5 * dstd / self.std
        dxc += (2.0 / self.batch_size) * self.xc * dvar
        dmu = np.sum(dxc, axis=0)
        dx = dxc - dmu / self.batch_size

        self.dgamma = dgamma
        self.dbeta = dbeta

        return dx


class Convolution:
    def __init__(self, W, b, stride=1, padding=0):
        self.W = W
        self.b = b
        self.stride = stride
        self.padding = padding

        if(self.W.ndim == 3):
            FC, FH, FW = self.W.shape
            self.W = self.W.reshape(1,FC, FH, FW)

        

    def forward(self, x):
        FN, FC, FH, FW = self.W.shape

        if(x.ndim == 3):
            C, H, W = x.shape
            x = x.reshape(1, C, H, W)

        N, C, H, W = x.shape

        out_h, out_w = get_conv_result_shape(
            H, W, FH, FW, self.stride, self.padding)
        im_col = im2col(x, FH, FW, self.stride, self.padding)
        W_col = self.W.reshape((FN, -1))

        out = im_col.dot(W_col.T)+self.b
        out = out.reshape(N, out_h, out_w, -1).transpose(0, 3, 1, 2)

        return out

class Pooling:
    def __init__(self, pool_h, pool_w, stride=1, padding=0):
        self.pool_h = pool_h
        self.pool_w = pool_w
        self.stride = stride
        self.padding = padding
    
    def forward(self, x):
        N, C, H, W = x.shape
        im_col = im2col(x, self.pool_h, self.pool_w, self.stride, self.padding)
        im_col = im_col.reshape(-1, self.pool_h*self.pool_w)
        out = im_col.max(axis=1)

        out_h, out_w = get_conv_result_shape(
            H, W, self.pool_h, self.pool_w, self.stride, self.padding)
        out = out.reshape(N,out_h, out_w, -1).transpose(0, 3, 1, 2)
        return out