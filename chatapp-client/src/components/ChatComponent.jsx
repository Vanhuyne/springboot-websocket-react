import React, { useState, useCallback, useEffect } from 'react';
import WebsocketService from '../services/WebsocketService';
import Login from './Login';
import UserList from './UserListComponent';

const ChatComponent = () => {
    const [messages, setMessages] = useState([]);
    const [users, setUsers] = useState([]);
    const [error, setError] = useState(null);
    const [isConnected, setIsConnected] = useState(false);
    const [username, setUsername] = useState('');
    const [inputMessage, setInputMessage] = useState('');
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [token, setToken] = useState('');

    const handleLogin = (username, token) => {
        setUsername(username);
        setToken(token);
        setIsLoggedIn(true);
        handleConnect(username, token);
    }

    const handleConnect = useCallback((username, token) => {
        WebsocketService.connect(
            'http://localhost:8080/chat',
            username,
            token,
            (message) => {
                console.log('Received message:', message);
                setMessages((prevMessages) => [...prevMessages, message]);
            },
            (errorMessage) => {
                setError(errorMessage);
                setIsConnected(false);
            },
            (userList) => {
                console.log('Received user list:', userList);
                setUsers(userList);
            }
        );

        setIsConnected(true);
        setError(null);
    }, []);

    const handleDisconnect = useCallback(() => {
        WebsocketService.disconnect();
        setIsConnected(false);
        setMessages([]);
        setUsers([]);
        setIsLoggedIn(false);
        setToken('');
    }, []);

    const sendMessage = useCallback(() => {
        if (!inputMessage.trim()) return;

        try {
            WebsocketService.sendMessage('/app/sendMessage', inputMessage);
            setInputMessage('');
            setError(null);
        } catch (err) {
            setError('Error sending message: ' + err.message);
        }
    }, [inputMessage]);

    useEffect(() => {
    }, [
        isConnected,
        handleDisconnect
    ]);

    if (!isLoggedIn) {
        return <Login onLogin={handleLogin} />;
    }

    return (
        <div >
            <h1 className='text-center mb-4 text-3xl font-bold text-gray-800'>Chat Messages</h1>

            <div className='container mx-auto px-4 flex'>
                <div className='w-1/4 pr-4 pb-4'>
                    <UserList users={users} />
                </div>
                <div className='w-3/4'>
                    {error && <div className="text-red-500 mb-4">{error}</div>}
                    <div className="bg-gray-100 p-4 h-64 overflow-y-auto mb-4">
                        {messages.map((msg, index) => (
                            <div key={index} className="mb-2">
                                <strong>{msg.sender?.username || 'Unknown'}: </strong>
                                <span className={msg.type === 'JOIN' ? 'text-green-500' : msg.type === 'LEAVE' ? 'text-red-500' : ''}>
                                    {msg.content}
                                </span>
                            </div>
                        ))}
                    </div>
                    <div className="flex mb-4">
                        <input
                            type="text"
                            value={inputMessage}
                            onChange={(e) => setInputMessage(e.target.value)}
                            className="flex-grow mr-2 p-2 border rounded"
                            placeholder="Type your message..."
                        />
                        <button
                            className="bg-blue-500 hover:bg-blue-400 text-white font-bold py-2 px-4 border-b-4 border-blue-700 hover:border-blue-500 rounded"
                            onClick={sendMessage}
                        >
                            Send
                        </button>
                    </div>
                    <button
                        className="bg-red-500 hover:bg-red-400 text-white font-bold py-2 px-4 border-b-4 border-red-700 hover:border-red-500 rounded"
                        onClick={handleDisconnect}
                    >
                        Disconnect
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ChatComponent;