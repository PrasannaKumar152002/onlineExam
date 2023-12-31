import React, { useContext, useEffect } from 'react';
import { AppContext } from '../components/user/UserPage';
import Header from '../components/user/Header';


const Report = () => {

  const { answers } = useContext(AppContext);
  const { questions } = useContext(AppContext);

  const examId = sessionStorage.getItem("exam");
  console.log("examid====report", examId);

  const selectionAnswer = () => {
    console.log("test", answers)
    try {
      const Array = [];
      questions.forEach(ele => {
        ele.forEach((element) => {
          const questionId = element.questionId;
          var ans = element.answer;
          var answer = null;
          if (element.questionType == "QT_MC") {
            var option = "";
            answers[element.questionId].map((ele) => {
              option+=(element[ele]+",");
            })
            answer = option.substring(0,option.length-1);
            console.log("option-", option);
          }
          else if (element.questionType == "QT_FIB") {
            answer = answers[element.questionId];
          }
          else {
            answer = element[answers[element.questionId]]
          }
          Array.push({ questionId, answer });

        });

      });
      console.log("array", Array);
      return Array;
    } catch (error) {
      console.log(error);

    }
  }


  useEffect(() => {
    fetchInfo();
  }, []);




  const url = "https://" + window.location.hostname + ":8443/OnlineExamPortal/control/update-result";

  const fetchInfo = () => {
    const selectionAnswerResult = selectionAnswer();
    const requestBody = { questions: questions }
    console.log("inside fetch...", selectionAnswerResult, "------", requestBody);

    fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ selectionAnswerResult, questions: questions, examId: examId }),
      credentials: 'include'
    })
      .then((res) => res.json())
      .then((fetchedData) => {
        console.log("fetched...date", fetchedData);

      })
      .catch((error) => {
        console.error('Error fetching data:', error);

      });
  };
  return (
    <div>
      <Header />
      {/* <h1>Score={correctmark}/{total}</h1> */}

    </div>
  );
};

export default Report;
