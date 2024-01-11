import React, { useEffect, useState } from "react";
import "./Cards.css";
import { cardsData } from "../../Data/Data";

import Card from "../Card/Card";
import img from "../../components/image/user.png"

const Cards = () => {
  var card = []
  var topic = []
  const [answers, setAnswers] = useState();
  const [questions, setQuestions] = useState();
  const [score, setScore] = useState(100);
  const [exams, setExam] = useState();
  const [topicList, setTopicList] = useState();
  useEffect(() => {
    console.log("Cardfetch called...");
    fetchResult();

  }, []);
  console.log("window location=",window.location.hostname);
  const url = "https://" + window.location.hostname + ":8443/OnlineExamPortal/control/fetch-user-report";
  const fetchResult = () => {
    fetch(url, {
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include'
    })
      .then((res) => res.json())
      .then((fetchedData) => {
        console.log("fetched...date", fetchedData);
        // setQuestions(fetchedData.questions);
        // setAnswers(fetchedData.userAttemptAnswerMasterList);
        // setScore(fetchedData.userAttemptMasterMap);
        // setExam(fetchedData.examList);
        // setTopicList(fetchedData.userAttemptTopicMasterList);
        console.log(fetchedData.userAttemptTopicMasterList);
        fetchedData.userAttemptTopicMasterList.map((oneTopic) => {
          topic.push(Number(oneTopic.userTopicPercentage))
        })
        setTopicList(topic);
        fetchedData.examList.map((oneExam) => {
          var info = {
            title: oneExam.examId,
            color: {
              backGround: "linear-gradient(180deg, #bb67ff 0%, #c484f3 100%)",
              boxShadow: "0px 10px 20px 0px #e0c6f5",
            },
            barValue: Number(fetchedData.userAttemptMasterMap.score),
            value: oneExam.examName,
            png: null,
            series: [
              {
                name: "TopicPercentage",
                data: topicList,
              },
            ],
          }
          card.push(info);
          
        })
        setExam(card);
      })
      .catch((error) => {
        console.error('Error fetching data:', error);
      });
  }
  card=exams;
  console.log("outCard-",card);

  
  const cards = {
    title: "Sales",
    color: {
      backGround: "linear-gradient(180deg, #bb67ff 0%, #c484f3 100%)",
      boxShadow: "0px 10px 20px 0px #e0c6f5",
    },
    barValue: 50,
    value: "25,970",
    // png: UilUsdSquare,
    series: [
      {
        name: "Sales",
        data: [31, 40, 28, 51, 42, 109, 100],
      },
    ],
  }
  return (
    <div className="Cards">
      {card.map((card, id) => {
        return (
          <div key={id}>
            <Card
              title={card.title}
              color={card.color}
              barValue={card.barValue}
              value={card.value}
              png={card.png}
              series={card.series}
            />
          </div>
        );
      })}
    </div>
  );
};

export default Cards;
