import Table from "react-bootstrap/Table";
import { useEffect, useState } from "react";
import QuestionForm from "../Dashboard/Form/QuestionForm";
import QuestionModalSample from "../Modal/Edit/QuestionModalSample";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faTrash } from "@fortawesome/free-solid-svg-icons";

function Question() {
  const [questions, setQuestions] = useState([]);
  const [topics, setTopics] = useState([]);
  const [setEnum, getEnum] = useState([]);
  const [quesType, setQuesType] = useState();
  const [topicChange, setTopicChange] = useState();
  const [changedquestionDetail, setQuestionDetail] = useState("");
  const [changedoptionA, setChangedOptionA] = useState("");
  const [changedoptionB, setChangedOptionB] = useState("");
  const [changedoptionC, setChangedOptionC] = useState("");
  const [changedoptionD, setChangedOptionD] = useState("");
  const [changedoptionE, setChangedOptionE] = useState("");
  const [changedanswer, setChangedAnswer] = useState("");
  const [changednumAnswers, setChangedNumAnswers] = useState("");
  const [changeddifficultyLevel, setChangeddifficultyLevel] = useState("");
  const [changedanswerValue, setChangedanswerValue] = useState("");
  const [changednegativeMarkValue, setChangednegativeMarkValue] = useState("");

  useEffect(() => {
    fetchQuestions();
    fetchTopics();
    fetchQuesType();
  }, [changedoptionA, changedoptionB,changedoptionC,changedoptionD,changedoptionE]);

  const changeQuestionDetailHandler = (e) => {
    setQuestionDetail(e.target.value);
  };

  const changeOptionAHandler = (e) => {
    if (e.target.value === null || e.target.value === "" || e.target.value === undefined)
    {
      setChangedOptionA(null);
    }
    else
    {
    setChangedOptionA(e.target.value);
    }
  };

  const changeOptionBHandler = (e) => {
    if (e.target.value === null || e.target.value === "" || e.target.value === undefined)
    {
      setChangedOptionB(null);
    }
    else
    {
      setChangedOptionB(e.target.value);
    }
    
  };

  const changeOptionCHandler = (e) => {
    if (e.target.value === "null" || e.target.value === "" || e.target.value === undefined)
    {
      setChangedOptionC(null);
    }
    else
    {
      setChangedOptionC(e.target.value);
    }
    console.log("*****************M"+ changedoptionC);
    
  };

  const changeOptionDHandler = (e) => {
    if (e.target.value === "null" || e.target.value === "" || e.target.value === undefined)
    {
      setChangedOptionD("null");
    }
    else
    {
      setChangedOptionD(e.target.value);
    }
    console.log(e.target.value);
    
  };

  const changeOptionEHandler = (e) => {
    if (e.target.value === null || e.target.value === "" || e.target.value === undefined)
    {
      setChangedOptionE(null);
    }
    else
    {
      setChangedOptionE(e.target.value);
    }
    console.log(e.target.value);
    
  };

  const changeAnswerHandler = (e) => {
    setChangedAnswer(e.target.value);
  };

  const changeNumAnswersHandler = (e) => {
    setChangedNumAnswers(e.target.value);
  };

  const changedifficultyLevelHandler = (e) => {
    setChangeddifficultyLevel(e.target.value);
  };

  const changeanswerValueHandler = (e) => {
    setChangedanswerValue(e.target.value);
  };

  const changenegativeMarkHandler = (e) => {
    setChangednegativeMarkValue(e.target.value);
  };

  const handleSelectQuesTypeChange = (e) => {
    setQuesType(e.enumId);
  };

  const handleSelectTopicChange = (e) => {
    setTopicChange(e.topicId);
  };

  const fetchTopics = async () => {
    try {
      const response = await fetch(
        "https://"+window.location.hostname + ":8443/OnlineExamPortal/control/fetch-topics",
        {
          method: "GET",
          credentials: "include",
        }
      );
      if (!response.ok) {
        throw new Error();
      }
      const data = await response.json();
      console.log(data);
      var list = data.TopicInfo.TopicList;
      setTopics(list);
    } catch (error) {
      console.log(error);
    }
  };
  console.log(topics);

  const fetchQuesType = async () => {
    try {
      const response = await fetch(
        "https://"+window.location.hostname + ":8443/OnlineExamPortal/control/fetch-ques-type",
        {
          method: "GET",
          credentials: "include",
        }
      );
      if (!response.ok) {
        throw new Error();
      }
      const data = await response.json();
      console.log(data);
      var list = data.QuestionTypeInfo.QuestionTypeList;
      getEnum(list);
    } catch (error) {
      console.log(error);
    }
  };

  const fetchQuestions = async () => {
    try {
      const response = await fetch(
        "https://"+window.location.hostname + ":8443/OnlineExamPortal/control/fetch-questions",
        {
          method: "GET",
          credentials: "include",
        }
      );
      if (!response.ok) {
        throw new Error();
      }
      const data = await response.json();
      console.log(data);
      setQuestions(undefined);
      var list = data.QuestionInfo.QuestionList;
      setQuestions(list);
      console.log("-------------------------------"+list);
    } catch (error) {
      console.log(error);
    }
  };

  const handleDeleteQuestion = async (id) => {
    try {
      const data_map = { questionId: id.toString() };
      const response = await fetch(
        "https://"+window.location.hostname + ":8443/OnlineExamPortal/control/delete-question",
        {
          method: "DELETE",
          credentials: "include",
          headers: {
            "content-type": "application/json",
          },
          body: JSON.stringify(data_map),
        }
      );
      fetchQuestions();
      console.log(response);
    } catch (error) {
      console.log(error);
    }
  };

  const [display, setDisplay] = useState({
    display: "none",
  });

  function handleAddQuestion() {
    setDisplay({ display: "block" });
  }

  function handleCloseQuestion(e) {
    setDisplay({ display: "none" });
  }

  const submitHandler = (e) => {
    e.preventDefault();
    var form = document.getElementById("question");
    const formData = new FormData(form);
    const data_map = {
      questionDetail: formData.get("questionDetail"),
      optionA: changedoptionA,
      optionB: changedoptionB,
      optionC: changedoptionC,
      optionD: changedoptionD,
      optionE: changedoptionE,
      answer: formData.get("answer"),
      numAnswers: formData.get("numAnswers"),
      questionType: quesType,
      difficultyLevel: formData.get("difficultyLevel"),
      answerValue: formData.get("answerValue"),
      topicId: topicChange,
      negativeMarkValue: formData.get("negativeMarkValue"),
    };
    console.log(data_map);
    console.log("Question:" + data_map.questionType);
    if (data_map.questionDetail === "") {
      document.getElementById("questiondetailerr").style.display = "block";
    } else {
      document.getElementById("questiondetailerr").style.display = "none";
    }
    if (data_map.optionA === "") {
      document.getElementById("optionaerr").style.display = "block";
    } else {
      document.getElementById("optionaerr").style.display = "none";
    }
    if (data_map.optionB === "") {
      document.getElementById("optionberr").style.display = "block";
    } else {
      document.getElementById("optionberr").style.display = "none";
    }
    if (data_map.optionC === "") {
      document.getElementById("optioncerr").style.display = "block";
    } else {
      document.getElementById("optioncerr").style.display = "none";
    }
    if (data_map.optionD === "") {
      document.getElementById("optionderr").style.display = "block";
    } else {
      document.getElementById("optionderr").style.display = "none";
    }
    if (data_map.optionE === "") {
      document.getElementById("optioneerr").style.display = "block";
    } else {
      document.getElementById("optioneerr").style.display = "none";
    }
    if (data_map.answer === "") {
      document.getElementById("answererr").style.display = "block";
    } else {
      document.getElementById("answererr").style.display = "none";
    }
    if (data_map.numAnswers === "") {
      document.getElementById("numanserr").style.display = "block";
    } else {
      document.getElementById("numanserr").style.display = "none";
    }
    if (data_map.questionType === "") {
      document.getElementById("questiontypeerr").style.display = "block";
    } else {
      document.getElementById("questiontypeerr").style.display = "none";
    }
    if (data_map.difficultyLevel === "") {
      document.getElementById("difficultylevelerr").style.display = "block";
    } else {
      document.getElementById("difficultylevelerr").style.display = "none";
    }
    if (data_map.answerValue === "") {
      document.getElementById("answervalueerr").style.display = "block";
    } else {
      document.getElementById("answervalueerr").style.display = "none";
    }
    if (data_map.topicId === "") {
      document.getElementById("topiciderr").style.display = "block";
    } else {
      document.getElementById("topiciderr").style.display = "none";
    }
    if (data_map.negativeMarkValue === "") {
      document.getElementById("negativemarkvalueeerr").style.display = "block";
    } else {
      document.getElementById("negativemarkvalueeerr").style.display = "none";
    }
    if (
      !(
        data_map.questionDetail === "" ||
        data_map.answer === "" ||
        data_map.numAnswers === "" ||
        data_map.questionType === "" ||
        data_map.difficultyLevel === "" ||
        data_map.answerValue === "" ||
        data_map.topicId === "" ||
        data_map.negativeMarkValue === ""
      )
    ) {
      if (data_map.optionA === "") {
        data_map.optionA = "null";
      }
      if (data_map.optionB === "") {
        data_map.optionB = "null";
      }
      if (data_map.optionC === "") {
        data_map.optionC = "null";
      }
      if (data_map.optionD === "") {
        data_map.optionD = "null";
      }
      if (data_map.optionE === "") {
        data_map.optionE = "null";
      }
      try {
        // FETCH
        fetch(
          "https://"+window.location.hostname + ":8443/OnlineExamPortal/control/create-question",
          {
            method: "POST",
            credentials: "include",
            headers: {
              "content-type": "application/json",
            },
            body: JSON.stringify(data_map),
          }
        )
          .then((response) => {
            return response.json();
          })
          .then((fetch_data) => {
            console.log(fetch_data);
            fetchQuestions();
          });
      } catch (error) {
        console.log(error);
      }
    }
    setChangedOptionA("null");
    setChangedOptionB("null");
    setChangedOptionC("null");
    setChangedOptionD("null");
    setChangedOptionE("null");
    setChangedAnswer("");
    setChangedNumAnswers("");
    setChangeddifficultyLevel("");
    setChangedanswerValue("");
    setChangednegativeMarkValue("");
    handleCloseQuestion();
    form.reset();
  };

  if (questions === undefined || questions.length === 0)
    return (
      <>
        <div>
          <div className="d-flex justify-content-center min-vh-2 text-black">
            <QuestionForm
              fetchQuestions={fetchQuestions}
              buttonName="CREATE"
              submitHandler={submitHandler}
              handleSelectQuesTypeChange={handleSelectQuesTypeChange}
              quesType={quesType}
              setEnum={setEnum}
              handleSelectTopicChange={handleSelectTopicChange}
              topicChange={topicChange}
              topics={topics}
              handleCloseQuestion={handleCloseQuestion}
              changedquestionDetail={changedquestionDetail}
              changedoptionA={changedoptionA}
              changedoptionB={changedoptionB}
              changedoptionC={changedoptionC}
              changedoptionD={changedoptionD}
              changedoptionE={changedoptionE}
              changedanswer={changedanswer}
              changednumAnswers={changednumAnswers}
              changeddifficultyLevel={changeddifficultyLevel}
              changedanswerValue={changedanswerValue}
              changednegativeMarkValue={changednegativeMarkValue}
              changeQuestionDetailHandler={changeQuestionDetailHandler}
              changeOptionAHandler={changeOptionAHandler}
              changeOptionBHandler={changeOptionBHandler}
              changeOptionCHandler={changeOptionCHandler}
              changeOptionDHandler={changeOptionDHandler}
              changeOptionEHandler={changeOptionEHandler}
              changeAnswerHandler={changeAnswerHandler}
              changeNumAnswersHandler={changeNumAnswersHandler}
              changedifficultyLevelHandler={changedifficultyLevelHandler}
              changeanswerValueHandler={changeanswerValueHandler}
              changenegativeMarkHandler={changenegativeMarkHandler}
            />
          </div>
        </div>
      </>
    );
  return (
    <>
      <div>
        <h2 align="center">Question List</h2>
        <form className="d-flex m-5" role="search">
          <input
            className="form-control me-2"
            type="search"
            placeholder="Search"
            aria-label="Search"
          />
          <button className="btn btn-outline-success" type="submit">
            Search
          </button>
        </form>
      </div>

      <div>
        <Table responsive className="table-borderless question-hide">
          <thead>
            <tr>
              <th>Question ID</th>
              <th>Questions</th>
              <th>Topic ID</th>
              <th>Question Type</th>
              <th>Option A</th>
              <th>Option B</th>
              <th>Option C</th>
              <th>Option D</th>
              <th>Option E</th>
              <th>No.Of.Answers</th>
              <th>Difficulty Level</th>
              <th>Answer</th>
              <th>Negative Mark Value</th>
              <th>Answer Value</th>
              <th>Edit</th>
              <th>Delete</th>
            </tr>
          </thead>
          <tbody>
            {questions.map((question, i) => {
              return (
                <tr key={i}>
                  <td>{question.questionId}</td>
                  <td>{question.questionDetail}</td>
                  <td>{question.topicId}</td>
                  <td>{question.questionType}</td>
                  <td>{question.optionA}</td>
                  <td>{question.optionB}</td>
                  <td>{question.optionC}</td>
                  <td>{question.optionD}</td>
                  <td>{question.optionE}</td>
                  <td>{question.numAnswers}</td>
                  <td>{question.difficultyLevel}</td>
                  <td>{question.answer}</td>
                  <td>{question.negativeMarkValue}</td>
                  <td>{question.answerValue}</td>
                  <td className="border-none px-3 py-1 mt-4 mb-2 text-white rounded-0">
                    <QuestionModalSample
                      buttonName="UPDATE"
                      fetchQuestions={fetchQuestions}
                      fetchId={question.questionId}
                      Enumkey={i}
                      Queskey={question.topicId}
                      questionDetail={question.questionDetail}
                      topicChange={topicChange}
                      questionType={question.questionType}
                      topicId={question.topicId}
                      optionA={question.optionA}
                      optionB={question.optionB}
                      optionC={question.optionC}
                      optionD={question.optionD}
                      optionE={question.optionE}
                      numAnswers={question.numAnswers}
                      difficultyLevel={question.difficultyLevel}
                      answer={question.answer}
                      answerValue={question.answerValue}
                      negativeMarkValue={question.negativeMarkValue}
                      setEnum={setEnum}
                      topics={topics}
                      submitHandler={submitHandler}
                      handleSelectQuesTypeChange={handleSelectQuesTypeChange}
                      quesType={quesType}
                      handleSelectTopicChange={handleSelectTopicChange}
                      changedquestionDetail={changedquestionDetail}
                      changedoptionA={changedoptionA}
                      changedoptionB={changedoptionB}
                      changedoptionC={changedoptionC}
                      changedoptionD={changedoptionD}
                      changedoptionE={changedoptionE}
                      changedanswer={changedanswer}
                      changednumAnswers={changednumAnswers}
                      changeddifficultyLevel={changeddifficultyLevel}
                      changedanswerValue={changedanswerValue}
                      changednegativeMarkValue={changednegativeMarkValue}
                      changeQuestionDetailHandler={changeQuestionDetailHandler}
                      changeOptionAHandler={changeOptionAHandler}
                      changeOptionBHandler={changeOptionBHandler}
                      changeOptionCHandler={changeOptionCHandler}
                      changeOptionDHandler={changeOptionDHandler}
                      changeOptionEHandler={changeOptionEHandler}
                      changeAnswerHandler={changeAnswerHandler}
                      changeNumAnswersHandler={changeNumAnswersHandler}
                      changedifficultyLevelHandler={
                        changedifficultyLevelHandler
                      }
                      changeanswerValueHandler={changeanswerValueHandler}
                      changenegativeMarkHandler={changenegativeMarkHandler}
                    />
                  </td>
                  <td>
                    <FontAwesomeIcon
                      icon={faTrash}
                      style={{ color: "red", cursor: "pointer" }}
                      onClick={() => handleDeleteQuestion(question.questionId)}
                    />
                  </td>
                </tr>
              );
            })}
          </tbody>
        </Table>
        <div className="d-flex justify-content-center col-3 mx-auto">
          <button
            className="border-none px-3 py-1 mt-4 mb-2 text-white"
            type="button"
            onClick={handleAddQuestion}
            style={{
              fontWeight: "bolder",
              background:
                "radial-gradient(circle at 48.7% 44.3%, rgb(30, 144, 231) 0%, rgb(56, 113, 209) 22.9%, rgb(38, 76, 140) 76.7%, rgb(31, 63, 116) 100.2%)",
            }}
          >
            ADD QUESTION
          </button>
        </div>
      </div>
      <div style={display} className="mt-3">
        <div className="d-flex justify-content-center min-vh-2 text-black">
          <QuestionForm
            fetchQuestions={fetchQuestions}
            buttonName="CREATE"
            submitHandler={submitHandler}
            handleSelectQuesTypeChange={handleSelectQuesTypeChange}
            quesType={quesType}
            setEnum={setEnum}
            handleSelectTopicChange={handleSelectTopicChange}
            topicChange={topicChange}
            topics={topics}
            handleCloseQuestion={handleCloseQuestion}
            changedquestionDetail={changedquestionDetail}
            changedoptionA={changedoptionA}
            changedoptionB={changedoptionB}
            changedoptionC={changedoptionC}
            changedoptionD={changedoptionD}
            changedoptionE={changedoptionE}
            changedanswer={changedanswer}
            changednumAnswers={changednumAnswers}
            changeddifficultyLevel={changeddifficultyLevel}
            changedanswerValue={changedanswerValue}
            changednegativeMarkValue={changednegativeMarkValue}
            changeQuestionDetailHandler={changeQuestionDetailHandler}
            changeOptionAHandler={changeOptionAHandler}
            changeOptionBHandler={changeOptionBHandler}
            changeOptionCHandler={changeOptionCHandler}
            changeOptionDHandler={changeOptionDHandler}
            changeOptionEHandler={changeOptionEHandler}
            changeAnswerHandler={changeAnswerHandler}
            changeNumAnswersHandler={changeNumAnswersHandler}
            changedifficultyLevelHandler={changedifficultyLevelHandler}
            changeanswerValueHandler={changeanswerValueHandler}
            changenegativeMarkHandler={changenegativeMarkHandler}
          />
        </div>
      </div>
    </>
  );
}

export default Question;
