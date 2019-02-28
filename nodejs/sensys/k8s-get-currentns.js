var execFileSync = require('child_process').execFileSync;
const fs = require('fs');

function getConfig() {
	//var stdout = execFileSync("kubectl", ["config", "view", "-o", "json"]);
	var stdout = fs.readFileSync("sensys/kube-config.out");
	return JSON.parse(stdout);
}
function getCurrentContext(configJson) {
	return configJson["current-context"];
}
function getCurrentNamespace(configJson) {
	var length = configJson.contexts.length;
	for(var i = 0; i < length; i++) {
		if(configJson.contexts[i].name == getCurrentContext(configJson)) {
			return configJson.contexts[i].context.namespace;
		}
	}
	return null;
}

var ns = getCurrentNamespace(getConfig());
if(ns) {
	console.log(ns);
	//return 0;
} else {
	//return 1;
}