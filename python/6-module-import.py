from fibonacci import fib, fib2
import fibonacci as fiblib
fib2(1000)
fiblib.fib(1000)

# import the 'parent_package/sub_package/foo.py'
import parent_package.sub_package.foo
parent_package.sub_package.foo.sudo_func()

from parent_package.sub_package.foo import sudo_func
sudo_func()

from parent_package.sub_package.bar import bar_func
bar_func()
