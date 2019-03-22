from keras.datasets import imdb
from keras.preprocessing import sequence
from keras import models
from keras import layers
from keras import optimizers
import matplotlib.pyplot as plt

max_features = 10000
max_len = 500
batch_size = 32

print('loading data')
(x_train, y_train), (x_test, y_test) = imdb.load_data(num_words=max_features)

input_train = sequence.pad_sequences(x_train, maxlen=max_len)
input_test = sequence.pad_sequences(x_test, maxlen=max_len)

model = models.Sequential()
model.add(layers.Embedding(max_features, 32))
model.add(layers.SimpleRNN(32))
model.add(layers.Dense(1, activation='sigmoid'))
model.compile(optimizer=optimizers.RMSprop(), loss='binary_crossentropy', metrics=['acc'])
history = model.fit(input_train, y_train, epochs=20, batch_size=batch_size, validation_split=0.2)

acc = history.history['acc']
val_acc = history.history['val_acc']
epochs = range(1, len(acc) + 1)

plt.plot(epochs, acc, 'bo', label='Trainning acc')
plt.plot(epochs, val_acc, 'b', label='Val acc')
plt.show()

