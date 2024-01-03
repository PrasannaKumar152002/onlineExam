import React, { useState, useEffect, useContext } from 'react';
import { NavLink } from 'react-router-dom';
import { AppContext } from '../components/user/UserPage';

const Dashboard = () => {
  const examInfoUrl = "https://"+ window.location.hostname +":8443/OnlineExamPortal/control/exam-info";
  const [examData, setExamData] = useState([]);
  const [fetchError, setFetchError] = useState(null);
  const { questions, setQuestions } = useContext(AppContext);
  setQuestions(null);



  const fetchExamInfo = () => {
    fetch(examInfoUrl, {
      method: "GET",
      credentials: "include",


    })
      .then((response) => response.json())
      .then((result) => {
        if (result.exam === undefined) {
          console.error('Error  in fetching exam data:', fetchError);
          setFetchError('Error fetching exam data. Please try again.');
        } else {
          console.log(result.exam.examList);
          setExamData(result.exam.examList);
        }
      })
      .catch((error) => {
        console.error('Error  in fetching exam data:', error);
        setFetchError('Error  in fetching exam data. Please try again.');
      });
  };

  useEffect(() => {
    fetchExamInfo();
  }, []);

  const renderExamCard = (exam) => {
    console.log("Entered render function for examId:", exam.examId);
    return (
      <ExamCard
        key={exam.examId}
        title={exam.examName}
        content={`ExamId: ${exam.examId}<br>ExamName: ${exam.examName}<br>Time: ${Number(exam.durationMinutes)}min<br>Description: ${exam.description}`}
        examId={exam.examId}
        noOfQuestions={exam.noOfQuestions}
      />
    );
  };

  return (
    <div className="container mt-4">
      {fetchError && <p>{fetchError}</p>}
      {!fetchError && (
        <div className="row">
          {examData.map((exam) => (
            <div key={exam.examId} className="col-md-4 mb-4">
              {renderExamCard(exam)}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

const SessionStorage = (props) => {
  sessionStorage.setItem("exam", props.examId);
  sessionStorage.setItem("ques", props.noOfQuestions);
};

const ExamCard = (props) => {
  return (
    <div className="card border border-dark">
      <div className="card-body" dangerouslySetInnerHTML={{ __html: props.content }} />

      <button className='btn btn-primary mt-2' onClick={() => { SessionStorage(props) }}>
        <NavLink to="/exam" className='text-light' style={{ textDecoration: "none" }}>
          Exam
        </NavLink>
      </button>
    </div>
  );
};

export default Dashboard;
