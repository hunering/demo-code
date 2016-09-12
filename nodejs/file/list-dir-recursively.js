/**
 * Huxiaoming
 */

const fs = require('fs');
const path = require('path');
var rootDir = "c:/temp";


//listDir(rootDir, function() {
//	console.log("done");
//});
exports.listDir = listDir;

function listDir(rootDir, done) {
	fs.readdir(rootDir, (err, files) => {
		var pending = {length: files.length};
		if(pending.length <= 0) {
			done();
		}
		for(var i in files) {
			var file = files[i];
			var fileFullPath = path.join(rootDir, file);
			
			fs.stat(fileFullPath, dealOneFile.bind(undefined, fileFullPath, pending, done));
			
		}
	})
}

function dealOneFile(fileName, pending, done, err, stats) {
	console.log("pending is: " + pending.length);
	if(stats.isDirectory()) {
		console.log("Is dir: " + fileName);
		listDir(fileName, function() {
			pending.length--;
			if(pending.length <= 0) {
				done();
			}
		});
	} else {
		console.log(fileName);		
		pending.length--;
		if(pending.length <= 0) {
			done();
		}
	}
}