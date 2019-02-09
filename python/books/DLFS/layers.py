import numpy as np
from utils import cross_entropy_error, softmax

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
  def __init__(self, W, b, name):
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