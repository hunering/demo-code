import os
import numpy as np
from keras.preprocessing.image import ImageDataGenerator
from keras.applications import VGG16
from keras import models
from keras import layers
from keras import optimizers
from keras import losses
from keras import metrics
import time

conv_base = VGG16(weights='imagenet', include_top=False,
                  input_shape=(150, 150, 3))

base_dir = r"C:\Users\huxiaomi\Downloads\deep-learning\data\kaggle-dogs-vs-cats"
small_base_dir = os.path.join(base_dir, "small")
train_dir = os.path.join(small_base_dir, r"train")
cv_dir = os.path.join(small_base_dir, r"validation")
test_dir = os.path.join(small_base_dir, r"test")
batch_size = 20

imageGen = ImageDataGenerator(rescale=(1.0/255))


def extract_features(dir, sample_count):
    features = np.zeros(shape=(sample_count, 4, 4, 512))
    labels = np.zeros(shape=(sample_count,))

    generator = imageGen.flow_from_directory(dir, target_size=(
        150, 150), batch_size=batch_size, class_mode='binary')
    i = 0
    for inputs_batch, labels_batch in generator:
        start_time = time.time()
        feature = conv_base.predict(inputs_batch)
        elapsed_time = time.time() - start_time
        features[i*batch_size:(i+1)*batch_size] = feature
        labels[i*batch_size:(i+1)*batch_size] = labels_batch
        i += 1
        if(i*batch_size >= sample_count):
            break

    return features, labels


train_feature, train_label = extract_features(train_dir, 2000)
validation_feature, validation_label = extract_features(cv_dir, 1000)
test_feature, test_label = extract_features(train_dir, 2000)

train_feature = train_feature.reshape(2000, -1)
validation_feature = validation_feature.reshape(1000, -1)
test_feature = test_feature.reshape(1000, -1)

model = models.Sequential()
model.add(layers.Dense(512, activation='relu', input_dim=4*4*512))
model.add(layers.Dropout(0.5))
model.add(layers.Dense(1, activation='sigmoid'))

model.compile(optimizer=optimizers.RMSprop(lr=1e-4),
              loss=losses.binary_crossentropy, metrics=[metrics.binary_accuracy])

history = model.fit(train_feature, train_label, batch_size=20, epochs=30,
                    validation_data=(validation_feature, validation_label))

