
# A namespace is a mapping from names to objects. 
# Most namespaces are currently implemented as Python dictionaries, 
def scope_test():
    def do_local():
        spam = "local spam"

    def do_nonlocal():
        # The nonlocal statement causes the listed identifiers to 
        # refer to previously bound variables in the nearest 
        # enclosing scope excluding globals. 
        nonlocal spam
        spam = "nonlocal spam"

    def do_global():
        # The global statement is a declaration which holds for 
        # the entire current code block. 
        # It means that the listed identifiers are to be interpreted as globals. 
        global spam
        spam = "global spam"

    spam = "test spam"
    do_local()
    print("After local assignment:", spam)
    do_nonlocal()
    print("After nonlocal assignment:", spam)
    do_global()
    print("After global assignment:", spam)
spam = "original global spam"
scope_test()
print(spam)
