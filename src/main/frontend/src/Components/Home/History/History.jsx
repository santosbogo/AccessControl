import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./History.css";
import axios from "axios";
import authentication from "../../Hoc/Hoc";

const ViewHistory = () => {
    const [selectedDate, setSelectedDate] = useState("");
    const [AccessData, setAccessData] = useState([]);
    const [ExitsData, setExitsData] = useState([]);
    const [combinedData, setCombinedData] = useState([]);

    const [error, setError] = useState('');
    const navigate = useNavigate(); // Importa useNavigate

    const handleDateChange = (e) => {
        const selectDate = e.target.value;
        setSelectedDate(selectDate);
    };

    useEffect(() => {
        if(selectedDate){
            console.log("Fetching history for", selectedDate);
            const fetchHistory = async () => {
                try {
                    const response = await axios.get('http://localhost:3333/attempt/getAttempt', {
                        params: {
                            selectedDate: selectedDate
                        }
                    });
                    console.log('aca');
                    console.log('Response:', response.data);
                    setAccessData(response.data);
                } catch (error) {
                    console.error('Error fetching Attempts:', error);
                    setError('Failed to fetch Attempts.');
                }
            };
            fetchHistory();
        } else {
            console.log("No date selected");
        }
    }, [selectedDate]);

    useEffect(() => {
        if(selectedDate){
            const fetchExits = async () => {
                console.log("Fetching exits for", selectedDate);
                try {
                    const response = await axios.get('http://localhost:3333/exit/getExits', {
                        params: {
                            selectedDate: selectedDate
                        }
                    });
                    console.log('Response:', response.data);
                    setExitsData(response.data);
                } catch (error) {
                    console.error('Error fetching Exits:', error);
                    setError('Failed to fetch Exits.');
                }
            };
            fetchExits();
        }
    }, [selectedDate]);

    useEffect(() => {
        const combineAndSortData = () => {
            const combined = [...AccessData, ...ExitsData];
            combined.sort((a, b) => {
                const timeA = new Date(`${selectedDate}T${a.time}`);
                const timeB = new Date(`${selectedDate}T${b.time}`);
                console.log(timeA, timeB); // Esto te mostrará cómo se están comparando las fechas
                return timeA - timeB;
            });

            console.log("Combined and sorted data:", combined);
            setCombinedData(combined);
        };

        combineAndSortData();
    }, [AccessData, ExitsData]);


    const handleGoBack = () => {
        navigate("/home"); // Redireccionar a la página de inicio (Home)
    };

    return (
        <div className="header-container">
            <div className="main-title">
                <h1>View History</h1>
                <input
                    type="date"
                    value={selectedDate}
                    onChange={handleDateChange}
                />
                <h2>History for {selectedDate}:</h2>
            </div>
            <div className="history-list-container">
                {combinedData.length > 0 ? (
                    <ul>
                        {combinedData.map((entry, index) => (
                            <li key={index}>
                                {entry.firstName ? (
                                    `${entry.firstName} ${entry.lastName} Access Attempt at ${entry.time}`
                                ) : (
                                    `Exit at ${entry.time}`
                                )}
                            </li>
                        ))}
                    </ul>
                ) : (
                    <p>No history available for this date.</p>
                )}
            </div>
            <button onClick={handleGoBack} className={"confirm-button"}>Back Home</button>
            {/* Botón de regreso a Home */}
        </div>
    );
};

export default authentication(ViewHistory);