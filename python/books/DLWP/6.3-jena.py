import os
import numpy as np
import matplotlib.pyplot as plt
from keras.models import Sequential
from keras import layers
from keras.optimizers import RMSprop
import matplotlib.pyplot as plt


def load_data():
    data_dir = r'C:\Users\huxiaomi\Downloads\deep-learning\data\jena_climate'
    fname = os.path.join(data_dir, 'jena_climate_2009_2016.csv')

    f = open(fname, encoding="utf8")
    data = f.read()
    f.close()

    lines = data.split('\n')
    header = lines[0]
    lines = lines[1:]
    headers = header.split(',')

    float_data = np.zeros((len(lines), len(headers)-1))
    for i, line in enumerate(lines):
        data_one_line_str = line.split(',')
        data_one_line = [float(x) for x in data_one_line_str[1:]]
        float_data[i] = data_one_line

    #temp_data = float_data[:, 1]
    # plt.plot(temp_data)
    # plt.show()
    return float_data


def normalize_data(float_data, train_data_size):
    means = np.mean(float_data[:train_data_size], axis=0)
    float_data -= means

    stds = np.std(float_data[:train_data_size], axis=0)
    float_data /= stds


def generator(data, lookback, delay, min_index, max_index,
              shuffle=False, batch_size=128, step=6):
    if max_index is None:
        max_index = len(data) - delay - 1
    i = min_index + lookback
    while 1:
        if shuffle:
            rows = np.random.randint(
                min_index + lookback, max_index, size=batch_size)
        else:
            if i + batch_size >= max_index:
                i = min_index + lookback
            rows = np.arange(i, min(i + batch_size, max_index))
            i += len(rows)

        samples = np.zeros((len(rows),
                            lookback // step,
                            data.shape[-1]))
        targets = np.zeros((len(rows),))
        for j, row in enumerate(rows):
            indices = range(rows[j] - lookback, rows[j], step)
            samples[j] = data[indices]
            targets[j] = data[rows[j] + delay][1]
        yield samples, targets


def create_generator(float_data, lookback=1440, step=6):
    delay = 144
    batch_size = 128

    train_gen = generator(float_data,
                          lookback=lookback,
                          delay=delay,
                          min_index=0,
                          max_index=200000,
                          shuffle=True,
                          step=step,
                          batch_size=batch_size)
    val_gen = generator(float_data,
                        lookback=lookback,
                        delay=delay,
                        min_index=200001,
                        max_index=300000,
                        step=step,
                        batch_size=batch_size)
    test_gen = generator(float_data,
                         lookback=lookback,
                         delay=delay,
                         min_index=300001,
                         max_index=None,
                         step=step,
                         batch_size=batch_size)

    # This is how many steps to draw from `val_gen`
    # in order to see the whole validation set:
    val_steps = (300000 - 200001 - lookback) // batch_size

    # This is how many steps to draw from `test_gen`
    # in order to see the whole test set:
    test_steps = (len(float_data) - 300001 - lookback) // batch_size

    return (train_gen, val_gen, test_gen), (val_steps, test_steps)


def evaluate_naive_method(val_gen, val_steps):
    maes = []
    for i in range(val_steps):
        samples, targets = next(val_gen)
        predicts = samples[:, -1, 1]
        ae = np.abs(predicts-targets)
        mae = np.mean(ae)
        maes.append(mae)
    return np.mean(maes)


def draw_histroy(history):
    loss = history.history['loss']
    val_loss = history.history['val_loss']

    epochs = range(len(loss))

    plt.figure()

    plt.plot(epochs, loss, 'bo', label='Training loss')
    plt.plot(epochs, val_loss, 'b', label='Validation loss')
    plt.title('Training and validation loss')
    plt.legend()

    plt.show()


def dense_model(float_data, lookback=1440, step=6):
    model = Sequential()
    model.add(layers.Flatten(input_shape=(
        lookback // step, float_data.shape[-1])))
    model.add(layers.Dense(32, activation='relu'))
    model.add(layers.Dense(1))

    model.compile(optimizer=RMSprop(), loss='mae')
    history = model.fit_generator(train_gen,
                                  steps_per_epoch=500,
                                  epochs=20,
                                  validation_data=val_gen,
                                  validation_steps=val_steps)
    draw_histroy(history)


def rnn_gru(float_data, lookback=1440, step=6):
    model = Sequential()
    model.add(layers.GRU(32, input_shape=(None, float_data.shape[-1])))
    model.add(layers.Dense(1))

    model.compile(optimizer=RMSprop(), loss='mae')
    history = model.fit_generator(train_gen,
                                  steps_per_epoch=500,
                                  epochs=20,
                                  validation_data=val_gen,
                                  validation_steps=val_steps)
    draw_histroy(history)


train_data_size = 200000
lookback = 1440
step = 6
float_data = load_data()
normalize_data(float_data, train_data_size)
(train_gen, val_gen, test_gen), (val_steps, test_steps) = create_generator(
    float_data, lookback=1440, step=6)
mae = evaluate_naive_method(val_gen, val_steps)
print(f'the Naive MAE is {mae}')
#  dense_model(float_data, lookback=1440, step=6)
rnn_gru(float_data)
