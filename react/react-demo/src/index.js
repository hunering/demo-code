import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import Game from './App';
import Clock from "./Clock";
import NameForm from "./NameForm";

import registerServiceWorker from './registerServiceWorker';

//ReactDOM.render(<App />, document.getElementById('root'));

ReactDOM.render(
  <Game />,
  document.getElementById('root')
);
ReactDOM.render(
  <Clock />,
  document.getElementById('clock')
);

ReactDOM.render(
  <NameForm />,
  document.getElementById('nameForm')
);

registerServiceWorker();
