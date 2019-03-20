from keras.preprocessing.text import Tokenizer
from keras.preprocessing.text import one_hot

samples = ['The cat sat on the mat', 'The dog ate my homework']

tokenizer = Tokenizer(num_words=1000)
tokenizer.fit_on_texts(samples)

sequences = tokenizer.texts_to_sequences(samples)
one_hot_results = tokenizer.texts_to_matrix(samples, mode='binary')
print(one_hot_results)
word_index = tokenizer.word_index

print(f'Found {len(word_index)} unique tokens')
