import numpy as np
import tensorflow as tf
from keras.backend.tensorflow_backend import set_session
from keras.datasets import imdb

def init_keras():    
    config = tf.ConfigProto()
    config.gpu_options.allow_growth = True
    session = tf.Session(config=config)
    set_session(session)

def imdb_sample2word(sample):
    word_index = imdb.get_word_index()
    word_index_rev = dict([(value, key) for (key, value) in word_index.items()])
    comment = ' '.join([word_index_rev.get(i-3, '?') for i in sample])
    return comment

def vectorize_sequences(sequences, dimension=10000):
    results = np.zeros((sequences.shape[0], dimension))
    for i in range(sequences.shape[0]):
        results[i, sequences[i]] = 1

    return results

def to_one_hot(labels, dimension=46):
    results = np.zeros(labels.shape[0], dimension)
    for i, label in enumerate(labels):
        results[i, label] = 1
    
    return results

def normalize_data(x):
    mean = np.mean(x, axis=0)
    x -= mean
    std = np.std(x, axis=0)
    x /= std