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

function App() {
  return (
    <IonApp>
      <IonReactRouter>
        <IonRouterOutlet>
          <Route path="/trips" component={Trips} exact />
          <Route path="/trip/:id" component={AddTrip}/>
          <Redirect exact from="/" to="/trips" />
        </IonRouterOutlet>
      </IonReactRouter>
    </IonApp>
  );
}

export default App;
