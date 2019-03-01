import numpy as np
from utils import im2col
from layers import Convolution, Pooling

import sys, os
sys.path.append(os.getcwd()+'\\books\\dlfs-orig\\')
import common.util as book_util
import layers as book_layers

"""
x1 = np.random.rand(1, 3, 7, 7)
col1 = im2col(x1, 5, 5, stride=1, pad=0)
print(f'the shape of result is {col1.shape}')

x1 = np.random.rand(10, 3, 7, 7)
col1 = im2col(x1, 5, 5, stride=1, pad=0)
print(f'the shape of result is {col1.shape}')
"""

a = (1,)
b = (2,)
print(a +b)
x1=np.array([[
    [
        [1,2,0],
        [1,1,3],
        [0,2,2]
    ],
    [
        [0,2,1],
        [0,3,2],
        [1,1,0]
    ],
    [
        [1,2,1],
        [0,1,3],
        [3,3,2]
    ]
    ],[
    [
        [1,2,0],
        [1,1,3],
        [0,2,2]
    ],
    [
        [0,2,1],
        [0,3,2],
        [1,1,0]
    ],
    [
        [1,2,1],
        [0,1,3],
        [3,3,2]
    ]
    ]]
)

window = x1[0,:,0:2,0:2]
col1 = im2col(x1, 2, 2, stride=2, pad=1)
col2 = book_util.im2col(x1, 2, 2, stride=2, pad=1)
print((col1==col2).all())

W = np.random.randn(3,2,2)
b = np.random.randn(1)
myconv = Convolution(W, b)
myresult = myconv.forward(x1)

bookconv = book_layers.Convolution(W, b)
bookconvresult = myconv.forward(x1)

print(myresult-bookconvresult)
x = np.random.randn(2, 3, 4, 4)
pool_h = 2
pool_w = 2
stride=2
padding=0
mypooling = Pooling(pool_h, pool_w, stride, padding)
mypoolingresult = mypooling.forward(x)

bookpooling = book_layers.Pooling(pool_h, pool_w, stride, padding)
bookpoolingresult = bookpooling.forward(x)
print(mypoolingresult-bookpoolingresult)



