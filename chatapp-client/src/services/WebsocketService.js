import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

class WebsocketService {
    constructor() {
        this.client = null;
        this.username = null;
        this.token = null;
    }

    connect = (url, username, token, onMessageReceived, onError, onUsersReceived) => {
        this.username = username;
        this.token = token;
        const socket = new SockJS(url);
        this.client = new Client({
            webSocketFactory: () => socket,
            connectHeaders: {
                Authorization: `Bearer ${token}`
            },
            onConnect: (frame) => {
                console.log('Connected: ' + frame);
                this.client.subscribe('/topic/public', (message) => {
                    if (message.body) {
                        onMessageReceived(JSON.parse(message.body));
                    }
                });

                this.client.subscribe('/topic/users', (message) => {
                    if (message.body) {
                        onUsersReceived(JSON.parse(message.body));
                    }
                });

                // Send the connect message with the token
                this.sendMessage('/app/connect', token);
            },
            onStompError: (frame) => {
                console.error('Broker reported error: ' + frame.headers['message']);
                onError('Connection error. Please try again later.');
            }
        });

        this.client.activate(); // Activate the client to connect
    }

    sendMessage = (destination, message) => {
        if (this.client && this.client.connected) {
            const payload = JSON.stringify({ content: message });
            console.log(`Sending to ${destination}:`, payload);
            this.client.publish({
                destination,
                body: payload,
                headers: {
                    Authorization: `Bearer ${this.token}`
                }
            });
        }
    }

    disconnect = () => {
        if (this.client) {
            // Send the disconnect message before closing the connection
            this.sendMessage('/app/disconnect', this.username);
            this.client.deactivate(); // Deactivate the client
            this.username = null;
            this.token = null;
        }
    }
}


const websocketServiceInstance = new WebsocketService();
export default websocketServiceInstance;