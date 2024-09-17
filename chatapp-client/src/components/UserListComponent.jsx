import React from 'react';
const UserList = ({ users }) => {
    return (
        <div className="bg-gray-100 p-4 h-64 overflow-y-auto">
            <h2 className="font-bold mb-2">Online Users</h2>
            <ul>
                {users.map((user, index) => (
                    <li key={index} className="mb-1">
                        {user.username}
                    </li>
                ))}
            </ul>
        </div>
    );
}
export default UserList;