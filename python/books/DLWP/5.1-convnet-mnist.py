from keras import models
from keras import layers
from keras import optimizers
from keras import metrics
from keras.datasets import mnist
from keras.utils import to_categorical
from utils import init_keras

init_keras()

(x_train, t_train), (x_test, t_test) = mnist.load_data()
x_train = x_train.reshape(x_train.shape[0], 28,28,1).astype('float32')/255
x_test = x_test.reshape(x_test.shape[0], 28,28,1).astype('float32')/255
t_train = to_categorical(t_train)
t_test = to_categorical(t_test)

model = models.Sequential()
model.add(layers.Conv2D(32, (3,3), activation='relu', input_shape=(28,28,1)))
model.add(layers.MaxPooling2D((2,2)))
model.add(layers.Conv2D(64, (3,3), activation='relu'))
model.add(layers.MaxPooling2D((2,2)))
model.add(layers.Conv2D(64, (3,3), activation='relu'))
model.add(layers.Flatten())
model.add(layers.Dense(512, activation='relu'))
model.add(layers.Dense(10, activation='softmax'))

model.compile(optimizer=optimizers.RMSprop(), loss='categorical_crossentropy',metrics=['accuracy'])

model.fit(x_train, t_train, epochs=5, batch_size=64)

test_loss, test_acc = model.evaluate(x_test, t_test)
print(f"test acc:{test_acc}")