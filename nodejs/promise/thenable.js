// pls refer to https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise

// Resolving a thenable object
var p1 = Promise.resolve({
	then : function(onFulfill, onReject) {
		onFulfill("fulfilled!");
	}
});
console.log(p1 instanceof Promise) // true, object casted to a Promise

p1.then(function(v) {
	console.log(v); // "fulfilled!"
}, function(e) {
	// not called
});

// Thenable throws before callback
// Promise rejects
var thenable = {
	then : function(resolve) {
		throw new TypeError("Throwing");
		resolve("Resolving");
	}
};

var p2 = Promise.resolve(thenable);
p2.then(function(v) {
	// not called
}, function(e) {
	console.log(e); // TypeError: Throwing
});

// Thenable throws after callback
// Promise resolves
var thenable = {
	then : function(resolve) {
		resolve("Resolving");
		throw new TypeError("Throwing");
	}
};

var p3 = Promise.resolve(thenable);
p3.then(function(v) {
	console.log(v); // "Resolving"
}, function(e) {
	// not called
});