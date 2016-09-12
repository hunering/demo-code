// pls refer to https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise

// Because the then method returns a Promise, 
// you can easily chain then calls. 
// Values returned from the onFulfilled or onRejected callback functions 
// will be automatically wrapped in a resolved promise.
var p2 = new Promise(function(resolve, reject) {
  resolve(1);
});

p2.then(function(value) {
  console.log(value); // 1
  return value + 1;
}).then(function(value) {
  console.log(value); // 2
});

p2.then(function(value) {
  console.log(value); // 1
});