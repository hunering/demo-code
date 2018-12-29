import numpy as np
import pandas as pd

s = pd.Series([0, 1, 2, 3, 4])
print(s)
print(s.index)
print(s.values)

d = {'a': 1, 'b': 2, 'c': 3}
s = pd.Series(d, index=['a', 'b', 'c', 'd'])
print(s)

s['d'] = 4
s = s.astype(np.int8)
print(s)

print(s['b':'c']) # not like typical python, inclusive!

print(s[s>1]) # Boolean indexing

d1 = pd.Series({'a': 1, 'b': 2, 'c': 3})
d2 = pd.Series({'a': 1, 'c': 2, 'b': 3})
print(d1+d2) # pandas try to match the index