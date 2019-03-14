from keras import models
from keras.preprocessing import image
from keras.applications import VGG16
from keras import backend as K

import numpy as np
import matplotlib.pyplot as plt
from utils import init_keras


def deprocess_img(x):
    x -= np.mean(x)
    x /= (np.std(x)+1e-5)
    x *= 0.1

    x += 0.5
    x = np.clip(x, 0, 1)

    x *= 255
    x = np.clip(x, 0, 255)
    return x


def gen_filter_pattern(layer, filter_idx, size=150):
    layer_output = layer.output
    filter_output = layer_output[:, :, :, filter_idx]
    loss = K.mean(filter_output)

    # the grads is in [N, H, W, C] format
    grads = K.gradients(loss, model.input)[0]
    grads /= (K.sqrt(K.mean(K.square(grads))) + 1e-5)

    iterate = K.function([model.input], [loss, grads])
    img = np.random.random((1, size, size, 3))*20 + 128
    step = 1.0
    for i in range(20):
        loss_v, grads_v = iterate([img])
        img += (grads_v*step)

    img = img[0]
    deprocess_img(img)

    return img


def gen_layer_pattern(layer_name, model):
    layer = model.get_layer(layer_name)
    size = 150
    margin = 5
    results = np.zeros((8*size + 7*margin, 8*size + 7*margin, 3))
    for i in range(8):
        for j in range(8):
            filter_img = gen_filter_pattern(layer, i + (j*8), size=size)
            h_start = i*size+i*margin
            h_end = h_start+size
            v_start = j*size+j*margin
            v_end = v_start+size
            results[h_start:h_end, v_start:v_end, :] = filter_img

    plt.imshow(results)
    plt.show()


init_keras()

model = VGG16(weights='imagenet', include_top=False,
              input_shape=(150, 150, 3))

gen_layer_pattern('block1_conv1', model)
gen_layer_pattern('block2_conv1', model)
gen_layer_pattern('block3_conv1', model)
gen_layer_pattern('block4_conv1', model)
