/**
 * http://usejsdoc.org/
 */
// The string:
var str = "Hello world!";

// Look for "Hello"
var patt = /Hello/g;
var result = patt.exec(str);

// Look for "W3Schools"
var patt2 = /W3Schools/g;
result2 = patt2.exec(str);

var expression = "(100-$sigar_cpu_idle$)";
var patt3 = /\$\w*\$/g;
result3 = patt3.exec(expression);
patt3 = /\$.*\$/g;
result3 = patt3.exec(expression);
console.log(result3);
