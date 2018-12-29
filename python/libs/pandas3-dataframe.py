import pandas as pd

df =  pd.DataFrame([[1,2,3], [4,5,6]], index=['a','b'], columns=['x','y','z'])
print(df)

df = pd.DataFrame([{'a':1,'b':2}, {'a':4, 'b':5, 'c':6}])
print(df)

df = pd.DataFrame({'one':pd.Series([1,2,3]), 'two':pd.Series([4,5,6])})
print(df)
print(df.index)
print(df.columns)

df = pd.DataFrame({'name': ['allen','jim','vivian'], 'age':[30,35,40], 'sex':['m','m','f']})
print(df)

print(df.set_index('name'))
df.set_index('name', inplace=True)
print(df)
df.reset_index(inplace=True)

print(df['name']) # return the columns
print(df.name)

print(df.loc[0]) # return the first row

df.drop(0, inplace=True) # delete the first row
print(df)

df.drop('name', inplace=True, axis=1) # delete the name column
print(df)

df = pd.DataFrame({'name': ['allen','jim','vivian'], 'age':[30,35,40], 'sex':['m','m','f']})
df.to_csv('df.csv')

del df
df = pd.read_csv('df.csv')