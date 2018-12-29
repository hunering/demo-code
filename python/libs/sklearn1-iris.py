from sklearn.datasets import load_iris
import pandas as pd
import matplotlib.pyplot as plt
from pandas.plotting import scatter_matrix

iris = load_iris()
print(iris)
print(iris.data)
print(iris.feature_names)
print(iris.target)
print(iris.target_names)

X = iris.data
y = iris.target

df = pd.DataFrame(X, columns=iris.feature_names)
df['class'] = y
print(df)
print(df.describe())

scatter_matrix(df)
plt.show()
