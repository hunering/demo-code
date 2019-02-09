import numpy as np
import datetime

def sigmoid(x):
    return 1 / (1 + np.exp(-x))


def identity_function(x):
    return x


def relu(x):
    return np.maximum(0, x)


def softmax(a):
    c = np.max(a)  # deal with overflow
    exp_a = np.exp(a - c)
    sum_exp_a = np.sum(exp_a)
    y = exp_a / sum_exp_a

    return y


def mean_squared_error(y, t):
    return 0.5 * np.sum((y-t)**2)


def cross_entropy_error(y, t):
    if y.ndim == 1:
      y = y.reshape(1, y.size)
      t = t.reshape(1, t.size)
    batch_size = y.shape[0]
    delta = 1e-7
    return -np.sum(t * np.log(y + delta)) / batch_size

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

    x[idx] = original_val - h
    fxh2 = f(x)

    grad[idx] = (fxh1 - fxh2) / (2*h)
    x[idx] = original_val

  return grad

def numerical_gradient(f, x):
  if x.ndim == 1:
    return _numerical_gradient_no_batch(f, x)
  else:
    grad = np.zeros_like(x)
    for idx, x_row in enumerate(x):
      start_time = datetime.datetime.now() 
      grad[idx] = _numerical_gradient_no_batch(f, x_row)
      end_time = datetime.datetime.now() 
      print(f'index:{idx}, start: {start_time}, end: {end_time}')
    
    return grad

def gradient_descent(f, init_x, lr=0.01, step_num=100):
  x = init_x

  for step in range(100):
    grad = numerical_gradient(f, x)
    x -= lr * grad

  return x
