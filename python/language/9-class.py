
class Sample_Class:
    """This is the sample class"""
    i = 12345
    def func(self):
        print("This is from sample class")

    def __init__(self):
        self.data = []

class Complex_Number:
    def __init__(self, i, j):
        self.i = i
        self.j = j
    def show(self):
        print((self.i, self.j))


obj = Sample_Class()
obj.func()

cn = Complex_Number(1,1)
cn.show()

cn.counter = 1
while cn.counter < 10:
    cn.counter = cn.counter * 2
print(cn.counter)
del cn.counter

# method object
# cn.show is a valid method reference, since Complex_Number.show is a function
# https://docs.python.org/3/tutorial/classes.html#instance-objects
cnF = cn.show
cnF()
Complex_Number.show(cn)
