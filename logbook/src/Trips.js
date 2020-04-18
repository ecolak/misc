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
    IonSelect,
    IonSelectOption,
    IonRow,
    IonCol
} from '@ionic/react'

import { add, arrowForwardOutline, funnel, boat } from 'ionicons/icons';

import axios from 'axios';

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

const TRIPS_API_URL = 'http://localhost:4000/trips';

class Trips extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            trips: [],
            filteredTrips: [],
            searchText: '',
            sortDir: 'latest'
        };

        this.search = this.search.bind(this);
        this.sort = this.sort.bind(this);
    }

    componentDidMount() {
        axios.get(TRIPS_API_URL).then(response => response.data)
            .then((data) => {
                this.setState({
                    trips: data,
                    filteredTrips: this.sortInternal(data, this.state.sortDir)
                });
            })
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
            switch(sortDir) {
                case 'earliest': 
                    sortFn = (a, b) => a.startDate < b.startDate ? -1 : (b.startDate < a.startDate ? 1 : 0);
                    break;
                case 'latest':
                    sortFn = (a, b) => b.startDate < a.startDate ? -1 : (a.startDate < b.startDate ? 1 : 0);
                    break;
                case 'longest':
                    sortFn = (a, b) =>  b.distance - a.distance;
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

    render() {
        return (
            <IonPage>
                <IonHeader>
                    <IonToolbar color="primary">
                        <IonTitle><IonIcon icon={boat}></IonIcon> My Logbook</IonTitle>
                    </IonToolbar>
                </IonHeader>
                <IonContent>
                    <IonRow>
                        <IonCol size="9">
                            <IonSearchbar value={this.state.searchText} onIonChange={this.search}></IonSearchbar>
                        </IonCol>
                        <IonCol size="3">
                            <IonItem>   
                                <IonLabel><IonIcon icon={funnel}></IonIcon></IonLabel>
                                <IonSelect name="sortOption" interface="popover" value={this.state.sortDir} onIonChange={this.sort}>
                                    <IonSelectOption value="latest">Latest</IonSelectOption>
                                    <IonSelectOption value="earliest">Earliest</IonSelectOption>
                                    <IonSelectOption value="longest">Longest</IonSelectOption>
                                    <IonSelectOption value="shortest">Shortest</IonSelectOption>
                                </IonSelect>
                            </IonItem>
                        </IonCol>           
                    </IonRow>       
                    <IonList>    
                        {this.state.filteredTrips.map((t) => <Trip details={t} />)}
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