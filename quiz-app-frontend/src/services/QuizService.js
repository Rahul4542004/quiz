import { responsiveFontSizes } from "@mui/material";
import axios from "axios";
import { createContext, useState } from "react";
const QUIZ_URL = "http://localhost:8095/api/questions";
export const getOS = () => axios.get(QUIZ_URL+"/os");
export const getCNS = () => axios.get(QUIZ_URL + "/cns");
export const getOOPS = () => axios.get(QUIZ_URL + "/oops");
export const getDBMS = () => axios.get(QUIZ_URL + "/dbms");
export const getOSByTopic = (topic) => axios.get(QUIZ_URL + "/os/topic",{params : {topic}});
export const getCNSByTopic = (topic) => axios.get(QUIZ_URL + "/cns/topic",{params : {topic}});
export const getDBMSByTopic = (topic) => axios.get(QUIZ_URL + "/dbms/topic",{params : {topic}});
export const getOOPSByTopic = (topic) => axios.get(QUIZ_URL + "/oops/topic",{params : {topic}});

export const saveTest = (test) => localStorage.setItem("test",JSON.stringify(test));
export const getTest = () => localStorage.getItem("test");
export const isTakingTest = () => {
    const test = localStorage.getItem("test")
    return test !== null && test !== "null";
}
export const getTime = () => {
    return localStorage.getItem("time");
}
export const setTime = (x) => {
    localStorage.setItem("time",JSON.stringify(x));
}
export const calculateScoreForOs = (responses) => axios.post(QUIZ_URL + "/os/submit",responses);
export const calculateScoreForCns = (responses) => axios.post(QUIZ_URL + "/cns/submit",responses);
export const calculateScoreForDbms = (responses) => axios.post(QUIZ_URL + "/dbms/submit",responses);
export const calculateScoreForOops = (responses) => axios.post(QUIZ_URL + "/oops/submit",responses);

const context = createContext();
const Provider = ({children}) => {
    const [state,setState] = useState('Hi');
    return(
    <context.Provider value={[state,setState]}>
        {children}
    </context.Provider>
    );
}