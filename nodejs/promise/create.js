'use strict';
// pls refer to https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise

var promiseCount = 0;


var thisPromiseCount = ++promiseCount;

// We make a new promise: we promise a numeric count of this promise,
// starting from 1 (after waiting 3s)
var p1 = new Promise(
    // The resolver function is called with the ability to resolve or
    // reject the promise
    function(resolve, reject) {
        console.log("start the promise");
        // This is only an example to create asynchronism
        setInterval(
            function() {
                // We fulfill the promise !
                resolve(thisPromiseCount);
            }, Math.random() * 2000 + 1000);
        
    }
);

// We define what to do when the promise is resolved/fulfilled with the
// then() call,
// and the catch() method defines what to do if the promise is rejected.
p1.then(
    // Log the fulfillment value
    function(val) {
    	console.log("Promise fulfilled, Async code terminated");
    })
.catch(
    // Log the rejection reason
    function(reason) {
        console.log('Handle rejected promise ('+reason+') here.');
    });


