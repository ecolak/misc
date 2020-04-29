import React from 'react';
import {
    IonPage, IonContent, IonImg, IonButton, IonRow, IonCol,
    IonCard, IonCardContent, IonCardHeader, IonCardTitle
} from '@ionic/react'

import { firebaseAuth, googleAuthProvider } from './Firebase';

import logo from './img/logo.png'

function Login(props) {

    function login() {
        firebaseAuth.signInWithPopup(googleAuthProvider).then(function (result) {
            // This gives you a Google Access Token. You can use it to access the Google API.
            // var token = result.credential.accessToken;
            // The signed-in user info.
            var user = result.user;
            localStorage.setItem('userId', `GO-${user.uid}`);
            props.history.push('/');
        }).catch(function (error) {
            console.log("login failed");
            console.log(error);
        });
    }

    return (
        <IonPage>
            <IonContent>

                <IonRow>
                    <IonCol></IonCol>
                    <IonCol>
                        <IonCard>
                            <IonImg src={logo} />
                            <IonCardHeader>
                                <IonCardTitle>My Logbook</IonCardTitle>
                            </IonCardHeader>
                            <IonCardContent>
                                <IonButton onClick={login}>Login with Google</IonButton>
                            </IonCardContent>
                        </IonCard>

                    </IonCol>
                    <IonCol></IonCol>
                </IonRow>
            </IonContent>
        </IonPage>
    );
}

export default Login;