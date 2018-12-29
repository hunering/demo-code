from sklearn.datasets import load_iris
import pandas as pd
import matplotlib.pyplot as plt
from pandas.plotting import scatter_matrix
from sklearn.neighbors import KNeighborsClassifier

iris = load_iris()

X = iris.data
y = iris.target

knn = KNeighborsClassifier()
knn.fit(X, y)
print(knn.predict([[3,5,4,2]]))