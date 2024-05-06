import React, { useState, useEffect } from "react";
import "./History.css";

const ViewHistory = () => {
    const [selectedDate, setSelectedDate] = useState("");
    const [historyData, setHistoryData] = useState([]);

    const handleDateChange = (event) => {
        setSelectedDate(event.target.value);
        fetchHistoryData(event.target.value);
    };

    const fetchHistoryData = (date) => {
        console.log(`Fetching history for date: ${date}`);
        const exampleData = [
            { id: 1, user: "User1", time: "08:00 AM" },
            { id: 2, user: "User2", time: "09:15 AM" },
            { id: 3, user: "User3", time: "10:30 AM" },

            // más datos...
        ];
        setHistoryData(exampleData);
    };

    return (
        <div className="view-history">
            <div className="header-container">
                <h1>View History</h1>
                <input
                    type="date"
                    value={selectedDate}
                    onChange={handleDateChange}
                />
                <h2>History for {selectedDate}:</h2>
            </div>
            <div className="history-list-container">
                {historyData.length > 0 ? (
                    <ul>
                        {historyData.map((entry) => (
                            <li key={entry.id}>
                                {entry.user} entered at {entry.time}
                            </li>
                        ))}
                    </ul>
                ) : (
                    <p>No history available for this date.</p>
                )}
            </div>
        </div>
    );
};

export default ViewHistory;
