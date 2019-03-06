import numpy as np
from keras.datasets import reuters
from keras import models
from keras import layers
from keras import optimizers
from keras import losses
from keras import metrics
from keras.utils import to_categorical
from sklearn.model_selection import train_test_split
from utils import init_keras, vectorize_sequences

init_keras()

(x_train, t_train), (x_test, t_test) = reuters.load_data(num_words=10000)

x_train = vectorize_sequences(x_train)
x_test = vectorize_sequences(x_test)
t_train = to_categorical(t_train)
t_test = to_categorical(t_test)

x_train, x_cv, t_train, t_cv = train_test_split(
    x_train, t_train, test_size=0.33)

network = models.Sequential()
network.add(layers.Dense(64, activation='relu', input_shape=(10000,)))
network.add(layers.Dense(64, activation='relu'))
network.add(layers.Dense(46, activation='softmax'))

network.compile(optimizer=optimizers.RMSprop(lr=0.001),
                loss=losses.categorical_crossentropy, metrics=[metrics.categorical_accuracy])
network.fit(x=x_train, y=t_train, epochs=15,
            batch_size=512, validation_data=(x_cv, t_cv))

result = network.evaluate(x=x_test, y=t_test)
print(result)