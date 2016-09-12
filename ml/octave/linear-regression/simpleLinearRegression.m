function [newTheta] = simpleLinearRegression(theta, alpha)
  
  # X is the sample, in the format of 
  # X0, X0, X0
  # X1, X1, X1
  
  # Y is the correct value, in the format of 
  # Y1
  # Y2
  # y3
  
  # theta is in the format of 
  # theta0
  # theta1
  # theta2
  
  load("-text", "sample.dat", "X", "Y");
  
  i = 1000;
  while (i >= 0)
   prediction = transpose(theta) * X; # prediction is like [p1, p2, ....]
   JDerivative = X * (transpose(prediction) - Y);
  
   theta = theta - (alpha/columns(X))*JDerivative;
   i--;
  endwhile
  
  newTheta = theta;
endfunction