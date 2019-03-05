from keras import models
from keras import layers
from keras.datasets import mnist
from keras.utils import to_categorical



network = models.Sequential()
network.add(layers.Dense(512, activation='relu', input_shape=(28*28,)))
network.add(layers.Dense(10, activation='softmax'))

network.compile(optimizer='rmsprop', loss='categorical_crossentropy',metrics=['accuracy'])

(x_train, t_train), (x_test, t_test) = mnist.load_data()
x_train = x_train.reshape(x_train.shape[0], -1).astype('float32')/255
x_test = x_test.reshape(x_test.shape[0], -1).astype('float32')/255
t_train = to_categorical(t_train)
t_test = to_categorical(t_test)

network.fit(x_train, t_train, epochs=5, batch_size=128)

test_loss, test_acc = network.evaluate(x_test, t_test)
print(f"test acc:{test_acc}")