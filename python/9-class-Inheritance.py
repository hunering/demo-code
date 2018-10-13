# For C++ programmers: all methods in Python are effectively virtual.
class Animal:
  def show(self):
    print("This is animal")
  def call_show(self):
    self.show()

class Dog(Animal):
  def show(self):
    print("This is dog")

dog = Dog()
dog.show()
Animal.show(dog)

dog.call_show() # will show "This is dog"

class Runable:
  def run(self):
    print("Running!")

class Run_Dog(Dog, Runable):
  def show(self):
    print("This is Run Dog")

run_dog = Run_Dog()
run_dog.run()
run_dog.show()
