import axios from 'axios';

const ApiClient = axios.create({
    baseURL: 'https://v0t6ppyvmd.execute-api.us-east-1.amazonaws.com/default/api/trip',
    headers: {'Authorization': 'S3tWy6Qkqj37LGtD', 'X-USER-ID': 'US-1234'}
});

export default ApiClient;