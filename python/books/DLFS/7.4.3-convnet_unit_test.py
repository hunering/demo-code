import numpy as np
from utils import im2col, col2im
from layers import Convolution, Pooling

import sys
import os
sys.path.append(os.getcwd()+'\\books\\dlfs-orig\\')
import common.layers as book_layers
import common.util as book_util

def test_im2col():
    """
    x1 = np.random.rand(1, 3, 7, 7)
    col1 = im2col(x1, 5, 5, stride=1, pad=0)
    print(f'the shape of result is {col1.shape}')

    x1 = np.random.rand(10, 3, 7, 7)
    col1 = im2col(x1, 5, 5, stride=1, pad=0)
    print(f'the shape of result is {col1.shape}')
    """
    col = np.arange(90*75).reshape(90, 75)
    r1 = col2im(col, (10, 3, 7, 7), 5, 5, stride=1, pad=0)
    r2 = book_util.col2im(col, (10, 3, 7, 7), 5, 5, stride=1, pad=0)
    print(f"test result of test_im2col: {(r1==r2).all()}")


def test_convolution_layer(input_data):
    W = np.random.randn(1, 3, 2, 2)
    b = np.random.randn(1)

    myconv = Convolution(W, b)
    myresult = myconv.forward(input_data)

    bookconv = book_layers.Convolution(W, b)
    bookconvresult = bookconv.forward(input_data)

    print(f"test result of test_convolution_layer: {(myresult==bookconvresult).all()}")

def test_convolution_backward(input_data):
    W = np.random.randn(1, 3, 2, 2)
    b = np.random.randn(1)

    myconv = Convolution(W, b)
    myresult = myconv.forward(input_data)

    bookconv = book_layers.Convolution(W, b)
    bookconvresult = bookconv.forward(input_data)

    dx = np.random.randn(*myresult.shape)
    dx2 = dx.copy()
    my_b_r = myconv.backward(dx)
    book_b_r = bookconv.backward(dx2)
    #print(my_b_r)
    #print(book_b_r)
    different = (my_b_r-book_b_r)
    print(f"test result of test_pooling_layer: {different.max()<1e-10}")


def test_pooling_layer(input_data):
    pool_h = 2
    pool_w = 2
    stride = 2
    padding = 0
    mypooling = Pooling(pool_h, pool_w, stride, padding)
    mypoolingresult = mypooling.forward(input_data)

    bookpooling = book_layers.Pooling(pool_h, pool_w, stride, padding)
    bookpoolingresult = bookpooling.forward(input_data)
    print(
        f"test result of test_pooling_layer: {(mypoolingresult==bookpoolingresult).all()}")


def test_pooling_backward(input_data):
    pool_h = 2
    pool_w = 2
    stride = 2
    padding = 0
    mypooling = Pooling(pool_h, pool_w, stride, padding)
    mypoolingresult = mypooling.forward(input_data)

    bookpooling = book_layers.Pooling(pool_h, pool_w, stride, padding)
    bookpoolingresult = bookpooling.forward(input_data)

    dx = np.random.randn(*mypoolingresult.shape)
    my_b_r = mypooling.backward(dx)
    book_b_r = bookpooling.backward(dx)
    # print(my_b_r)
    # print(book_b_r)
    print(f"test result of test_pooling_layer: {(my_b_r==book_b_r).all()}")


x = np.random.randn(2, 3, 4, 4)
test_im2col()
test_convolution_layer(x)
test_convolution_backward(x)
test_pooling_layer(x)
test_pooling_backward(x)


x1 = np.array([[
    [
        [1, 2, 0],
        [1, 1, 3],
        [0, 2, 2]
    ],
    [
        [0, 2, 1],
        [0, 3, 2],
        [1, 1, 0]
    ],
    [
        [1, 2, 1],
        [0, 1, 3],
        [3, 3, 2]
    ]
], [
    [
        [1, 2, 0],
        [1, 1, 3],
        [0, 2, 2]
    ],
    [
        [0, 2, 1],
        [0, 3, 2],
        [1, 1, 0]
    ],
    [
        [1, 2, 1],
        [0, 1, 3],
        [3, 3, 2]
    ]
]]
)
