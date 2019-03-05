import numpy as np
from keras.datasets import imdb
from keras import models
from keras import layers
from keras import optimizers
from keras import losses
from keras import metrics

from utils import init_keras, imdb_vectorize_sequences

init_keras()
(train_data, train_labels), (test_data, test_labels) = \
    imdb.load_data(num_words=10000)

x_train = imdb_vectorize_sequences(train_data)
x_cv = x_train[:10000]
x_train = x_train[10000:]

x_test = imdb_vectorize_sequences(test_data)
t_train = np.asarray(train_labels).astype(np.float32)
t_cv = t_train[:10000]
t_train = t_train[10000:]
t_test = np.asarray(test_labels).astype(np.float32)

network = models.Sequential()
network.add(layers.Dense(16, activation='relu', input_shape=(10000,)))
network.add(layers.Dense(16, activation='relu', input_shape=(16,)))
network.add(layers.Dense(1, activation='sigmoid', input_shape=(16,)))

network.compile(optimizer=optimizers.RMSprop(lr=0.001),
                loss=losses.binary_crossentropy, metrics=[metrics.binary_accuracy])
history = network.fit(x_train, t_train, epochs=20, batch_size=512, validation_data=(x_cv, t_cv))
