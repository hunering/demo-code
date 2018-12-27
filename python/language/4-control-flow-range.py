# range() function return an iterable object, NOT an list
print(range(10))
print(list(range(5)))
print(list(range(5, 10)))
print(list(range(0, 10, 3)))

a = ['Mary', 'had', 'a', 'little', 'lamb']
for i in range(len(a)):
    print(i, a[i])
