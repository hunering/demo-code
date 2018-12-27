# name mangling
# Any identifier of the form __spam (at least two leading underscores, 
# at most one trailing underscore) is textually replaced with 
# _classname__spam, where classname is the current class name 
# with leading underscore(s) stripped. 

class Mapping:
    def __init__(self, iterable):
        self.items_list = []
        self.__update(iterable)

    def update(self, iterable):
        for item in iterable:
            self.items_list.append(item)

    __update = update   # private copy of original update() method

class MappingSubclass(Mapping):

    def update(self, keys, values):
        # provides new signature for update(), will overwrite the parent's funtion
        # but does not break __init__()
        super().update((keys, values))
        for item in zip(keys, values):
            self.items_list.append(item)

mapping = Mapping([])
print(mapping.__dict__)
print(dir(mapping))
mapping.update(range(10))

subclass = MappingSubclass([])
print(mapping.__dict__)
print(dir(mapping))
subclass.update("key", "value")

# subclass.update(range(10))

# the following will throw exception,
# because __update is replaced with _Mapping__update
# mapping.__update