import React, { useContext, useEffect, useState } from 'react'
import '../components/portal.css';
import $ from 'jquery';
import { Badge, Icon, Button, Row, Col, Popconfirm } from 'antd';
import Trainee from './Trainee';
import { useNavigate } from 'react-router-dom';
import { AppContext } from '../components/user/UserPage';

function QuestionPalette({ data }) {
  const { answers, setAnswers } = useContext(AppContext);
  const { questions } = useContext(AppContext);
  var sequenceNo = 1;
  const nav = useNavigate();
  var trigger = $('.hamburger'),
    overlay = $('.overlay'),
    isClosed = false;

  function hamburger_cross() {
    $('#wrapper').toggleClass('toggled');

    if (isClosed == true) {
      overlay.hide();
      trigger.removeClass('is-open');
      trigger.addClass('is-closed');
      isClosed = false;
    } else {
      overlay.show();
      trigger.removeClass('is-closed');
      trigger.addClass('is-open');
      isClosed = true;
    }
  }
  const FULL_DASH_ARRAY = 283;
  const WARNING_THRESHOLD = 600;
  const ALERT_THRESHOLD = 300;

  const COLOR_CODES = {
    info: {
      color: "green"
    },
    warning: {
      color: "orange",
      threshold: WARNING_THRESHOLD
    },
    alert: {
      color: "red",
      threshold: ALERT_THRESHOLD
    }
  };

  var exTime = Number(sessionStorage.getItem("examTime"))*60;
  console.log("exam time : ", exTime);

  const TIME_LIMIT = exTime;
  let timePassed = 0;
  let timeLeft = TIME_LIMIT;
  let timerInterval = null;
  let remainingPathColor = COLOR_CODES.info.color;

  useEffect(() => {
    document.getElementById("app").innerHTML = `
<div class="base-timer">
  <svg class="base-timer__svg" viewBox="0 0 100 100" xmlns="http://www.w3.org/2000/svg">
    <g class="base-timer__circle">
      <circle class="base-timer__path-elapsed" cx="50" cy="50" r="45"></circle>
      <path
        id="base-timer-path-remaining"
        stroke-dasharray="283"
        class="base-timer__path-remaining ${remainingPathColor}"
        d="
          M 50, 50
          m -45, 0
          a 45,45 0 1,0 90,0
          a 45,45 0 1,0 -90,0
        "
      ></path>
    </g>
  </svg>
  <span id="base-timer-label" class="base-timer__label">${formatTime(
      timeLeft
    )}</span>
</div>
`;
    startTimer();
  }, [])



  function onTimesUp() {
    clearInterval(timerInterval);
  }

  function startTimer() {
    timerInterval = setInterval(() => {
      timePassed = timePassed += 1;
      timeLeft = TIME_LIMIT - timePassed;
      document.getElementById("base-timer-label").innerHTML = formatTime(
        timeLeft
      );
      setCircleDasharray();
      setRemainingPathColor(timeLeft);

      if (timeLeft === 0) {
        onTimesUp();
      }
    }, 1000);
  }

  function formatTime(time) {
    const minutes = Math.floor(time / 60);
    let seconds = time % 60;

    if (seconds < 10) {
      seconds = `0${seconds}`;
    }

    return `${minutes}:${seconds}`;
  }

  function setRemainingPathColor(timeLeft) {
    const { alert, warning, info } = COLOR_CODES;
    if (timeLeft <= alert.threshold) {
      document
        .getElementById("base-timer-path-remaining")
        .classList.remove(warning.color);
      document
        .getElementById("base-timer-path-remaining")
        .classList.add(alert.color);
    } else if (timeLeft <= warning.threshold) {
      document
        .getElementById("base-timer-path-remaining")
        .classList.remove(info.color);
      document
        .getElementById("base-timer-path-remaining")
        .classList.add(warning.color);
    }
  }

  function calculateTimeFraction() {
    const rawTimeFraction = timeLeft / TIME_LIMIT;
    return rawTimeFraction - (1 / TIME_LIMIT) * (1 - rawTimeFraction);
  }

  function setCircleDasharray() {
    const circleDasharray = `${(
      calculateTimeFraction() * FULL_DASH_ARRAY
    ).toFixed(0)} 283`;
    document
      .getElementById("base-timer-path-remaining")
      .setAttribute("stroke-dasharray", circleDasharray);
  }
  const handleOptionChange = (questionId, selectedOption) => {
    setAnswers((prevAnswers) => ({
      ...prevAnswers,
      [questionId]: selectedOption,
    }));
  };
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
                console.log("QT_SC")
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
  const[sequence,setSequence]=useState(1);
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
    <div>
      <div className='container'>
        <div className='row'>
          <div className='col-3'>
            <div id="wrapper">
              <div className="overlay"></div>

              {/* <!-- Sidebar   id="sidebar-wrapper" --> */}
              <nav className="navbar navbar-inverse fixed-top col-3" id="sidebar-wrapper" role="navigation">
                <Trainee />
                <div id="app"></div>
                <div className="question-list-wrapper">
                  <div className="question-list-inner" style={{ background: "#F5F0EF" }}>
                    <Row style={{ padding: '5px' }}>
                      {data.map((ques) => {
                        return (
                          <Col span={6} style={{ padding: '10px' }}>
                            <button className='btn  pl-3 pr-3 pt-2 pb-2 ml-2  btn-light border border-dark' style={{ background: "#B6B0AF", color: "white" }}>{ques.sequenceNum}</button>
                          </Col>
                        )
                      })}
                    </Row>

                  </div>
                </div>
                <div className="End-test-container">
                  <Popconfirm
                    title="Are you sure to end the test"
                    onConfirm={() => { nav("/dashboard"); window.location.reload() }}
                    okText="Yes"
                    cancelText="No"
                  >
                    <Button type="default">End Test</Button>
                  </Popconfirm>
                </div>
              </nav>
            </div>
          </div>
          {/* <!-- /#sidebar-wrapper --> */}

          {/* <!-- Page Content  --> */}
          <div className='col-9'>
            <button type="button" className="hamburger animated fadeInLeft is-closed" data-toggle="offcanvas" onClick={hamburger_cross}>
              <span class="hamb-top"></span>
              <span class="hamb-middle"></span>
              <span class="hamb-bottom"></span>
            </button>
            <div class="container" id="page-content">
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
                        nav('/report');
                      }}
                    >
                      Submit
                    </button>
                  </div>
                ) : (
                  ""
                )}
              </div>
            </div>
          </div>
          {/* <!-- /#page-content-wrapper --> */}

        </div>
        {/* <!-- /#wrapper --> */}

        {/* {data.map((ques)=>(<button className='btn pl-3 pr-3 pt-2 pb-2 ml-3 mb-3 btn-light border border-dark'>{ques.Seq}</button>))} */}
      </div>
    </div>
  )
}

export default QuestionPalette
