import React, { useEffect, useState } from "react";
import Table from "react-bootstrap/Table";
import ExamTopicMappingForm from "../../Dashboard/Form/ExamTopicMappingForm";
export default function ExamTopicMapping() {
  const [topics, setTopics] = useState([]);
  const [exams, setExams] = useState([]);
  const [examTopic, setExamTopic] = useState([]);
  const [percentage, setPercentage] = useState();
  const [questionsPerExam, setQuestionsPerExam] = useState();
  const [topicChange, setTopicChange] = useState("CHOOSE ONE");
  const [examID, setExamID] = useState("CHOOSE ONE");
  const [message, setMessage] = useState("");
  const [showPercentage, setShowPercentage] = useState("");
  const [questions, setQuestions] = useState("");
  const [reloadComponent, setReloadComponent] = useState(false);

  useEffect(() => {
    fetchTopics();
    fetchExam();
    fetchExamTopicMapping();
  }, []);

  const handleChangePercentage = (e) => {
    if (e.target.value % 5 !== 0 || e.target.value % 10 !== 0) {
      setPercentage(e.target.value);
    } else {
    setPercentage(e.target.value);
    }
    // setQuestionsPerExam(e.target.value);
  };
  const handleSelectTopicChange = (e) => {
    setTopicChange(e.topicId);
  };

  const handleSelectExamChange = (e) => {
    setExamID(e.examId);
  };
  const handleSelectCountChange = (e) => {
    // setQuestionsPerExam()
  };
  const fetchTopics = async () => {
    try {
      const response = await fetch(
        "https://" +
          window.location.hostname +
          ":8443/OnlineExamPortal/control/fetch-topics",
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

  const fetchExam = async () => {
    try {
      const response = await fetch(
        "https://" +
          window.location.hostname +
          ":8443/OnlineExamPortal/control/fetch-exams",
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
      var list = data.ExamInfo.ExamList;
      setExams(list);
    } catch (error) {
      console.log(error);
    }
  };
  console.log(exams);

  const fetchExamTopicMapping = async () => {
    try {
      const response = await fetch(
        "https://" +
          window.location.hostname +
          ":8443/OnlineExamPortal/control/fetch-all-exam-topics",
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
      var list = data.ExamTopicsInfo.ExamTopicList;
      setExamTopic(list);
    } catch (error) {
      console.log(error);
    }
  };
  console.log(exams);

  const calculatePercentage = (e) => {
    e.preventDefault();
    const data_map = {
      examId: examID,
      topicId: topicChange,
      percentage: percentage,
      questionsPerExam: questionsPerExam ? questionsPerExam.toString() : "0",
    };
    console.log(data_map);
    if (examID === "CHOOSE ONE") {
      document.getElementById("examIDerr").style.display = "block";
    } else {
      document.getElementById("examIDerr").style.display = "none";
    }
    if (topicChange === "CHOOSE ONE") {
      document.getElementById("topicIDerr").style.display = "block";
    } else {
      document.getElementById("topicIDerr").style.display = "none";
    }
    if (data_map.percentage === undefined) {
      document.getElementById("percentageerr").style.display = "block";
    } else {
      document.getElementById("percentageerr").style.display = "none";
    }
    try {
      if (!(examID === "CHOOSE ONE" || topicChange === "CHOOSE ONE" || data_map.percentage === undefined)) {
        fetch(
          "https://" +
            window.location.hostname +
            ":8443/OnlineExamPortal/control/calculate-percentage",
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
            // console.log(fetch_data.output);
            var topicQuestionsPerExam = fetch_data.topicQuestionsPerExam;
            console.log(topicQuestionsPerExam);
            // var message = fetch_data.message;
            // setMessage(message);
            console.log(fetch_data.message);
            setMessage(fetch_data.message);
            setShowPercentage(fetch_data.percentage);
            console.log(fetch_data.percentage)
            setQuestions(fetch_data.questions);
            setQuestionsPerExam(topicQuestionsPerExam);
            fetchExamTopicMapping();
          });
      }
    } catch (error) {
      console.log(error);
    }
  };

  const submitHandler = (e) => {
    e.preventDefault();
    var form = document.getElementById("examtopping");
    const formData = new FormData(form);
    const data_map = {
      examId: examID,
      topicId: topicChange,
      percentage: percentage,
      topicPassPercentage: formData.get("topicPassPercentage"),
      questionsPerExam: questionsPerExam ? questionsPerExam.toString() : "0",
    };
    console.log(data_map);
    console.log(examID === "CHOOSE ONE");

    if (examID === "CHOOSE ONE") {
      document.getElementById("examIDerr").style.display = "block";
    } else {
      document.getElementById("examIDerr").style.display = "none";
    }
    if (topicChange === "CHOOSE ONE") {
      document.getElementById("topicIDerr").style.display = "block";
    } else {
      document.getElementById("topicIDerr").style.display = "none";
    }
    if (data_map.percentage === undefined) {
      document.getElementById("percentageerr").style.display = "block";
    } else {
      document.getElementById("percentageerr").style.display = "none";
    }
    if (data_map.topicPassPercentage === "") {
      document.getElementById("topicpasspercentageerr").style.display = "block";
    } else {
      document.getElementById("topicpasspercentageerr").style.display = "none";
    }
    if (
      !(
        (
          examID === "CHOOSE ONE" ||
          topicChange === "CHOOSE ONE" ||
          data_map.percentage === undefined ||
          data_map.topicPassPercentage === ""
        )
      )
    ) {
      try {
        fetch(
          "https://" +
            window.location.hostname +
            ":8443/OnlineExamPortal/control/create-topic-for-exam",
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
            console.log(fetch_data.message);
            var message = fetch_data.message;
            setMessage(message);
            setShowPercentage(fetch_data.percentage);
            setQuestions(fetch_data.questions);
            setPercentage("");
            setQuestionsPerExam("");
            fetchExamTopicMapping();
            setExamID("CHOOSE ONE");
            setTopicChange("CHOOSE ONE");
            setReloadComponent(!reloadComponent);
            console.log(reloadComponent);
          });
      } catch (error) {
        console.log(error);
      }
    }
    form.reset();
  };
  if (examTopic === undefined) {
    <div className="d-flex justify-content-center min-vh-2 text-black">
      <ExamTopicMappingForm
        questions={questions}
        message={message}
        percentage={percentage}
        topics={topics}
        submitHandler={submitHandler}
        examID={examID}
        topicChange={topicChange}
        questionsPerExam={questionsPerExam}
        exams={exams}
        showPercentage={showPercentage}
        handleSelectTopicChange={handleSelectTopicChange}
        handleSelectExamChange={handleSelectExamChange}
        handleSelectCountChange={handleSelectCountChange}
        handleChangePercentage={handleChangePercentage}
        calculatePercentage={calculatePercentage}
      />
    </div>;
  }
  if (examTopic === undefined || examTopic.length === 0)
    return (
      <>
        <ExamTopicMappingForm
          questions={questions}
          message={message}
          percentage={percentage}
          topics={topics}
          submitHandler={submitHandler}
          examID={examID}
          topicChange={topicChange}
          questionsPerExam={questionsPerExam}
          exams={exams}
          showPercentage={showPercentage}
          handleSelectTopicChange={handleSelectTopicChange}
          handleSelectExamChange={handleSelectExamChange}
          handleSelectCountChange={handleSelectCountChange}
          handleChangePercentage={handleChangePercentage}
          calculatePercentage={calculatePercentage}
        />
      </>
    );
  return (
    <div>
      <Table responsive>
        <thead>
          <tr>
            <th>Exam ID</th>
            <th>Topic ID</th>
            <th>Percentage</th>
            <th>Topic Pass Percentage</th>
            <th>Questions Per Exam</th>
          </tr>
        </thead>
        <tbody>
          {examTopic.map((examtopic, index) => {
            return (
              <tr key={index}>
                <td>{examtopic.examId}</td>
                <td>{examtopic.topicId}</td>
                <td>{examtopic.percentage}</td>
                <td>{examtopic.topicPassPercentage}</td>
                <td>{examtopic.questionsPerExam}</td>
              </tr>
            );
          })}
        </tbody>
      </Table>
      <div className="d-flex justify-content-center min-vh-2 text-black">
        <ExamTopicMappingForm
          questions={questions}
          message={message}
          percentage={percentage}
          topics={topics}
          submitHandler={submitHandler}
          examID={examID}
          topicChange={topicChange}
          questionsPerExam={questionsPerExam}
          exams={exams}
          showPercentage={showPercentage}
          handleSelectTopicChange={handleSelectTopicChange}
          handleSelectExamChange={handleSelectExamChange}
          handleSelectCountChange={handleSelectCountChange}
          handleChangePercentage={handleChangePercentage}
          calculatePercentage={calculatePercentage}
        />
      </div>
    </div>
  );
}
