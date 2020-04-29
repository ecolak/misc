import React from 'react';
import {
    IonHeader,
    IonToolbar,
    IonTitle,
    IonContent,
    IonIcon,
    IonLabel,
    IonList,
    IonItem,
    IonPage,
    IonFab,
    IonFabButton,
    IonSearchbar,
    IonSegment,
    IonSegmentButton,
    IonButton,
    IonCol,
    IonRow,
    IonLoading
} from '@ionic/react'

import { userTrips } from './Firebase';

import { add, arrowForwardOutline, boat } from 'ionicons/icons';

// TODO: Add spinner
// TODO: Display placeholder when 0 trips

function Trip(props) {
    const link = '/trip/' + props.details.id;
    return (
        <IonItem href={link}>
            <IonLabel>
                {props.details.origin} <IonIcon icon={arrowForwardOutline}></IonIcon> {props.details.destination}
                <p>{props.details.startDate} / {props.details.endDate}</p>
            </IonLabel>
        </IonItem>
    );
}

class Trips extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            trips: [],
            filteredTrips: [],
            searchText: '',
            sortDir: 'latest',
            loading: false
        };

        this.history = props.history;
        this.search = this.search.bind(this);
        this.sort = this.sort.bind(this);
        this.exportCSV = this.exportCSV.bind(this);
    }

    componentDidMount() {
        this.loadTrips();
    }

    componentDidUpdate(prevProps) {
        if (localStorage.getItem('userId') !== this.userId) {
            this.loadTrips();
        }
    }

    loadTrips() {
        const userId = localStorage.getItem('userId');
        this.userId = userId;
        if (!userId) {
            this.history.push('/login');
            return;
        }

        this.setState({ loading: true });
        userTrips(userId).on("value", (function(snapshot) {       
            this.setState({ loading: false });

            const data = snapshot.val();

            if (data) {
                // Firebase returns a map. Need to turn into array for sorting
                const values = Object.entries(data).map(e => {
                    e[1]["id"] = e[0];
                    return e[1];
                });
                this.setState({
                    trips: values,
                    filteredTrips: this.sortInternal(values, this.state.sortDir)
                });
            } 
        }).bind(this));
    }

    search(event) {
        const searchText = event.target.value.toLowerCase();

        const filtered = this.state.trips.filter((trip) => {
            if (trip.origin.toLowerCase().includes(searchText) ||
                trip.destination.toLowerCase().includes(searchText)) {
                return true;
            }
            return false;
        });
        this.setState({
            searchText: event.target.value,
            filteredTrips: filtered
        });
    }

    sort(event) {
        const sortBy = event.target.value
        const sorted = this.sortInternal(this.state.trips, sortBy);
        this.setState({
            filteredTrips: sorted,
            sortDir: sortBy
        });
    }

    sortInternal(data, sortDir) {
        if (sortDir) {
            let sortFn = null;
            switch (sortDir) {
                case 'earliest':
                    sortFn = (a, b) => a.startDate < b.startDate ? -1 : (b.startDate < a.startDate ? 1 : 0);
                    break;
                case 'latest':
                    sortFn = (a, b) => b.startDate < a.startDate ? -1 : (a.startDate < b.startDate ? 1 : 0);
                    break;
                case 'longest':
                    sortFn = (a, b) => b.distance - a.distance;
                    break;
                case 'shortest':
                    sortFn = (a, b) => a.distance - b.distance;
                    break;
                default:
            }
            if (sortFn) {
                return data.sort(sortFn);
            }
        }
        return data;
    }

    exportCSV(event) {
        console.log('Export trips as CSV');
    }

    render() {
        return (
            <IonPage>
                <IonHeader>
                    <IonToolbar color="primary">
                        <IonTitle><IonIcon icon={boat}></IonIcon> My Logbook</IonTitle>
                    </IonToolbar>
                </IonHeader>
                <IonContent>
                    <IonLoading isOpen={this.state.loading} message={'Please wait...'} />
                    <IonSearchbar value={this.state.searchText} onIonChange={this.search}></IonSearchbar>
                    <IonRow>
                        <IonCol>
                            <IonSegment value={this.state.sortDir} onIonChange={this.sort}>
                                <IonSegmentButton value="latest">
                                    <IonLabel>Latest</IonLabel>
                                </IonSegmentButton>
                                <IonSegmentButton value="earliest">
                                    <IonLabel>Earliest</IonLabel>
                                </IonSegmentButton>
                                <IonSegmentButton value="longest">
                                    <IonLabel>Longest</IonLabel>
                                </IonSegmentButton>
                                <IonSegmentButton value="shortest">
                                    <IonLabel>Shortest</IonLabel>
                                </IonSegmentButton>
                            </IonSegment>
                        </IonCol>
                    
                    </IonRow>
                    
                    
                    <IonItem lines="full">
                        <IonLabel slot="start">{this.state.filteredTrips.length} trips</IonLabel>   
                        <IonButton slot="end" onClick={this.exportCSV}>Export as CSV</IonButton>
                    </IonItem>

                    <IonList>
                        {this.state.filteredTrips.map(t => <Trip details={t} />) }
                    </IonList>
                    <IonFab vertical="bottom" horizontal="end" slot="fixed">
                        <IonFabButton href="/trip/new">
                            <IonIcon icon={add} />
                        </IonFabButton>
                    </IonFab>
                </IonContent>
            </IonPage>
        );
    }
}

export default Trips;