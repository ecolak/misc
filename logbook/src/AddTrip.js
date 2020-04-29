import React from 'react';
import {
    IonHeader,
    IonToolbar,
    IonTitle,
    IonContent,
    IonLabel,
    IonList,
    IonItem,
    IonInput,
    IonPage,
    IonDatetime,
    IonTextarea,
    IonButton,
    IonButtons,
    IonBackButton,
    IonAlert,
    IonLoading
} from '@ionic/react'

import {userTrips, singleTrip} from './Firebase';

// TODO: Add spinner
// TODO: Disable submit and delete buttons while in progress

class AddTrip extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            id: '',
            origin: '',
            destination: '',
            startDate: '',
            startTime: '',
            endDate: '',
            endTime: '',
            distance: '',
            duties: '',
            vesselName: '',
            conditions: '',
            otherDetails: '',

            showDeleteAlert: false,
            loading: false
        }

        this.history = props.history;
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.showDeleteAlert = this.showDeleteAlert.bind(this);
        this.hideDeleteAlert = this.hideDeleteAlert.bind(this);
        this.deleteTrip = this.deleteTrip.bind(this);
    }

    componentDidMount() {
        const userId = localStorage.getItem('userId');
        if (!userId) {
            this.history.push('/login');
            return;
        }
        this.userId = userId;

        const pathParams = this.props.match.params;

        if (pathParams.id && pathParams.id !== "new") {
            this.setState({ loading: true });

            singleTrip(userId, pathParams.id).on("value", (function(snapshot) {
                this.setState({ loading: false });

                const data = snapshot.val();
    
                if (data) {
                    this.setState({
                        id: pathParams.id,
                        origin: data.origin,
                        destination: data.destination,
                        startDate: data.startDate,
                        endDate: data.endDate,
                        startTime: data.startTime,
                        endTime: data.endTime,
                        distance: data.distance,
                        duties: data.duties,
                        vesselName: data.vesselName,
                        conditions: data.conditions,
                        otherDetails: data.otherDetails
                    });
                } 
            }).bind(this));
        }
    }

    handleChange(event) {
        this.setState({
            [event.target.name]: event.target.value
        })
    }

    handleSubmit(event) {
        const ddlen = 10; // YYYY-MM-DD
        const tlen = 11;  // 2020-04-16T08:00:00.858-07:00

        const sd = this.state.startDate;
        const fsd = sd && sd.length >= ddlen ? sd.substring(0, ddlen) : '';
        const ed = this.state.endDate;
        const fed = ed && ed.length >= ddlen ? ed.substring(0, ddlen) : '';
        const st = this.state.startTime;
        const fst = st && fsd ? fsd + "T" + st.substring(tlen) : '';
        const et = this.state.endTime;
        const fet = et && fed ? fed + "T" + et.substring(tlen) : '';
       
        // TODO: Verify ed is after sd
        const id = this.state.id;

        const data = {
            origin: this.state.origin,
            destination: this.state.destination,
            startDate: fsd,
            startTime: fst,
            endDate: fed,
            endTime: fet,
            distance: this.state.distance ? parseInt(this.state.distance) : 0,
            duties: this.state.duties,
            vesselName: this.state.vesselName,
            conditions: this.state.conditions,
            otherDetails: this.state.otherDetails
        }

        if (id) {
            singleTrip(this.userId, id).set(data, (function(error) {
                if (error) {
                    console.log(error);
                } else {
                    console.log("Successfully saved trip");
                    this.history.push('/trips');
                }
            }).bind(this));
        } else {
            userTrips(this.userId).push().set(data, (function(error) {
                if (error) {
                    console.log(error);
                } else {
                    console.log("Successfully saved trip");
                    this.history.push('/trips');
                }
            }).bind(this));
        }

        event.preventDefault();
    }

    showDeleteAlert() {
        this.setState({
            showDeleteAlert: true
        })
    }

    hideDeleteAlert() {
        this.setState({
            showDeleteAlert: false
        })
    }

    deleteTrip() {
        singleTrip(this.userId, this.state.id).remove((function(error) {
            if (error) {
                console.log(error);
            } else {
                console.log("Successfully removed trip");
                this.history.push('/trips');
            }
        }).bind(this));
    }

    render() {
        return (
            <IonPage>
                <IonHeader>
                    <IonToolbar color="primary">
                        <IonButtons slot="start">
                            <IonBackButton defaultHref="/trips" />
                        </IonButtons>
                        <IonTitle>{this.state.id === '' ? 'Add' : 'Edit'} Trip</IonTitle>
                    </IonToolbar>
                </IonHeader>
                <IonContent>
                    <IonLoading isOpen={this.state.loading} message={'Please wait...'} />
                    <form onSubmit={this.handleSubmit}>
                        <IonList lines="full">
                            <IonItem>
                                <IonLabel position="floating">Origin</IonLabel>
                                <IonInput name="origin" value={this.state.origin} onIonChange={this.handleChange}  required></IonInput>
                            </IonItem>
                            <IonItem>
                                <IonLabel position="floating">Destination</IonLabel>
                                <IonInput name="destination" value={this.state.destination} onIonChange={this.handleChange} required></IonInput>
                            </IonItem>
                            <IonItem>
                                <IonLabel>Start Date</IonLabel>
                                <IonDatetime name="startDate" value={this.state.startDate} onIonChange={this.handleChange} displayFormat="YYYY-MM-DD" required></IonDatetime>
                            </IonItem>
                            <IonItem>
                                <IonLabel>Start Time</IonLabel>
                                <IonDatetime name="startTime" value={this.state.startTime} onIonChange={this.handleChange} displayFormat="h:mm A" pickerFormat="h:mm A"></IonDatetime>
                            </IonItem>
                            <IonItem>
                                <IonLabel>End Date</IonLabel>
                                <IonDatetime name="endDate" value={this.state.endDate} onIonChange={this.handleChange} displayFormat="YYYY-MM-DD" required></IonDatetime>
                            </IonItem>
                            <IonItem>
                                <IonLabel>End Time</IonLabel>
                                <IonDatetime name="endTime" value={this.state.endTime} onIonChange={this.handleChange} displayFormat="h:mm A" pickerFormat="h:mm A"></IonDatetime>
                            </IonItem>
                            <IonItem>
                                <IonLabel>Distance</IonLabel>
                                <IonInput type="number" name="distance" value={this.state.distance} onIonChange={this.handleChange}></IonInput>
                                <IonLabel>NM</IonLabel>
                            </IonItem>
                            <IonItem>
                                <IonLabel position="floating">Duties</IonLabel>
                                <IonInput name="duties" value={this.state.duties} onIonChange={this.handleChange}></IonInput>
                            </IonItem>
                            <IonItem>
                                <IonLabel position="floating">Vessel Name</IonLabel>
                                <IonInput name="vesselName" value={this.state.vesselName} onIonChange={this.handleChange}></IonInput>
                            </IonItem>
                            <IonItem>
                                <IonLabel position="floating">Weather & Sea Conditions</IonLabel>
                                <IonTextarea name="conditions" value={this.state.conditions} onIonChange={this.handleChange}></IonTextarea>
                            </IonItem>
                            <IonItem>
                                <IonLabel position="floating">Other Details</IonLabel>
                                <IonTextarea name="otherDetails" rows="6" value={this.state.otherDetails} onIonChange={this.handleChange}></IonTextarea>
                            </IonItem>
                            <IonButton type="submit" expand="block">Submit</IonButton>
                        </IonList>
                    </form>
                    { this.state.id ? <IonButton color="danger" expand="block" onClick={this.showDeleteAlert}>Delete</IonButton>  : ''}
                </IonContent>
                <IonAlert
                    isOpen={this.state.showDeleteAlert}
                    onDidDismiss={this.hideDeleteAlert}
                    header={'Delete this trip'}
                    message={'Are you sure?'}
                    buttons={[
                        {
                            text: 'Cancel',
                            role: 'cancel'
                        },
                        {
                            text: 'Delete',
                            handler: this.deleteTrip
                        }
                    ]}
                />
            </IonPage>
        );
    }
}

export default AddTrip;