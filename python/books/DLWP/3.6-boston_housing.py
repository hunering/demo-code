import numpy as np
from keras import models
from keras import layers
from keras import optimizers
from keras import losses
from keras import metrics
from keras.datasets import boston_housing
from sklearn.model_selection import train_test_split
import matplotlib.pyplot as plt
from utils import normalize_data, init_keras

init_keras()

(x_train, y_train),(x_test, y_test) = boston_housing.load_data()

normalize_data(x_train)
normalize_data(x_test)

k = 4
epochs = 500
mae_histories_4_k_fold = []
for i in range(k):
    x_train, x_cv, y_train, y_cv = train_test_split(
    x_train, y_train, test_size=1.0/k)

    model = models.Sequential()
    model.add(layers.Dense(64, activation='relu', input_shape=(x_train.shape[1],)))
    model.add(layers.Dense(64, activation='relu'))
    model.add(layers.Dense(1))

    model.compile(optimizer=optimizers.RMSprop(lr=0.001), loss=losses.mse, metrics=[metrics.mae])

    history = model.fit(x_train, y_train, epochs=epochs, batch_size=1)
    mae_histories_4_k_fold.append(history.history['mean_absolute_error'])

average_mae_history = [np.mean([x[i] for x in mae_histories_4_k_fold]) for i in range(epochs)]
average_mae_history = average_mae_history[10:]
plt.plot(range(1, len(average_mae_history)+1), average_mae_history)
plt.xlabel('Eponchs')
plt.ylabel('MAE')
plt.show() 
