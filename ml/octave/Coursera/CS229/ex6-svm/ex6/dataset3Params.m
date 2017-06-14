function [C, sigma] = dataset3Params(X, y, Xval, yval)
%DATASET3PARAMS returns your choice of C and sigma for Part 3 of the exercise
%where you select the optimal (C, sigma) learning parameters to use for SVM
%with RBF kernel
%   [C, sigma] = DATASET3PARAMS(X, y, Xval, yval) returns your choice of C and 
%   sigma. You should complete this function to return the optimal C and 
%   sigma based on a cross-validation set.
%

% You need to return the following variables correctly.
C = 1;
sigma = 0.3;

% ====================== YOUR CODE HERE ======================
% Instructions: Fill in this function to return the optimal C and sigma
%               learning parameters found using the cross validation set.
%               You can use svmPredict to predict the labels on the cross
%               validation set. For example, 
%                   predictions = svmPredict(model, Xval);
%               will return the predictions on the cross validation set.
%
%  Note: You can compute the prediction error using 
%        mean(double(predictions ~= yval))
%
minError = size(Xval, 1);
C_pre = 0.01;

for i = 1:10
  sigma_pre = 0.01;
  for j = 1:10
    model= svmTrain(X, y, C_pre, @(x1, x2) gaussianKernel(x1, x2, sigma_pre));
    predictions = svmPredict(model, Xval);
    currentError = mean(double(predictions ~= yval));
    if(currentError < minError),
      minError = currentError;
      C = C_pre;
      sigma = sigma_pre;
    end    
    
    sigma_pre = sigma_pre*3;
  endfor
  
  C_pre = C_pre*3;
endfor






% =========================================================================

end
