/**
 * Huxiaoming
 */
const fs = require('fs');

function readTrainningSet(fileName) {
	var fileContent = fs.readFileSync(fileName, 'utf8', 'r');
	return fileContent;
}

function loadTrainningSet(fileName) {
	var trainningSetString = readTrainningSet(fileName);
	var itemList = trainningSetString.split('\n');
	var trainningSet = [];
	for(var item in itemList) {
		
		var itemValues = itemList[item].split(',');
		var tsItem = {x : parseFloat(itemValues[0]), y : parseFloat(itemValues[1])};
		trainningSet.push(tsItem);
	}
	
	return trainningSet;
}

// we guess our hypothesis is Y=θ0+θ1X 
function gradientDescent(trainningSet) {
	
	var theta0 = 0.0, theta1 = 0.0;
	var alpha = 0.01;
	
	var i = 1000;
	var precision = 0.00001;
	
	while(i > 0) {
		var tempTheta0 = theta0 - alpha * calcDelta4Theta0(trainningSet, theta0, theta1);
		var tempTheta1 = theta1 - alpha * calcDelta4Theta1(trainningSet, theta0, theta1);
		
		var stepUpdateDelta0 = Math.abs(tempTheta0 - theta0);
		var stepUpdateDelta1 = Math.abs(tempTheta1 - theta1);
		
		theta0 = tempTheta0;
		theta1 = tempTheta1;
		
		if(stepUpdateDelta0 <= precision && stepUpdateDelta1 <= precision) {
			break;
		} else {
			i--;			
		}
	}
	
	return {theta0: theta0, theta1: theta1}
}

function calcDelta4Theta0(trainningSet, theta0, theta1) {
	var delta = 0;
	for(var i in trainningSet) {
		var currentSample = trainningSet[i];
		var oneSampleDelta = calcHypothesisValue(theta0, theta1, currentSample.x) 
				- currentSample.y;
		delta += oneSampleDelta;
	}
	
	return delta/trainningSet.length;
}

function calcDelta4Theta1(trainningSet, theta0, theta1) {
	var delta = 0;
	for(var i in trainningSet) {
		var currentSample = trainningSet[i];
		var oneSampleDelta = (calcHypothesisValue(theta0, theta1, currentSample.x) 
				- currentSample.y)*currentSample.x;
		
		delta += oneSampleDelta;
	}
	
	return delta/trainningSet.length;
}

function calcHypothesisValue(theta0, theta1, x) {
	return theta0 + theta1 * x;
}


// our correct hypothesis y = 5 + 3x
var trainningSet = loadTrainningSet('linear-regression\\trainning-set.txt');
var result = gradientDescent(trainningSet);

console.log("Theta0: " + result.theta0 + "; Theta1: " + result.theta1);
