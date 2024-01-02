import React, { useState, useEffect, useContext } from 'react';

import { NavLink } from 'react-router-dom';
import { AppContext } from '../components/user/UserPage';

const Dashboard = () => {
  const url = "https://"+window.location.hostname + ":8443/OnlineExamPortal/control/exam-info";
  const [data, setData] = useState([]);
  const [error, setError] = useState(null);
  const { questions, setQuestions } = useContext(AppContext);
  setQuestions(null);
  
  var user=sessionStorage.getItem("userId"); 
console.log("=====",user);

  const fetchInfo = () => {
    

    fetch(url, {
      method: "GET",
      credentials: "include",
      headers: {
        'Content-Type': 'application/json',
      },
    })
      .then((response) => response.json())
      .then((result) => {
        
        if (result.exam === undefined) {
          
          console.error('Error fetching data:', error);
          setError('Error fetching data. Please try again.');
        } else {
          console.log(result.exam.exam);
          setData(result.exam.exam);
        
        }

      })
      .catch((error) => {
        console.error('Error fetching data:', error);
        setError('Error fetching data. Please try again.');
      });
  };

  useEffect(() => {
    fetchInfo();
  }, []);

  const fetchDataFromDatabase = (exam) => {
    console.log("Entered func" + exam.examId);
    return (
      <Card
        key={`${exam.examId}`}
        title={`${exam.examName}`}
        content={`ExamId:${(exam.examId)}<br>ExamName: ${(exam.examName)}<br>Time: ${Number(exam.durationMinutes)}min<br>Description: ${exam.description}`}
        examId={`${exam.examId}`}
        noOfQuestion={`${exam.noOfQuestions}`}
      />
    );
  };

  return (
    <div className="container mt-4">
      {error && <p>{error}</p>}
      {!error && (
        <div className="row">
          {data.map((exam) => (
            <div key={exam.examId} className="col-md-4 mb-4">
              {fetchDataFromDatabase(exam)}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
const Upda=(props)=>{
  sessionStorage.setItem("exam", props.examId);

  sessionStorage.setItem("ques",props.noOfQuestion);
 

}
const Card = (props) => {
 
  return (
    <div className="card border border-dark">
      <div className="card-body" dangerouslySetInnerHTML={{ __html: props.content }} />

      <button className='btn btn-primary mt-2' onClick={()=>{Upda(props)}}>
        <NavLink to="/exam" className='text-light' style={{ textDecoration: "none" }}>
          Exam
        </NavLink>
      </button>
    </div>
  );
};

export default Dashboard;
