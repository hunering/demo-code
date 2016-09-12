/**
 * Huxiaoming
 */

var fileOper = require('./list-dir-recursively.js');

console.log("dddd");

var promise = new Promise(function(resolve, reject) {
	fileOper.listDir("c:/temp", function(){
		resolve("done");
	});
});



promise.then(function(value) {
	console.log("we got:" + value);
})