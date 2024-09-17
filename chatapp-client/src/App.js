
import './App.css';
import { Routes } from 'react-router-dom';
import { BrowserRouter as Router, Route } from 'react-router-dom';

import ChatComponent from './components/ChatComponent';

function App() {
  return (
    <Router>
      <div className='container mx-auto px-20'>
        <Routes>
          {/* Define the route for the ChatRoom */}
          <Route path="/" element={<ChatComponent />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
