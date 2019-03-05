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

def imdb_vectorize_sequences(sequences, dimension=10000):
    results = np.zeros((sequences.shape[0], dimension))
    for i in range(sequences.shape[0]):
        results[i, sequences[i]] = 1

    return results