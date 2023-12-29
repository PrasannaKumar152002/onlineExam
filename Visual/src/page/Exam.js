import React, { useState, useEffect, useContext } from 'react';
import { AppContext } from '../components/user/UserPage';
import { useNavigate } from 'react-router-dom';
function Exam() {
  const nav = useNavigate();
  const { answers, setAnswers } = useContext(AppContext);
  const { questions, setQuestions } = useContext(AppContext);
  const examId = sessionStorage.getItem("exam");
  
  const noOfQuestions = sessionStorage.getItem("ques");


  const [sequenceNumber, setsequenceNumber] = useState([]);
 


  useEffect(() => {
    if (examId) {
      const apiUrl = "https://localhost:8443/OnlineExamPortal/control/questionInfo";
      const requestBody = { examId: examId, noOfQuestions: noOfQuestions };
    

      fetch(apiUrl, {
        method: 'POST',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(requestBody),
      })
        .then((responce) => responce.json())
        .then((result) => {
        

         
          if (Array.isArray(result.question.examquestion)) {
            setQuestions(result.question.examquestion);
            setsequenceNumber(result.questionSequence)
           
          } else {
            console.error('Invalid format for questions:', result.question.examquestion);
          
           
          }
        })
        .catch((error) => {
          console.error('Error fetching questions:', error);
         
        });
    }
  }, [examId, noOfQuestions]);

  const handleOptionChange = (questionId, selectedOption) => {
    setAnswers((prevAnswers) => ({
      ...prevAnswers,
      [questionId]: selectedOption,
    }));

  };
  var seq = 1;
 
 
  const renderQuestion = (question) => (
    <div key={question.questionId} className={`card mt-3 ${answers[question.questionId] ? 'border-success' : 'border-danger'}`}>
      <div className='card-body'>
        <h5 className='card-title'>{(seq++) + ". " + question.questionDetail}</h5>
        <div className='form-check'>
          {['A', 'B', 'C', 'D', 'E'].map((option) => {
            const optionKey = `option${option}`;
            const optionValue = question[optionKey];
            const que = question.QuestionType;

            switch (que) {
              case 'QT_SC':
                return optionValue && (
                  <div key={optionKey} className='form-check'>
                    <input
                      type='radio'
                      className='form-check-input'
                      id={`${optionKey}-${question.questionId}`}
                      name={`question${question.questionId}`}
                      value={optionKey}
                      onChange={() => handleOptionChange(question.questionId, optionKey)}
                      checked={answers[question.questionId] === optionKey}
                    />
                    <label className='form-check-label' htmlFor={`${optionKey}-${question.questionId}`}>
                      {optionValue}
                    </label>
                  </div>
                );
              case 'QT_MC':
                return optionValue && (
                  <div key={optionKey} className='form-check'>
                    <input
                      type='checkbox'
                      className='form-check-input'
                      id={`${optionKey}-${question.questionId}`}
                      name={`question${question.questionId}`}
                      value={optionKey}
                      onChange={() => handleOptionChange(question.questionId + "@" + optionKey, optionKey)}
                      checked={answers[question.questionId] && answers[question.questionId].includes(optionValue)}
                    />
                    <label className='form-check-label' htmlFor={`${optionKey}-${question.questionId}`}>
                      {optionValue}
                    </label>
                  </div>
                );
              case 'QT_TF':
                if (option == 'A' || option == 'B') {
                  return optionValue && (
                    <div key={optionKey} className='form-check'>
                      <input
                        type='radio'
                        className='form-check-input'
                        id={`${optionKey}-${question.questionId}`}
                        name={`question${question.questionId}`}
                        value={optionKey}
                        onChange={() => handleOptionChange(question.questionId, optionKey)}
                        checked={answers[question.questionId] === optionKey}
                      />
                      <label className='form-check-label' htmlFor={`${optionKey}-${question.questionId}`}>
                        {optionValue}
                      </label>
                    </div>
                  );
                }
              case 'QT_FIB':
                console.log("enter.....");
                if (option == 'A') {

                  return optionValue && (
                    <div key={optionKey} className='form-check'>
                      <input
                        type="text"
                        id={`${question.questionId}in`}
                        onChange={(e) => handleOptionChange(question.questionId, e.target.value)}
                      />
                    </div>
                  );
                }
              default:
                return null;
            }

          })}
        </div>
      </div>
    </div>
  );

  // Helper function to render a group of questions
  const renderQuestionGroup = (quesGroup) => (
    <React.Fragment key={quesGroup[0].questionId}>
      {Array.isArray(quesGroup) ? (
        quesGroup.map((ques) => renderQuestion(ques))
      ) : (
        <p>Error: Invalid format for quesGroup</p>
      )}
     

    </React.Fragment>
  );
  
  return (
    <div className='container mt-4'>
      {Array.isArray(questions) ? (
        questions.map((quesGroup) => renderQuestionGroup(quesGroup))
      ) : (
        <p>Error: No questions assigned for this exam</p>
      )}



      {questions!=null?
      <div className='mt-4'>
        <button
          className='btn btn-primary'
          onClick={() => {
            nav('/report');

          }}
        >
          submit

        </button>
      </div>:""}
    </div>
  );
}

export default Exam;
