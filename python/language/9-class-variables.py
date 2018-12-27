# Class and Instance Variables
# instance variables are for data unique to 
# each instance and class variables are for 
# attributes and methods shared by all instances of the class

class Dog:

    kind = 'canine'         # class variable shared by all instances

    def __init__(self, name):
        self.name = name    # instance variable unique to each instance

d = Dog('Fido')
e = Dog('Buddy')
print(d.kind, d.name)
print(e.kind, e.name)
print(e.__class__)

