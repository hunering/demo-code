import os
import numpy as np
from keras.preprocessing.image import ImageDataGenerator
from keras.applications import VGG16
from keras import models
from keras import layers
from keras import optimizers
from keras import losses
from keras import metrics

conv_base = VGG16(weights='imagenet', include_top=False,
                  input_shape=(150, 150, 3))

base_dir = r"C:\Users\huxiaomi\Downloads\deep-learning\data\kaggle-dogs-vs-cats"
small_base_dir = os.path.join(base_dir, "small")
train_dir = os.path.join(small_base_dir, r"train")
cv_dir = os.path.join(small_base_dir, r"validation")
test_dir = os.path.join(small_base_dir, r"test")
batch_size = 20

train_datagen = ImageDataGenerator(rescale=1.0 / 255, rotation_range=40, width_shift_range=0.2,
                                   height_shift_range=0.2, shear_range=0.2,
                                   zoom_range=0.2, horizontal_flip=True)
test_datagen = ImageDataGenerator(rescale=1.0 / 255)

conv_base = VGG16(weights='imagenet', include_top=False,
                  input_shape=(150, 150, 3))

model = models.Sequential()
model.add(conv_base)
model.add(layers.Flatten())
model.add(layers.Dense(256, activation='relu', input_dim=4*4*512))
model.add(layers.Dense(1, activation='sigmoid'))
conv_base.trainable = False
model.compile(optimizer=optimizers.RMSprop(lr=1e-5),
              loss=losses.binary_crossentropy, metrics=[metrics.binary_accuracy])
len(model.trainable_weights)


train_generator = train_datagen.flow_from_directory(
    train_dir, target_size=(150, 150), batch_size=batch_size, class_mode='binary')
validation_generator = train_datagen.flow_from_directory(
    cv_dir, target_size=(150, 150), batch_size=batch_size, class_mode='binary')

history = model.fit_generator(train_generator, epochs=100, steps_per_epoch=100,
                              validation_data=validation_generator, validation_steps=50)
