import React, { useEffect, useState } from "react";
import Table from "react-bootstrap/Table";
import ExamTopicMappingForm from "../../Dashboard/Form/ExamTopicMappingForm";
export default function ExamTopicMapping() {
  const [topics, setTopics] = useState([]);
  const [exams, setExams] = useState([]);
  const [examTopic, setExamTopic] = useState([]);
  const [percentage, setPercentage] = useState();
  const [selectedQuestionsPerExam, setSelectedQuestionsPerExam] = useState();
  const [topicChange, setTopicChange] = useState("");
  const [examChange, setExamChange] = useState("");
  const [count, setCountChange] = useState();

  useEffect(() => {
    fetchTopics();
    fetchExam();
    fetchExamTopicMapping();
  }, []);
  
  const handleChangePercentage = (e) => {
    setSelectedQuestionsPerExam(e.target.value)
  }
  const handleSelectTopicChange = (e) => {
    setTopicChange(e.topicId);
  };

  const handleSelectExamChange = (e) => {
    setExamChange(e.examId);
  };
  const handleSelectCountChange = (e) => {
    setCountChange(e.target.value);
  };
  const fetchTopics = async () => {
    try {
      const response = await fetch(
        "https://localhost:8443/OnlineExamPortal/control/FetchTopicMaster",
        {
          method: "POST",
          credentials: "include",
        }
      );
      if (!response.ok) {
        throw new Error();
      }
      const data = await response.json();
      console.log(data);
      var list = data.TopicMaster;
      setTopics(list);
    } catch (error) {
      console.log(error);
    }
  };
  console.log(topics);

  const fetchExam = async () => {
    try {
      const response = await fetch(
        "https://localhost:8443/OnlineExamPortal/control/FetchExamMaster",
        {
          method: "POST",
          credentials: "include",
        }
      );
      if (!response.ok) {
        throw new Error();
      }
      const data = await response.json();
      console.log(data);
      var list = data.ExamMaster;
      setExams(list);
    } catch (error) {
      console.log(error);
    }
  };
  console.log(exams);

  const fetchExamTopicMapping = async () => {
    try {
      const response = await fetch(
        "https://localhost:8443/OnlineExamPortal/control/FetchExamTopicMapping",
        {
          method: "POST",
          credentials: "include",
        }
      );
      if (!response.ok) {
        throw new Error();
      }
      const data = await response.json();
      console.log(data);
      var list = data.ExamTopicMapping;
      setExamTopic([...list]);
    } catch (error) {
      console.log(error);
    }
  };
  console.log(exams);



  const submitHandler = (e) => {
    e.preventDefault();
    var form = document.getElementById("examtopping");
    const formData = new FormData(form);
    const data_map = {
      examId: examChange,
      topicId: topicChange,
      percentage: percentage,
      topicPassPercentage: formData.get("topicPassPercentage"),
      questionsPerExam:  selectedQuestionsPerExam,
    };
    console.log(data_map);

    if (data_map.examId === "") {
      document.getElementById("examIDerr").style.display = "block";
    } else {
      document.getElementById("examIDerr").style.display = "none";
    }
    if (data_map.topicId === "") {
      document.getElementById("topicIDerr").style.display = "block";
    } else {
      document.getElementById("topicIDerr").style.display = "none";
    }
    if (data_map.percentage === "") {
      document.getElementById("percentageerr").style.display = "block";
    } else {
      document.getElementById("percentageerr").style.display = "none";
    }
    if (data_map.topicPassPercentage === "") {
      document.getElementById("topicpasspercentageerr").style.display = "block";
    } else {
      document.getElementById("topicpasspercentageerr").style.display = "none";
    }
    if (data_map.questionsPerExam === "") {
      document.getElementById("questionsperexamerr").style.display = "block";
    } else {
      document.getElementById("questionsperexamerr").style.display = "none";
    }
    if (
      !(
        data_map.examId === "" ||
        data_map.topicId === "" ||
        data_map.percentage === "" ||
        data_map.topicPassPercentage === "" ||
        data_map.questionsPerExam === ""
      )
    ) {
      try {
        fetch(
          "https://localhost:8443/OnlineExamPortal/control/CreateExamTopicMapping",
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
            fetchExamTopicMapping();
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
        topics={topics}
        submitHandler={submitHandler}
        examChange={examChange}
        selectedQuestionsPerExam={selectedQuestionsPerExam}
        count={count}
        exams={exams}
        examId={examTopic.examId}
        handleSelectTopicChange={handleSelectTopicChange}
        handleSelectExamChange={handleSelectExamChange}
        handleSelectCountChange={handleSelectCountChange}
        handleChangePercentage={handleChangePercentage}
      />
    </div>
  }

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
          topics={topics}
          submitHandler={submitHandler}
          examChange={examChange}
          selectedQuestionsPerExam={selectedQuestionsPerExam}
          count={count}
          exams={exams}
          examId={examTopic.examId}
          handleSelectTopicChange={handleSelectTopicChange}
          handleSelectExamChange={handleSelectExamChange}
          handleSelectCountChange={handleSelectCountChange}
          handleChangePercentage={handleChangePercentage}
        />
      </div>
    </div>
  );
}
