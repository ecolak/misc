import React from 'react';
import {
  IonApp,
  IonRouterOutlet
} from '@ionic/react'

import { IonReactRouter } from '@ionic/react-router';

import './App.css';
import { Route, Redirect } from 'react-router-dom';

import AddTrip from './AddTrip';
import Trips from './Trips';
import Login from './Login';

function App() {
  return (
    <IonApp>
      <IonReactRouter>
        <IonRouterOutlet>
        <Route path="/login" component={Login} exact />
          <Route path="/trips" component={Trips} exact />
          <Route path="/trip/:id" component={AddTrip}/>
          <Redirect exact from="/" to="/trips" />
        </IonRouterOutlet>
      </IonReactRouter>
    </IonApp>
  );
}

export default App;
