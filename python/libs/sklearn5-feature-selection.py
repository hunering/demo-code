from pandas import DataFrame
from sklearn.feature_selection import VarianceThreshold

df = DataFrame({'a':[10,20,30],'b':[100,200,300], 'c':[1,1,1]})
print(df)
# remove features in which more than 80% are the same
sel = VarianceThreshold(threshold=0.8*(1-0.8))
result = sel.fit_transform(df)
print(result)

