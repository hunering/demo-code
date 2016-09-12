// pls refer to https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise


Promise.resolve("Success").then(function(value) {
	console.log(value);
}, function(value) {
	console.log("rejected: " + value);
})

var p = Promise.resolve([ 1, 2, 3 ]);
p.then(function(v) {
	console.log(v[0]); // 1
});

var original = Promise.resolve(true);
var cast = Promise.resolve(original);
cast.then(function(v) {
  console.log(v); // true
});