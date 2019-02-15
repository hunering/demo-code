import numpy as np
import datetime
from PIL import Image
from keras.datasets import mnist
from keras.utils import to_categorical

def sigmoid(x):
    return 1 / (1 + np.exp(-x))


def identity_function(x):
    return x


def relu(x):
    return np.maximum(0, x)

"""
def softmax(a):
    c = np.max(a)  # deal with overflow
    exp_a = np.exp(a - c)
    sum_exp_a = np.sum(exp_a)
    y = exp_a / sum_exp_a

    return y
"""
def softmax(x):
    if x.ndim == 2:
        x = x.T
        x = x - np.max(x, axis=0)
        y = np.exp(x) / np.sum(np.exp(x), axis=0)
        return y.T 

    x = x - np.max(x) # 溢出对策
    return np.exp(x) / np.sum(np.exp(x))

def mean_squared_error(y, t):
    return 0.5 * np.sum((y-t)**2)


def cross_entropy_error(y, t):
    if y.ndim == 1:
        t = t.reshape(1, t.size)
        y = y.reshape(1, y.size)
        
    # 监督数据是one-hot-vector的情况下，转换为正确解标签的索引
    if t.size == y.size:
        t = t.argmax(axis=1)
             
    batch_size = y.shape[0]
    return -np.sum(np.log(y[np.arange(batch_size), t] + 1e-7)) / batch_size

def numerical_diff(f, x):
  h = 1e-4
  return (f(x+h) - f(x-h)) / (2*h)

def _numerical_gradient_no_batch(f, x):
  h = 1e-4
  grad = np.zeros_like(x)

  for idx in range(x.size):
    original_val = x[idx]
    x[idx] = original_val + h
    fxh1 = f(x)
    #print(f"numerical gradient v1:{fxh1}")

    x[idx] = original_val - h
    fxh2 = f(x)
    #print(f"numerical gradient v2:{fxh2}")

    grad[idx] = (fxh1 - fxh2) / (2*h)
    if grad[idx] > 0:
      pass

    x[idx] = original_val

  return grad

def numerical_gradient(f, x):
  if x.ndim == 1:
    return _numerical_gradient_no_batch(f, x)
  else:
    grad = np.zeros_like(x)
    for idx, x_row in enumerate(x):
      #start_time = datetime.datetime.now() 
      grad[idx] = _numerical_gradient_no_batch(f, x_row)
      #end_time = datetime.datetime.now() 
      #print(f'index:{idx}, start: {start_time}, end: {end_time}')
    
    return grad

def gradient_descent(f, init_x, lr=0.01, step_num=100):
  x = init_x

  for step in range(100):
    grad = numerical_gradient(f, x)
    x -= lr * grad

  return x

def img_show(img):
  pil_img = Image.fromarray(np.uint8(img))
  pil_img.show()

def load_mnist(normalize=False, one_hot_label=True):
  (x_train, y_train), (x_test, y_test) = mnist.load_data()

  if normalize:
    x_train = x_train.reshape(x_train.shape[0], 28*28).astype(np.float32)/255.0
    x_test = x_test.reshape(x_test.shape[0], 28*28).astype(np.float32)/255.0
 
  if one_hot_label:
    y_train = to_categorical(y_train, num_classes=10)
    y_test = to_categorical(y_test, num_classes=10)
  
  return (x_train, y_train), (x_test, y_test)