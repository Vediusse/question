import React from 'react';
import ReactDOM from 'react-dom/client';
import './style/index.css'; // Подключаем стили
import App from './App';
import reportWebVitals from './reportWebVitals';

import {UserProvider} from "./services/UserContext";

const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);

root.render(
  <React.StrictMode>
      <UserProvider>
      <App />
      </UserProvider>
  </React.StrictMode>
);


reportWebVitals();
