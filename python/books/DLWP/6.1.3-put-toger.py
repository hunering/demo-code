from keras.preprocessing.text import Tokenizer
from keras.preprocessing.sequence import pad_sequences
from keras import models
from keras import layers
from keras import optimizers
import os
import numpy as np
import matplotlib.pyplot as plt


def load_imdb_data():
    texts = []
    labels = []

    imdb_dir = r'C:\Users\huxiaomi\Downloads\deep-learning\data\imdb\aclImdb'
    train_dir = os.path.join(imdb_dir, 'test')

    for type in ['neg', 'pos']:
        dir_name = os.path.join(train_dir, type)
        for fname in os.listdir(dir_name):
            if fname[-4:] == '.txt':
                f = open(os.path.join(dir_name, fname), encoding="utf8")
                texts.append(f.read())
                f.close()
                if type == 'neg':
                    labels.append(0)
                else:
                    labels.append(1)
    return texts, labels


def get_train_val_matrix(texts, labels, max_features=10000, max_len=100):
    tokenizer = Tokenizer(num_words=max_features)
    tokenizer.fit_on_texts(texts)
    sequens = tokenizer.texts_to_sequences(texts)

    word_index = tokenizer.word_index
    print(f'Found {len(word_index)} unique tokens')

    data = pad_sequences(sequens, maxlen=max_len)

    labels = np.asarray(labels)

    indices = np.arange(data.shape[0])
    np.random.shuffle(indices)

    data = data[indices]
    labels = labels[indices]

    train_sample_n = 20000
    validation_sample_n = 5000

    x_train = data[:train_sample_n]
    x_val = data[train_sample_n:validation_sample_n+train_sample_n]
    y_train = labels[:train_sample_n]
    y_val = labels[train_sample_n:validation_sample_n+train_sample_n]

    return (x_train, y_train), (x_val, y_val), word_index


def prepare_glove(word_index, max_features=10000, embedding_dim=100):

    file_path = r'C:\Users\huxiaomi\Downloads\deep-learning\data\glove\glove.6B.100d.txt'
    f = open(file_path, encoding="utf8")

    embedding_index = {}
    for line in f:
        words = line.split()
        word = words[0]
        vec = np.asarray(words[1:], dtype=np.float)
        embedding_index[word] = vec

    f.close()

    embedding_matrix = np.zeros((max_features, embedding_dim))

    for word, i in word_index.items():
        if i < max_features:
            vec = embedding_index.get(word)
            if vec is not None:
                embedding_matrix[i] = vec

    return embedding_matrix


def build_model(embedding_matrix, max_features=10000, max_len=100, embedding_dim=100):
    model = models.Sequential()
    model.add(layers.Embedding(max_features,
                               embedding_dim, input_length=max_len))
    model.add(layers.Flatten())
    model.add(layers.Dense(32, activation='relu'))
    model.add(layers.Dense(1, activation='sigmoid'))

    model.layers[0].set_weights([embedding_matrix])
    model.layers[0].trainable = False
    return model


max_len = 1000
texts, labels = load_imdb_data()
#  word_index is an dictionary
(x_train, y_train), (x_val, y_val), word_index = get_train_val_matrix(
    texts, labels, max_len=max_len)
embedding_matrix = prepare_glove(word_index)
model = build_model(embedding_matrix, max_len=max_len)
model.compile(optimizer=optimizers.RMSprop(),
              loss='binary_crossentropy', metrics=['acc'])
history = model.fit(x_train, y_train, epochs=10,
                    batch_size=32, validation_data=(x_val, y_val))
model.save_weights('pre_trained_glove_model.h5')

acc = history.history['acc']
val_acc = history.history['val_acc']
epochs = range(1, len(acc) + 1)

plt.plot(epochs, acc, 'bo', label='Trainning acc')
plt.plot(epochs, val_acc, 'b', label='Val acc')
plt.show()
