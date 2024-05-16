import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./History.css";
import axios from "axios";

const ViewHistory = () => {
    const [selectedDate, setSelectedDate] = useState("");
    const [historyData, setHistoryData] = useState([]);
    const navigate = useNavigate(); // Importa useNavigate

    const handleDateChange = (event) => {
        setSelectedDate(event.target.value);
        fetchHistoryData(event.target.value);
    };

    const fetchHistoryData = async (selectedDate) => {
        if (!selectedDate) {
            console.log("No date selected");
            return; // Exit the function if no date is selected
        }
        const url = `http://localhost:3333/attempt/${selectedDate}/getAttempt`;
        console.log(`Fetching history for date: ${selectedDate} from ${url}`);
        try {
            const response = await axios.get(url);
            setHistoryData(response.data);
        } catch (error) {
            console.error('Error fetching data:', error);
        }
    };

    const handleGoBack = () => {
        navigate("/home"); // Redireccionar a la página de inicio (Home)
    };

    useEffect(() => {
        fetchHistoryData(selectedDate);
    }, [selectedDate]);

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
                                {entry.user.username} entered at {entry.time}
                            </li>
                        ))}
                    </ul>
                ) : (
                    <p>No history available for this date.</p>
                )}
            </div>
            <button onClick={handleGoBack}>Back to Home</button> {/* Botón de regreso a Home */}
        </div>
    );
};

export default ViewHistory;
