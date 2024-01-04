import React, { useState, useEffect, useContext } from 'react';
import { AppContext } from '../components/user/UserPage';
import { useNavigate } from 'react-router-dom';

function Exam() {
  const navigate = useNavigate();
  const { answers, setAnswers } = useContext(AppContext);
  const { questions, setQuestions } = useContext(AppContext);
  const examId = sessionStorage.getItem("exam");
  const noOfQuestions = sessionStorage.getItem("ques");
  const [questionSequence, setQuestionSequence] = useState([]);

  useEffect(() => {
    if (examId) {
      console.log("==================exam")
      const questionInfoUrl = "https://"+ window.location.hostname +":8443/OnlineExamPortal/control/question-info";
      const requestBody = { examId: examId, noOfQuestions: noOfQuestions };

      fetch(questionInfoUrl, {
        method: 'POST',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(requestBody),
      })
        .then((response) => response.json())
        .then((result) => {
          console.log("inside....................");
          if (Array.isArray(result.question.examquestion)) {
            console.log("======",result.question.examquestion);
            setQuestions(result.question.examquestion);
           console.log(result.userAttemptAnswerMaster)
            setQuestionSequence(result.userAttemptAnswerMaster);
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
  var sequenceNo=1;
  const renderQuestion = (question) => (
    <div key={question.questionId} className={`card mt-3 ${answers[question.questionId] ? 'border-success' : 'border-danger'}`}>
      <div className='card-body'>
        <h5 className='card-title'>{(sequenceNo++) + ". " + question.questionDetail}</h5>
        <div className='form-check'>
          {['A', 'B', 'C', 'D', 'E'].map((option) => {
            const optionKey = `option${option}`;
            const optionValue = question[optionKey];
            const questionType = question.questionType;

            switch (questionType) {
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
                if (option === 'A' || option === 'B') {
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
                console.log("Entering QT_FIB case...");
                if (option === 'A') {
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

  const renderQuestionGroup = (questionGroup) => (
    <React.Fragment key={questionGroup[0].questionId}>
      {Array.isArray(questionGroup) ? (
        questionGroup.map((question) => renderQuestion(question))
      ) : (
        <p>Error: Invalid format for questionGroup</p>
      )}
    </React.Fragment>
  );

  return (
    <div className='container mt-4'>
      {Array.isArray(questions) ? (
        questions.map((questionGroup) => renderQuestionGroup(questionGroup))
      ) : (
        <p>Error: No questions assigned for this exam</p>
      )}

      {questions !== null ? (
        <div className='mt-4'>
          <button
            className='btn btn-primary'
            onClick={() => {
              navigate('/report');
            }}
          >
            Submit
          </button>
        </div>
      ) : (
        ""
      )}
    </div>
  );
}

export default Exam;
