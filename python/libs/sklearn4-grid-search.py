import sklearn.datasets
import numpy as np
from sklearn.neighbors import KNeighborsClassifier
from sklearn.metrics import accuracy_score
from sklearn.model_selection import train_test_split
from sklearn.model_selection import GridSearchCV

digits = sklearn.datasets.load_digits()

X = digits.data
y = digits.target

X_train,X_test,y_train,y_test = train_test_split(X,y)

params = [
    {
        'weights':['distance'],
        'n_neighbors':[i for i in range(1,11)],
        'p':[i for i in range(1,6)]    
    },
    {
        'weights':['uniform'],
        'n_neighbors':[i for i in range(1,11)]
    }
]

knn_clf = KNeighborsClassifier()

grid_search = GridSearchCV(knn_clf,params)#传入knn算法对象和参数集合

grid_search.fit(X_train,y_train)  #传入数据集，这行代码我感觉运行了一年

print(grid_search.best_estimator_)   #显示最佳参数模型
print(grid_search.best_params_)  #显示最佳的超参数
print(grid_search.best_score_ ) #显示最佳的正确率
