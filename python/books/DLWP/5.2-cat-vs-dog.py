import os
from keras import models
from keras import layers
from keras import optimizers
from keras import metrics
from keras import losses
from keras.datasets import mnist
from keras.utils import to_categorical
from keras.preprocessing.image import ImageDataGenerator
from keras import regularizers

import matplotlib.pyplot as plt
from utils import init_keras

init_keras()


def build_model():
    model = models.Sequential()
    model.add(layers.Conv2D(32, (3, 3), activation='relu',
                            input_shape=(150, 150, 3)))
    model.add(layers.MaxPooling2D((2, 2)))
    model.add(layers.Conv2D(64, (3, 3), activation='relu'))
    model.add(layers.MaxPooling2D((2, 2)))
    model.add(layers.Conv2D(128, (3, 3), activation='relu'))
    model.add(layers.MaxPooling2D((2, 2)))
    model.add(layers.Conv2D(128, (3, 3), activation='relu'))
    model.add(layers.MaxPooling2D((2, 2)))
    model.add(layers.Flatten())
    model.add(layers.Dropout(0.5))
    model.add(layers.Dense(512, activation='relu'))
    model.add(layers.Dense(1, activation='sigmoid'))
    return model

"""

"""

base_dir = r"C:\Users\huxiaomi\Downloads\deep-learning\data\kaggle-dogs-vs-cats"
small_base_dir = os.path.join(base_dir, "small")
train_dir = os.path.join(small_base_dir, r"train")
cv_dir = os.path.join(small_base_dir, r"validation")
batch_size = 32

train_datagen = ImageDataGenerator(rescale=1.0 / 255, rotation_range=40, width_shift_range=0.2,
                                   height_shift_range=0.2, shear_range=0.2,
                                   zoom_range=0.2, horizontal_flip=True)
test_datagen = ImageDataGenerator(rescale=1.0 / 255)

train_generator = train_datagen.flow_from_directory(
    train_dir, target_size=(150, 150), batch_size=batch_size, class_mode='binary')
validation_generator = train_datagen.flow_from_directory(
    cv_dir, target_size=(150, 150), batch_size=batch_size, class_mode='binary')

model = build_model()

model.compile(optimizer=optimizers.RMSprop(lr=1e-4),
              loss=losses.binary_crossentropy, metrics=['binary_accuracy'])
history = model.fit_generator(train_generator, epochs=100, steps_per_epoch=100,
                              validation_data=validation_generator, validation_steps=50)

model.save('cats_vs_dogs_small.h5')
'''
loss = history.history['loss']
acc = history.history['binary_accuracy']
val_acc = history.history['val_binary_accuracy']
val_loss = history.history['val_loss']

epochs = range(1, len(acc) + 1)
plt.plot(epochs, acc, 'bo', label='Training acc')
plt.plot(epochs, val_acc, 'b', label='Val acc')
plt.title('Accuracy')
plt.legend()

plt.figure()
plt.plot(epochs, loss, 'bo', label='Training loss')
plt.plot(epochs, val_loss, 'b', label='Val loss')
plt.title('Losses')
plt.legend()

plt.show()
'''