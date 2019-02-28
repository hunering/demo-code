const libxml = require('libxmljs');
const fs = require('fs');
const process = require('process');
var pd = require('pretty-data').pd;
var prompt = require('prompt-sync')();

if (process.argv.length >= 9) {
	var i = 2;
	var templateModelFile = process.argv[i++];
	var modelFileName = process.argv[i++];
	var totalNodes = process.argv[i++];
	var rowCount = process.argv[i++];
	var rackCountPerRow = process.argv[i++];
	var nodeCountPerRack = process.argv[i++];
	var sensysAggregatorName = process.argv[i++];
	var sensysNodeNamePrefix = process.argv[i++];
} else {

}


var aggregatorPort = 55805;
var nodePort = 55810;
var schedulerName = sensysAggregatorName;
var schedulerPort = 55820;

var templateFileContent = fs.readFileSync(templateModelFile);
var docTemplate = libxml.parseXmlString(templateFileContent);

var clusterElement = docTemplate.get('/configuration/junction');

var currentNode = 0;
for(var currentRow = 0; currentRow < rowCount; currentRow++) {
	var rowElement = clusterElement.node('junction');
	rowElement.node('type', 'row');
	rowElement.node('name', 'row'+currentRow);
	for(var currentRack = 0; currentRack < rackCountPerRow; currentRack ++) {
		var rackElement = rowElement.node('junction');
		rackElement.node('type', 'rack');
		rackElement.node('name', 'row'+currentRow+'rack'+currentRack);
		var controllerElement = rackElement.node('controller');
		controllerElement.node('host', sensysAggregatorName);
		controllerElement.node('port', aggregatorPort.toString());
		controllerElement.node('aggregator', 'yes');
		
		for(var node4CurrentRack = 0; 
			(node4CurrentRack < nodeCountPerRack) && (currentNode < totalNodes);
			currentNode++, node4CurrentRack++) {
			var nodeElement = rackElement.node('junction');
			nodeElement.node('type', 'node');
			nodeElement.node('name', sensysNodeNamePrefix+currentNode);
			var controllerElement = nodeElement.node('controller');
			controllerElement.node('host', sensysNodeNamePrefix+currentNode);
			controllerElement.node('port', nodePort.toString());
			controllerElement.node('aggregator', 'no');			
		}
	}	
}

var rootElement = docTemplate.get('/configuration');
var schedulerElement = rootElement.node('scheduler');
schedulerElement.node('shost', sensysAggregatorName);
schedulerElement.node('port', schedulerPort.toString());

// console.log("going to save " + modelFileName); 
fs.writeFile(modelFileName, pd.xml(docTemplate.toString()));
