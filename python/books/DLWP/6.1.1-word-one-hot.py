import numpy as np

samples = ['The cat sat on the mat.', 'The dog ate my homework']

token_indx = {}
for sample in samples:
    for word in sample.split():
        if word not in token_indx:
            token_indx[word] = len(token_indx) + 1

max_length = 10
results = np.zeros(shape=(len(samples), max_length, max(token_indx.values())+1))

for i, sample in enumerate(samples):
    for j, word in list(enumerate(sample.split()))[:max_length]:
        results[i, j, token_indx[word]] = 1

print(results)
