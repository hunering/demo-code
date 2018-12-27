
retry = 2
while retry > 0:
    try:
        var = int(input("Pls input an number: "))
    except ValueError as inst:
        print(type(inst))    # the exception instance
        print(inst.args)     # arguments stored in .args
        print(inst)     
        print("Opps, try again")
        retry = retry - 1
    except (RuntimeError, TypeError, NameError):
        pass
    except:
        print("Unexpected error:")
        raise
    else:
        print("No exception detected")
    finally:
        print("touch finally section")

class B(Exception):
    pass

class C(B):
    pass

class D(C):
    pass

for cls in [B, C, D]:
    try:
        raise cls()
    except D:
        print("D")
    except C:
        print("C")
    except B:
        print("B")

for cls in [B, C, D]:
    try:
        raise cls()
    except B:
        print("B")
    except C:
        print("C")
    except D:
        print("D")


#  Predefined Clean-up Actions
# After the statement is executed, the file f is always closed, 
# even if a problem was encountered while processing the lines. 
# Objects which, like files, provide predefined clean-up actions 
# will indicate this in their documentation.
with open("myfile.txt") as f:
    for line in f:
        print(line, end="")