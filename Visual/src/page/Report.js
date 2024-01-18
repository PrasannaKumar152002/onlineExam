import React, { useContext, useEffect, useState,useRef } from 'react';
import { AppContext } from '../components/user/UserPage';
import Header from '../components/user/Header';
import "../page/answer.css";
import '../page/answermobileview.css';
import '../page/individualquestion_mobileview.css';
import { Descriptions } from 'antd';
import Title from 'antd/es/skeleton/Title';
import { Button, Col, Modal, Row, Table } from 'react-bootstrap';
import { Tag } from 'antd';
import { unmountComponentAtNode } from 'react-dom';
import Icon from '@ant-design/icons/lib/components/Icon';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import tick from '../components/image/check.png'
import wrong from '../components/image/cancel.png'
import {useReactToPrint} from 'react-to-print';

const Report = (props) => {
  var user = sessionStorage.getItem("userId");
  const { answers, setAnswers } = useContext(AppContext);
  const { questions, setQuestions } = useContext(AppContext);
  const [score, setScore] = useState(0);
  const printRef=useRef();
  
  useEffect(() => {
    console.log("fetch called...");
    setQuestions(props.questions);
    setAnswers(props.userAttemptAnswerMasterList);
    setScore(props.userAttemptMasterMap);

  }, []);
  
  var seq = 1;

  const handlePrint=useReactToPrint({content:()=>printRef.current})
  return (
    <div>
      <div className="answer-table-outer" style={{ width: "100%" }}>
      <div className="answer-table-wrapper" ref={printRef}>
      <div class="d-grid gap-2 d-md-flex justify-content-md-end">
      <button class="btn btn-outline-primary" type="button" onClick={props.hideDetails}>Back</button>
      <button class="btn btn-outline-danger" type="button"onClick={handlePrint}>Print</button>
    </div>
       
        <Title className="answer-table-heading" level={4}>Result</Title>
        
          <Descriptions bordered title={null} border size="small">
            <Descriptions.Item label="Email Id">{user}</Descriptions.Item>
            <Descriptions.Item label="Score">{Number(score.score)}</Descriptions.Item>
            <Descriptions.Item label="Result">{score ? score.userPassed == "Y" ? "Pass" : "Fail" : 0}</Descriptions.Item>
          </Descriptions>
          <br />


          <table class="table" >
            <thead class="thead-light">
              <tr>
                <th scope="col">S.No</th>
                <th scope="col">Question</th>
                <th scope="col">CorrectAnswer</th>
                <th scope="col">GivenAnswer</th>
                <th scope="col">Mark</th>
                <th scope="col">Status</th>

              </tr>
            </thead>
            <tbody>
              {questions ? (questions.map((oneQuestion, index) => {
                console.log("index-", index);
                console.log("answer=", score);
                return (
                  <tr>
                    <th scope="row">{seq++}</th>
                    <td>{oneQuestion.questionDetail}</td>
                    <td><button type="button" class="btn btn-outline-success">{oneQuestion.answer}</button></td>
                    <td><button type="button" class="btn btn-outline-primary">{answers[index].submittedAnswer}</button></td>
                    <td>{oneQuestion.answerValue}</td>
                    <td>{oneQuestion.answer == answers[index].submittedAnswer ? <img src={tick} style={{ width: "20px" }} /> : <img src={wrong} style={{ width: "22px" }} />}</td>
                  </tr>
                )
              })
              ) : <p>No status to show</p>}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Report;
