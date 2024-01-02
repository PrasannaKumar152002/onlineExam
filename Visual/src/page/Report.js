import React, { useContext,useEffect} from 'react';
import { AppContext } from '../components/user/UserPage';
import { useNavigate } from 'react-router-dom';

const Report = () => {
  
  const { answers } = useContext(AppContext);
  const { questions } = useContext(AppContext);
  const nav=useNavigate();

  console.log("--->", answers);
  console.log("--->report", questions);

  var correctmark = 0;
  var total=0;
  var wrongmark=0;
  try{
  questions.forEach(ele => {
    ele.forEach((element) => {
      total+=element.answerValue;
      console.log("totalmark",total)
      var answer=null;
      var selectedOption=element.answer;
      if(element[answers[element.questionId]]==undefined)
      {
        answer=answers[element.questionId];
      }
      else{
        answer=element[answers[element.questionId]];
      }
      console.log("----->answer",answer)
      selectedOption.trim() ==  answer.trim()? correctmark += element.answerValue: wrongmark+= element.answerValue
    
    });
  });
}catch(error){
  console.log(error);
  nav("/dashboard")
}

  console.log("mark----->",correctmark);
  console.log("wrongmark----->",wrongmark);
  console.log("total----->",total);

  // useEffect(() => {
  //   fetchInfo();
  // }, []);

  // const requestBody = {correctmark,wrongmark,total};
  // const url = "https://"+window.location.hostname + ":8443/OnlineExamPortal/control/update-result";

  // const fetchInfo = () => {
  //   console.log("inside fetch...");

  //   fetch(url, {
  //     method: 'POST',
  //     headers: {
  //       'Content-Type': 'application/json',
  //     },
  //     body: JSON.stringify(requestBody),
  //   })
  //     .then((res) => res.json())
  //     .then((fetchedData) => {
  //       console.log("fetched...date",fetchedData);
        
  //     })
  //     .catch((error) => {
  //       console.error('Error fetching data:', error);
  //       nav("/dashboard")
  //     });
  // };
  return (
    <div>
        <h1>Score={correctmark}/{total}</h1>
        
    </div>
  );
};

export default Report;
